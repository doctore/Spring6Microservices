package com.security.custom.application.spring6microservice.model;

import com.security.custom.application.spring6microservice.model.enums.PermissionEnum;
import com.spring6microservices.common.core.util.CollectionUtil;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static com.spring6microservices.common.core.util.CollectionUtil.addIfNotNull;
import static com.spring6microservices.common.core.util.ObjectUtil.getOrElse;

@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode(of = {"username"})
@NoArgsConstructor
public class User implements UserDetails {

    @Serial
    private static final long serialVersionUID = -2635894377988063111L;

    private Long id;

    @NotNull
    @Size(min = 1, max = 128)
    private String name;

    @NotNull
    @Size(min = 1, max = 64)
    private String username;

    @NotNull
    @Size(min = 1, max = 128)
    private String password;

    @NotNull
    private boolean active;

    @NotNull
    private LocalDateTime createdAt;

    private Set<Role> roles;


    /**
     * Adds a new {@link Role} to the current {@link User}.
     *
     * @param role
     *    {@link Role} to add
     *
     * @return {@code true} if {@code role} is not {@code null} and was added to {@link User#getRoles()},
     *         {@code false} otherwise
     */
    public boolean addRole(final Role role) {
        this.roles = getOrElse(
                this.roles,
                new HashSet<>()
        );
        return addIfNotNull(
                this.roles,
                role
        );
    }


    /**
     * Get {@link PermissionEnum} and add them to a {@link Set} of {@link GrantedAuthority}
     *
     * @return {@link Set} of {@link GrantedAuthority}
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> result = new HashSet<>();
        if (!CollectionUtil.isEmpty(roles)) {
            for (Role role: roles) {
                result.add(
                        new SimpleGrantedAuthority(
                                role.getName().name()
                        )
                );
                if (!CollectionUtil.isEmpty(role.getPermissions())) {
                    for (PermissionEnum permission: role.getPermissions()) {
                        result.add(
                                new SimpleGrantedAuthority(
                                        permission.name()
                                )
                        );
                    }
                }
            }
        }
        return result;
    }


    @Override
    public boolean isAccountNonExpired() {
        return active;
    }


    @Override
    public boolean isAccountNonLocked() {
        return active;
    }


    @Override
    public boolean isCredentialsNonExpired() {
        return active;
    }


    @Override
    public boolean isEnabled() {
        return active;
    }

}
