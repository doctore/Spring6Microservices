package com.security.custom.application.spring6microservice.repository.mapper;

import com.security.custom.application.spring6microservice.enums.Permissions;
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

import static java.util.Optional.ofNullable;

@UtilityClass
public class UserMapper {

    /**
     *    Using provided {@link ResultSetExtractor} {@code resultSet} returns a new {@link User} instance containing
     * the related {@link Role} and {@link Permissions}.
     */
    public static final ResultSetExtractor<User> userWithRolesAndPermissionsResultExtractor = (resultSet) -> {
        Map<Integer, Role> roleMap = new HashMap<>();
        User user = null;
        while (resultSet.next()) {
            if (resultSet.isFirst()) {
                user = new User(
                        resultSet.getLong("id"),
                        resultSet.getString("name"),
                        resultSet.getString("username"),
                        resultSet.getString("password"),
                        resultSet.getBoolean("active"),
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
                resultSet.getInt("roleId"),
                new Role(
                        resultSet.getInt("roleId"),
                        resultSet.getString("roleName")
                )
        );
        role.addPermission(
                Permissions.valueOf(
                        resultSet.getString("permissionName")
                )
        );
        roleMap.put(
                role.getId(),
                role
        );
    }

}
