package com.security.custom.model;

import com.security.custom.configuration.persistence.PersistenceConfiguration;
import com.security.custom.enums.SecurityHandler;
import com.security.custom.enums.token.TokenEncryptionAlgorithm;
import com.security.custom.enums.token.TokenEncryptionMethod;
import com.security.custom.enums.token.TokenSignatureAlgorithm;
import com.security.custom.enums.token.TokenType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

@AllArgsConstructor
@Builder
@Entity
@Getter
@NoArgsConstructor
@Setter
@Table(
        name = PersistenceConfiguration.TABLE.APPLICATION_CLIENT_DETAILS,
        schema = PersistenceConfiguration.SCHEMA
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
    private SecurityHandler securityHandler;

    /**
     *    {@link ApplicationClientDetails#encryptionAlgorithm}, {@link ApplicationClientDetails#encryptionMethod} and
     * {@link ApplicationClientDetails#encryptionSecret} must be {@code null} if its value is:
     * <ul>
     *     <li>{@link TokenType#JWS}</li>
     *     <li>{@link TokenType#ENCRYPTED_JWS}</li>
     * </ul>
     * <p>
     *    {@link ApplicationClientDetails#encryptionAlgorithm}, {@link ApplicationClientDetails#encryptionMethod} and
     * {@link ApplicationClientDetails#encryptionSecret} must be not {@code null} if its value is:
     * <ul>
     *     <li>{@link TokenType#JWE}</li>
     *     <li>{@link TokenType#ENCRYPTED_JWE}</li>
     * </ul>
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    private TokenType tokenType;

    @Enumerated(EnumType.STRING)
    private TokenEncryptionAlgorithm encryptionAlgorithm;

    /**
     * It must be {@code null} if {@link ApplicationClientDetails#encryptionAlgorithm} is {@code null}.
     */
    @Enumerated(EnumType.STRING)
    private TokenEncryptionMethod encryptionMethod;

    /**
     * It must be {@code null} if {@link ApplicationClientDetails#encryptionAlgorithm} is {@code null}.
     */
    private String encryptionSecret;

    @NotNull
    private int accessTokenValidityInSeconds;

    @NotNull
    private Integer refreshTokenValidityInSeconds;

    @NotNull
    private LocalDateTime createdAt;


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


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ApplicationClientDetails that = (ApplicationClientDetails) o;
        return Objects.equals(
                id,
                that.id
        );
    }


    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

}
