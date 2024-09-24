package com.security.custom.application.spring6microservice.model;

import com.security.custom.application.spring6microservice.enums.Permissions;
import com.security.custom.application.spring6microservice.enums.Roles;
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
    private Roles name;


    public Role(final Integer id,
                final String name) {
        this.id = id;
        this.name = Roles.valueOf(name);
    }

    private Set<Permissions> permissions;


    public boolean addPermission(final Permissions permission) {
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
