package com.security.custom.model;

import com.security.custom.configuration.Constants;
import com.security.custom.enums.AuthenticationConfiguration;
import com.security.custom.enums.token.TokenEncryptionAlgorithm;
import com.security.custom.enums.token.TokenEncryptionMethod;
import com.security.custom.enums.token.TokenSignatureAlgorithm;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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

@AllArgsConstructor
@Builder
@Data
@Entity
@EqualsAndHashCode(of = {"id"})
@NoArgsConstructor
@Table(
        name = "application_client_details",
        schema = Constants.DATABASE_SCHEMA.SECURITY
)
public class ApplicationClientDetails implements UserDetails {

    @Serial
    private static final long serialVersionUID = -171319389828209358L;

    @Id
    @NotNull
    @Size(min = 1, max = 64)
    private String id;

    @NotNull
    @Size(min = 1, max = 256)
    private String applicationClientSecret;

    @NotNull
    @Enumerated(EnumType.STRING)
    private TokenSignatureAlgorithm signatureAlgorithm;

    @NotNull
    private String signatureSecret;

    @NotNull
    @Enumerated(EnumType.STRING)
    private AuthenticationConfiguration authenticationGenerator;

    /**
     *    If {@code null} then only JWS token will be used. In this case: {@link ApplicationClientDetails#encryptionMethod}
     * and {@link ApplicationClientDetails#encryptionSecret} must be {@code null} too.
     */
    @Enumerated(EnumType.STRING)
    private TokenEncryptionAlgorithm encryptionAlgorithm;

    /**
     *    If {@code null} then only JWS token will be used. In this case: {@link ApplicationClientDetails#encryptionAlgorithm}
     * and {@link ApplicationClientDetails#encryptionSecret} must be {@code null} too.
     */
    @Enumerated(EnumType.STRING)
    private TokenEncryptionMethod encryptionMethod;

    /**
     *    If {@code null} then only JWS token will be used. In this case: {@link ApplicationClientDetails#encryptionAlgorithm}
     * and {@link ApplicationClientDetails#encryptionMethod} must be {@code null} too.
     */
    private String encryptionSecret;

    @NotNull
    private int accessTokenValidityInSeconds;

    /**
     * If {@code null} then only access token will be generated and trying to refresh it will generate an error.
     */
    private int refreshTokenValidityInSeconds;


    @Override
    public String getUsername() {
        return id;
    }

    @Override
    public String getPassword() {
        return applicationClientSecret;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new HashSet<>();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
