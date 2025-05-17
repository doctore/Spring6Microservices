package com.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * Class used to receive the authorization information related with logged users.
 */
@AllArgsConstructor
@EqualsAndHashCode(of = {"username"})
@Data
@NoArgsConstructor
public class UsernameAuthoritiesDto {

    private String username;
    private Set<String> authorities;

}
