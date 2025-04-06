package com.security.oauth.repository;

import com.security.oauth.configuration.persistence.PersistenceConfiguration;
import com.security.oauth.service.cache.RegisteredClientCacheService;
import com.spring6microservices.common.core.util.AssertUtil;
import com.spring6microservices.common.core.util.StringUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

/**
 * Manages the Oauth client information, allowing to define a custom database table to store related data.
 */
@Log4j2
@Repository
public class CustomJdbcRegisteredClientRepository extends JdbcRegisteredClientRepository {

    private static final String COLUMN_NAMES = "id"
        + ", client_id"
        + ", client_id_issued_at"
        + ", client_secret"
        + ", client_secret_expires_at"
        + ", client_name"
        + ", client_authentication_methods"
        + ", authorization_grant_types"
        + ", redirect_uris"
        + ", post_logout_redirect_uris"
        + ", scopes"
        + ", client_settings"
        + ", token_settings";

    private static final String TABLE_NAME = PersistenceConfiguration.SCHEMA + ".oauth2_registered_client";

    private static final String PK_FILTER = "id = ?";

    private static final String LOAD_REGISTERED_CLIENT_SQL = "SELECT " + COLUMN_NAMES
            + " FROM " + TABLE_NAME
            + " WHERE ";

    private static final String INSERT_REGISTERED_CLIENT_SQL = "INSERT INTO " + TABLE_NAME + "(" + COLUMN_NAMES + ") "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String UPDATE_REGISTERED_CLIENT_SQL = "UPDATE " + TABLE_NAME
            + " SET client_secret = ? "
            + ", client_secret_expires_at = ? "
            + ", client_name = ? "
            + ", client_authentication_methods = ? "
            + ", authorization_grant_types = ? "
            + ", redirect_uris = ? "
            + ", post_logout_redirect_uris = ? "
            + ", scopes = ? "
            + ", client_settings = ? "
            + ", token_settings = ? "
            + " WHERE " + PK_FILTER;

    private static final String COUNT_REGISTERED_CLIENT_SQL = "SELECT COUNT(*) "
            + "FROM " + TABLE_NAME
            + " WHERE ";


    private final RegisteredClientCacheService cacheService;


    public CustomJdbcRegisteredClientRepository(final JdbcOperations jdbcOperations,
                                                final RegisteredClientCacheService cacheService) {
        super(jdbcOperations);
        this.cacheService = cacheService;
    }


    @Override
    public RegisteredClient findById(final String id) {
        AssertUtil.hasText(id, "id cannot be empty");
        return cacheService.get(id)
                .orElseGet(() -> {
                    RegisteredClient registeredClient = findBy(
                            "id = ?",
                            id
                    );
                    if (null != registeredClient) {
                        cacheService.put(
                                id,
                                registeredClient
                        );
                    }
                    return registeredClient;
                });
    }


    @Override
    public RegisteredClient findByClientId(final String clientId) {
        AssertUtil.hasText(clientId, "clientId cannot be empty");
        return cacheService.get(clientId)
                .orElseGet(() -> {
                    RegisteredClient registeredClient = findBy(
                            "client_id = ?",
                            clientId
                    );
                    if (null != registeredClient) {
                        cacheService.put(
                                registeredClient.getId(),
                                registeredClient
                        );
                    }
                    return registeredClient;
                });
    }


    @Override
    public void save(final RegisteredClient registeredClient) {
        AssertUtil.notNull(registeredClient, "registeredClient cannot be null");
        RegisteredClient existingRegisteredClient = findBy(
                PK_FILTER,
                registeredClient.getId()
        );
        if (null != existingRegisteredClient) {
            updateRegisteredClient(
                    registeredClient
            );
        }
        else {
            insertRegisteredClient(
                    registeredClient
            );
        }
        cacheService.put(
                registeredClient.getId(),
                registeredClient
        );
    }


    /**
     * Returns the {@link RegisteredClient} that matches with provided {@code filter} and {@code args} as their values.
     *
     * @param filter
     *    {@link String} with the SQL conditions to find
     * @param args
     *    {@link Object} with the values related with {@code filter}
     *
     * @return {@link RegisteredClient} related with the given {@code filter} and {@code args},
     *         {@code null} if no one was found
     *
     * @throws DataAccessException if there was an error getting the {@link RegisteredClient}
     */
    private RegisteredClient findBy(final String filter,
                                    final Object... args) {
        List<RegisteredClient> result = this.getJdbcOperations()
                .query(
                        LOAD_REGISTERED_CLIENT_SQL + filter,
                        this.getRegisteredClientRowMapper(),
                        args
                );
        return !result.isEmpty()
                ? result.getFirst()
                : null;
    }


    /**
     * Updates an existing {@link RegisteredClient} in database.
     *
     * @param registeredClient
     *    {@link RegisteredClient} to update
     *
     * @throws DataAccessException if there was an error updating {@code registeredClient}
     */
    private void updateRegisteredClient(final RegisteredClient registeredClient) {
        List<SqlParameterValue> parameters = new ArrayList<>(
                this.getRegisteredClientParametersMapper()
                        .apply(
                                registeredClient
                        )
        );
        SqlParameterValue id = parameters.removeFirst();
        parameters.removeFirst();   // remove client_id
        parameters.removeFirst();   // remove client_id_issued_at
        parameters.add(id);
        PreparedStatementSetter pss = new ArgumentPreparedStatementSetter(
                parameters.toArray()
        );
        this.getJdbcOperations()
                .update(
                        UPDATE_REGISTERED_CLIENT_SQL,
                        pss
                );
    }


    /**
     * Adds a new {@link RegisteredClient} in database.
     *
     * @param registeredClient
     *    {@link RegisteredClient} to insert
     *
     * @throws DataAccessException if there was an error adding {@code registeredClient}
     */
    private void insertRegisteredClient(final RegisteredClient registeredClient) {
        assertUniqueIdentifiers(
                registeredClient
        );
        List<SqlParameterValue> parameters = this.getRegisteredClientParametersMapper()
                .apply(
                        registeredClient
                );
        PreparedStatementSetter pss = new ArgumentPreparedStatementSetter(
                parameters.toArray()
        );
        this.getJdbcOperations()
                .update(
                        INSERT_REGISTERED_CLIENT_SQL,
                        pss
                );
    }


    /**
     *    Verifies if there is a {@link RegisteredClient} in database with the same {@link RegisteredClient#getId()}
     * and/or {@link RegisteredClient#getClientSecret()} than {@code registeredClient}.
     *
     * @param registeredClient
     *    {@link RegisteredClient} to check that there is no one else with the same {@link RegisteredClient#getId()}
     *     and/or {@link RegisteredClient#getClientSecret()}
     *
     * @throws IllegalArgumentException if there is a with the same {@link RegisteredClient#getId()}
     *                                  and/or {@link RegisteredClient#getClientSecret()} than provided one
     */
    private void assertUniqueIdentifiers(final RegisteredClient registeredClient) {
        Integer count = this.getJdbcOperations()
                .queryForObject(
                        COUNT_REGISTERED_CLIENT_SQL + "client_id = ?",
                        Integer.class,
                        registeredClient.getClientId()
                );
        if (count != null && count > 0) {
            throw new IllegalArgumentException(
                    format("Registered client must be unique. Found duplicate client identifier: %s",
                            registeredClient.getClientId()
                    )
            );
        }
        else {
            if (!StringUtil.isBlank(registeredClient.getClientSecret())) {
                count = this.getJdbcOperations()
                        .queryForObject(
                                COUNT_REGISTERED_CLIENT_SQL + "client_secret = ?",
                                Integer.class,
                                registeredClient.getClientSecret()
                        );
                if (count != null && count > 0) {
                    throw new IllegalArgumentException(
                            format("Registered client must be unique. Found duplicate client secret for identifier: %s",
                                    registeredClient.getClientId()
                            )
                    );
                }
            }
        }
    }

}
