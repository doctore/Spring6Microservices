package com.security.oauth.repository;

import com.security.oauth.model.Role;
import com.security.oauth.model.User;
import com.security.oauth.model.enums.PermissionEnum;
import com.security.oauth.repository.mapper.UserMapper;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

import static com.security.oauth.configuration.persistence.PersistenceConfiguration.TABLE;
import static java.util.Optional.ofNullable;

@Repository
public class UserRepository {

    public static String USER_ACTIVE_COLUMN = "active";
    public static String USER_ID_COLUMN = "id";
    public static String USER_NAME_COLUMN = "name";
    public static String USER_PASSWORD_COLUMN = "password";
    public static String USER_USERNAME_COLUMN = "username";
    public static String USER_CREATED_AT_COLUMN = "createdAt";
    public static String ROLE_ID_COLUMN = "roleId";
    public static String ROLE_NAME_COLUMN = "roleName";
    public static String PERMISSION_NAME_COLUMN = "permissionName";

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;


    @Autowired
    public UserRepository(final NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }


    /**
     *    Gets the {@link User} (including its {@link Role}s and {@link PermissionEnum}) which {@link User#getUsername()}
     * matches with the given one.
     *
     * @param username
     *    Username to search a coincidence in {@link User#getUsername()}
     *
     * @return {@link Optional} with the {@link User} which {@link User#getUsername()} matches with the given one,
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
                                     + ", u.created_at as " + USER_CREATED_AT_COLUMN
                                     + ", r.id as " + ROLE_ID_COLUMN
                                     + ", r.name as " + ROLE_NAME_COLUMN
                                     + ", p.name as " + PERMISSION_NAME_COLUMN + " "
                              + "from " + TABLE.USER + " u "
                              + "join " + TABLE.USER_ROLE + " ur on (ur.user_id = u.id) "
                              + "join " + TABLE.ROLE + " r on (r.id = ur.role_id) "
                              + "join " + TABLE.ROLE_PERMISSION + " rp on (rp.role_id = r.id) "
                              + "join " + TABLE.PERMISSION + " p on (p.id = rp.permission_id) "
                              + "where u.username = :username",
                           Map.of(
                                   USER_USERNAME_COLUMN,
                                   username
                           ),
                           UserMapper.userWithRolesAndPermissionsResultExtractor
                        )
                );
    }

}
