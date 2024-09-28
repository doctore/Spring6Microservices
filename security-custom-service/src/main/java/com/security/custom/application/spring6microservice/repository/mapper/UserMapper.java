package com.security.custom.application.spring6microservice.repository.mapper;

import com.security.custom.application.spring6microservice.enums.Permissions;
import com.security.custom.application.spring6microservice.enums.Roles;
import com.security.custom.application.spring6microservice.model.Role;
import com.security.custom.application.spring6microservice.model.User;
import com.spring6microservices.common.core.util.MapUtil;
import lombok.experimental.UtilityClass;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static com.security.custom.application.spring6microservice.repository.UserRepository.PERMISSION_NAME_COLUMN;
import static com.security.custom.application.spring6microservice.repository.UserRepository.ROLE_ID_COLUMN;
import static com.security.custom.application.spring6microservice.repository.UserRepository.ROLE_NAME_COLUMN;
import static com.security.custom.application.spring6microservice.repository.UserRepository.USER_ACTIVE_COLUMN;
import static com.security.custom.application.spring6microservice.repository.UserRepository.USER_ID_COLUMN;
import static com.security.custom.application.spring6microservice.repository.UserRepository.USER_NAME_COLUMN;
import static com.security.custom.application.spring6microservice.repository.UserRepository.USER_PASSWORD_COLUMN;
import static com.security.custom.application.spring6microservice.repository.UserRepository.USER_USERNAME_COLUMN;
import static java.util.Optional.ofNullable;

@UtilityClass
public class UserMapper {

    /**
     *    Using provided {@link ResultSetExtractor} {@code resultSet} returns a new {@link User} instance containing
     * the related {@link Role} and {@link Permissions}.
     *
     * @throws IllegalArgumentException if stored role name in database has no constant with the specified name in {@link Roles}
     *                                  or its related permissions in database has no constant with the specified name in {@link Permissions}
     */
    public static final ResultSetExtractor<User> userWithRolesAndPermissionsResultExtractor = (resultSet) -> {
        Map<Integer, Role> roleMap = new HashMap<>();
        User user = null;
        while (resultSet.next()) {
            if (resultSet.isFirst()) {
                user = new User(
                        resultSet.getLong(USER_ID_COLUMN),
                        resultSet.getString(USER_NAME_COLUMN),
                        resultSet.getString(USER_USERNAME_COLUMN),
                        resultSet.getString(USER_PASSWORD_COLUMN),
                        resultSet.getBoolean(USER_ACTIVE_COLUMN),
                        new HashSet<>()
                );
            }
            updateRoleAndPermissions(
                    roleMap,
                    resultSet
            );
        }
        return ofNullable(user)
                .map(u -> {
                    u.getRoles().addAll(
                            roleMap.values()
                    );
                    return u;
                })
                .orElse(null);
    };


    private static void updateRoleAndPermissions(final Map<Integer, Role> roleMap,
                                                 final ResultSet resultSet) throws SQLException {
        Role role = MapUtil.getOrElse(
                roleMap,
                resultSet.getInt(ROLE_ID_COLUMN),
                new Role(
                        resultSet.getInt(ROLE_ID_COLUMN),
                        resultSet.getString(ROLE_NAME_COLUMN)
                )
        );
        role.addPermission(
                Permissions.valueOf(
                        resultSet.getString(PERMISSION_NAME_COLUMN)
                )
        );
        roleMap.put(
                role.getId(),
                role
        );
    }

}
