package com.security.oauth.model;

import com.security.oauth.model.enums.PermissionEnum;
import com.security.oauth.model.enums.RoleEnum;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import static com.spring6microservices.common.core.util.CollectionUtil.addIfNotNull;
import static com.spring6microservices.common.core.util.ObjectUtil.getOrElse;

@Data
@EqualsAndHashCode(of = {"name"})
@NoArgsConstructor
public class Role implements Serializable {

    @Serial
    private static final long serialVersionUID = -2934897377980463123L;

    private Integer id;

    @NotNull
    @Size(min =1 , max = 128)
    private RoleEnum name;

    private Set<PermissionEnum> permissions;


    public Role(final Integer id,
                final String name) {
        this.id = id;
        this.name = RoleEnum.valueOf(name);
        this.permissions = new HashSet<>();
    }


    /**
     * Adds a new {@link PermissionEnum} to the current {@link Role}.
     *
     * @param permission
     *    {@link PermissionEnum} to add
     *
     * @return {@code true} if {@code permission} is not {@code null} and was added to {@link Role#getPermissions()},
     *         {@code false} otherwise
     */
    public boolean addPermission(final PermissionEnum permission) {
        this.permissions = getOrElse(
                this.permissions,
                new HashSet<>()
        );
        return addIfNotNull(
                this.permissions,
                permission
        );
    }

}
