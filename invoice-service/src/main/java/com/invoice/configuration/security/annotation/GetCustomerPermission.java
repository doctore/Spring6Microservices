package com.invoice.configuration.security.annotation;

import com.invoice.configuration.Constants;
import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Retention(RUNTIME)
@Target({
        METHOD,
        TYPE
})
@PreAuthorize(
        "hasAuthority('" + Constants.PERMISSIONS.GET_CUSTOMER +"')"
)
public @interface GetCustomerPermission {
}
