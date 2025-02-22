package com.security.custom.util;

import com.security.custom.enums.HashAlgorithm;
import com.spring6microservices.common.core.util.AssertUtil;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static java.lang.String.format;

@Log4j2
@UtilityClass
public class HashUtil {


    /**
     * Verify if applying the given {@code hashAlgorithm} with the provided {@code sourceString}, the result is {@code hashedString}
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
     *                                  if {@code hashAlgorithm} is {@code null} or it does not exist
     */
    public static boolean verifyHash(final String sourceString,
                                     final String hashedString,
                                     final HashAlgorithm hashAlgorithm) {
        AssertUtil.notNull(sourceString, "sourceString must be not null");
        AssertUtil.notNull(hashedString, "hashedString must be not null");
        AssertUtil.notNull(hashAlgorithm, "hashAlgorithm must be not null");
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(
                    hashAlgorithm.getAlgorithm()
            );
            byte[] digested = messageDigest.digest(sourceString.getBytes());
            return hashedString.equals(
                    bytesToHex(digested)
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
    private static String bytesToHex(final byte[] hash) {
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
