package com.security.custom.service.token.provider;

import com.security.custom.enums.token.TokenType;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * The annotated element must be included in the selected {@link TokenType}.
 */
@Documented
@Retention(RUNTIME)
@Target({TYPE})
public @interface TokenTypeProvider {

    TokenType value();

}
