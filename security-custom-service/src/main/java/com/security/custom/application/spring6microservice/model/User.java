package com.security.custom.application.spring6microservice.model;

import com.security.custom.application.spring6microservice.enums.Permissions;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static com.spring6microservices.common.core.util.CollectionUtil.addIfNotNull;
import static com.spring6microservices.common.core.util.ObjectUtil.getOrElse;
import static java.util.Optional.ofNullable;

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
    @Size(min =1 , max = 128)
    private String name;

    @NotNull
    @Size(min = 1, max = 64)
    private String username;

    @NotNull
    @Size(min = 1, max = 128)
    private String password;

    @NotNull
    private boolean active;

    private Set<Role> roles;


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
     * Get {@link Permissions} and add them to a {@link Set} of {@link GrantedAuthority}
     *
     * @return {@link Set} of {@link GrantedAuthority}
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        // TODO: PENDING TO DEVELOP
        return new HashSet<>();
        /*
        return ofNullable(roles)
                .map(rList -> rList.stream()
                        .map(r -> new SimpleGrantedAuthority(r.name()))
                        .collect(toSet()))
                .orElseGet(HashSet::new);
                 */
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
