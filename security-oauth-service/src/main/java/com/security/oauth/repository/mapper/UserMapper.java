package com.security.oauth.repository.mapper;

import com.security.oauth.model.Role;
import com.security.oauth.model.User;
import com.security.oauth.model.enums.PermissionEnum;
import com.security.oauth.model.enums.RoleEnum;
import com.spring6microservices.common.core.util.MapUtil;
import lombok.experimental.UtilityClass;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static com.security.oauth.repository.UserRepository.*;
import static java.util.Optional.ofNullable;

@UtilityClass
public class UserMapper {

    /**
     *    Using provided {@link ResultSetExtractor} {@code resultSet} returns a new {@link User} instance containing
     * the related {@link Role} and {@link PermissionEnum}.
     *
     * @throws IllegalArgumentException if stored role name in database has no constant with the specified name in {@link RoleEnum}
     *                                  or its related permissions in database has no constant with the specified name in {@link PermissionEnum}
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
                        resultSet.getObject(USER_CREATED_AT_COLUMN, LocalDateTime.class),
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
                PermissionEnum.valueOf(
                        resultSet.getString(PERMISSION_NAME_COLUMN)
                )
        );
        roleMap.put(
                role.getId(),
                role
        );
    }

}
