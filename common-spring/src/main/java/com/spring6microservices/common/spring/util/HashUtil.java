package com.spring6microservices.common.spring.util;

import com.spring6microservices.common.core.util.AssertUtil;
import com.spring6microservices.common.core.util.StringUtil;
import com.spring6microservices.common.spring.enums.HashAlgorithm;
import lombok.experimental.UtilityClass;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static com.spring6microservices.common.core.util.StringUtil.getOrElse;
import static java.lang.String.format;

@UtilityClass
public class HashUtil {


    /**
     * Returns the hash value of given {@code sourceString} using provided {@link HashAlgorithm}.
     *
     * @param sourceString
     *    {@link String} to hash. {@link StringUtil#EMPTY_STRING} will be used if it has no value
     * @param hashAlgorithm
     *    {@link HashAlgorithm} to use
     *
     * @return {@link String} with the hash value of {@code sourceString}
     *
     * @throws IllegalArgumentException if {@code hashAlgorithm} is {@code null} or it could not be used to hash {@code sourceString}
     */
    public static String hash(final String sourceString,
                              final HashAlgorithm hashAlgorithm) {
        AssertUtil.notNull(hashAlgorithm, "hashAlgorithm must be not null");
        final String finalSourceString = getOrElse(
                sourceString,
                StringUtil.EMPTY_STRING
        );
        MessageDigest messageDigest = getMessageDigestInstance(
                hashAlgorithm
        );
        return bytesToHexString(
                messageDigest.digest(
                        finalSourceString.getBytes()
                )
        );
    }


    /**
     * Verify if applying the given {@code hashAlgorithm} with the provided {@code sourceString}, the result is {@code hashedString}.
     *
     * @param sourceString
     *    Original {@link String} to verify using {@code hashAlgorithm}
     * @param hashedString
     *    {@link String} to compare with {@code sourceString} after applying {@code hashAlgorithm}
     * @param hashAlgorithm
     *    {@link HashAlgorithm} to use
     *
     * @return {@code true} if {@code sourceString} applying {@code hashAlgorithm} is equals to {@code hashedString},
     *         {@code false} otherwise
     *
     * @throws IllegalArgumentException if {@code sourceString} is {@code null} or
     *                                  if {@code hashedString} is {@code null} or
     *                                  if {@code hashAlgorithm} is {@code null} or it could not be used to hash {@code sourceString}
     */
    public static boolean verifyHash(final String sourceString,
                                     final String hashedString,
                                     final HashAlgorithm hashAlgorithm) {
        AssertUtil.notNull(sourceString, "sourceString must be not null");
        AssertUtil.notNull(hashedString, "hashedString must be not null");
        AssertUtil.notNull(hashAlgorithm, "hashAlgorithm must be not null");

        MessageDigest messageDigest = getMessageDigestInstance(
                hashAlgorithm
        );
        byte[] digested = messageDigest.digest(
                sourceString.getBytes()
        );
        return hashedString.equals(
                bytesToHexString(digested)
        );
    }


    /**
     * Returns the {@link MessageDigest} related with provided {@link HashAlgorithm}.
     *
     * @param hashAlgorithm
     *    {@link HashAlgorithm} to use
     *
     * @return {@link MessageDigest}
     *
     * @throws IllegalArgumentException if there is no {@link MessageDigest} related with given {@code hashAlgorithm}
     */
    private static MessageDigest getMessageDigestInstance(final HashAlgorithm hashAlgorithm) {
        try {
            return MessageDigest.getInstance(
                    hashAlgorithm.getAlgorithm()
            );

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(
                    format("There was an error trying to use the provided hash algorithm: %s ",
                            hashAlgorithm.getAlgorithm()
                    ),
                    e
            );
        }
    }


    /**
     * Converts given {@code hash} in a {@link String}.
     *
     * @param hash
     *    Byte array to convert
     *
     * @return {@link String}
     */
    private static String bytesToHexString(final byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

}
