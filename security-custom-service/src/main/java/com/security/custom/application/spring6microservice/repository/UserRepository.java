package com.security.custom.application.spring6microservice.repository;

import com.security.custom.application.spring6microservice.configuration.Spring6MicroserviceConstants;
import com.security.custom.application.spring6microservice.enums.Permissions;
import com.security.custom.application.spring6microservice.model.Role;
import com.security.custom.application.spring6microservice.model.User;
import com.security.custom.application.spring6microservice.repository.mapper.UserMapper;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@Repository(value = Spring6MicroserviceConstants.SPRING6MICROSERVICE_APPLICATION_NAME + "UserRepository")
public class UserRepository {

    public static String USER_ACTIVE_COLUMN = "active";
    public static String USER_ID_COLUMN = "id";
    public static String USER_NAME_COLUMN = "name";
    public static String USER_PASSWORD_COLUMN = "password";
    public static String USER_USERNAME_COLUMN = "username";
    public static String ROLE_ID_COLUMN = "roleId";
    public static String ROLE_NAME_COLUMN = "roleName";
    public static String PERMISSION_NAME_COLUMN = "permissionName";

    private final String USER_TABLE = "security.spring6microservice_user";
    private final String USER_ROLE_TABLE = "security.spring6microservice_user_role";
    private final String ROLE_TABLE = "security.spring6microservice_role";
    private final String ROLE_PERMISSION_TABLE = "security.spring6microservice_role_permission";
    private final String PERMISSION_TABLE = "security.spring6microservice_permission";

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;


    @Autowired
    public UserRepository(@Lazy final NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }


    /**
     *    Gets the {@link User} (including its {@link Role}s and {@link Permissions}) which {@link User#getUsername()}
     * matches with the given one.
     *
     * @param username
     *    Username to search a coincidence in {@link User#getUsername()}
     *
     * @return {@link Optional} with the {@link User} which {@link User#getUsername()} matches with the given one.
     *         {@link Optional#empty()} otherwise.
     */
    public Optional<User> findByUsername(@Nullable String username) {
        return ofNullable(username)
                .map(u ->
                        namedParameterJdbcTemplate.query(
                                "select u.id as " + USER_ID_COLUMN
                                     + ", u.name as " + USER_NAME_COLUMN
                                     + ", u.username as " + USER_USERNAME_COLUMN
                                     + ", u.password as " + USER_PASSWORD_COLUMN
                                     + ", u.active as " + USER_ACTIVE_COLUMN
                                     + ", r.id as " + ROLE_ID_COLUMN
                                     + ", r.name as " + ROLE_NAME_COLUMN
                                     + ", p.name as " + PERMISSION_NAME_COLUMN + " "
                              + "from " + USER_TABLE + " u "
                              + "join " + USER_ROLE_TABLE + " ur on (ur.user_id = u.id) "
                              + "join " + ROLE_TABLE + " r on (r.id = ur.role_id) "
                              + "join " + ROLE_PERMISSION_TABLE + " rp on (rp.role_id = r.id) "
                              + "join " + PERMISSION_TABLE + " p on (p.id = rp.permission_id) "
                              + "where u.username = :username",
                           Map.of(USER_USERNAME_COLUMN, username),
                           UserMapper.userWithRolesAndPermissionsResultExtractor
                        )
                );
    }

}
