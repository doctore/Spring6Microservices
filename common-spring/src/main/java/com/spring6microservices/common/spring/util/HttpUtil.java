package com.spring6microservices.common.spring.util;

import com.spring6microservices.common.core.collection.tuple.Tuple;
import com.spring6microservices.common.core.collection.tuple.Tuple2;
import com.spring6microservices.common.core.util.AssertUtil;
import com.spring6microservices.common.core.util.StringUtil;
import lombok.experimental.UtilityClass;

import java.util.Base64;

import static com.spring6microservices.common.core.util.StringUtil.getOrElse;
import static java.lang.String.format;

@UtilityClass
public class HttpUtil {

    public static final String BASIC_AUTHORIZATION_HEADER = "Basic ";
    public static final String BASIC_AUTHORIZATION_HEADER_SEPARATOR = ":";


    /**
     * Builds the required Basic Authentication header value.
     *
     * @param username
     *    User's identifier. {@link StringUtil#EMPTY_STRING} will be used if it has no value
     * @param password
     *    User's password. {@link StringUtil#EMPTY_STRING} will be used if it has no value
     *
     * @return {@link String}
     *
     * @throws IllegalArgumentException if given {@code username} contains the character {@link HttpUtil#BASIC_AUTHORIZATION_HEADER_SEPARATOR}
     */
    public static String encodeBasicAuthentication(final String username,
                                                   final String password) {
        final String finalUsername = getOrElse(
                username,
                StringUtil.EMPTY_STRING
        );
        final String finalPassword = getOrElse(
                password,
                StringUtil.EMPTY_STRING
        );
        AssertUtil.isFalse(
                finalUsername.contains(BASIC_AUTHORIZATION_HEADER_SEPARATOR),
                format("Given username: %s must not contain the character: '%s'",
                        username,
                        BASIC_AUTHORIZATION_HEADER_SEPARATOR
                )
        );
        final String auth =
                finalUsername +
                        BASIC_AUTHORIZATION_HEADER_SEPARATOR +
                        finalPassword;

        byte[] encodedAuth = Base64.getEncoder()
                .encode(auth.getBytes());

        return BASIC_AUTHORIZATION_HEADER +
                new String(encodedAuth);
    }


    /**
     * Decodes provided encoded Basic Authentication header value.
     *
     * @param encodeBasicAuth
     *    {@link String} with an encoded Basic Authentication header value. {@link StringUtil#EMPTY_STRING} will be used if it has no value
     *
     * @return {@link Tuple2} with username as first value and password as second one
     *
     * @throws IllegalArgumentException if given {@code encodeBasicAuth} does not start with {@link HttpUtil#BASIC_AUTHORIZATION_HEADER} or
     *                                  if given {@code encodeBasicAuth} without {@link HttpUtil#BASIC_AUTHORIZATION_HEADER} is not in valid Base64 scheme or
     *                                  if after decoding {@code encodeBasicAuth}, it does not contain {@link HttpUtil#BASIC_AUTHORIZATION_HEADER_SEPARATOR}
     */
    public static Tuple2<String, String> decodeBasicAuthentication(final String encodeBasicAuth) {
        final String finalEncodeBasicAuth = getOrElse(
                encodeBasicAuth,
                StringUtil.EMPTY_STRING
        );
        AssertUtil.isTrue(
                finalEncodeBasicAuth.startsWith(BASIC_AUTHORIZATION_HEADER),
                format("Given encode basic authentication: %s must start with: '%s'",
                        encodeBasicAuth,
                        BASIC_AUTHORIZATION_HEADER
                )
        );
        final String base64Credentials = finalEncodeBasicAuth.substring(
                BASIC_AUTHORIZATION_HEADER.length()
        ).trim();

        final String rawBasicAuth = new String(
                Base64.getDecoder()
                        .decode(base64Credentials.getBytes())
        );
        int delim = rawBasicAuth.indexOf(BASIC_AUTHORIZATION_HEADER_SEPARATOR);
        AssertUtil.isFalse(
                -1 == delim,
                format("Using the given encode basic authentication: %s after removing basic authentication header: %s"
                                + "and decode it: %s, was not possible to find the expected username and password separator: '%s'",
                        encodeBasicAuth,
                        BASIC_AUTHORIZATION_HEADER,
                        rawBasicAuth,
                        BASIC_AUTHORIZATION_HEADER_SEPARATOR
                )
        );
        return Tuple.of(
                rawBasicAuth.substring(0, delim),
                rawBasicAuth.substring(delim + 1)
        );
    }

}
