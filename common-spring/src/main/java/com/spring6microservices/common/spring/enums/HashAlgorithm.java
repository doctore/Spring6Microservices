package com.spring6microservices.common.spring.enums;

import com.spring6microservices.common.core.util.EnumUtil;
import com.spring6microservices.common.core.util.StringUtil;
import com.spring6microservices.common.spring.validator.enums.IEnumInternalPropertyValue;
import lombok.Getter;
import org.springframework.lang.Nullable;

import java.util.Arrays;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

/**
 * Secure one-way hash methods that take arbitrary-sized data and output a fixed-length hash value.
 */
@Getter
public enum HashAlgorithm implements IEnumInternalPropertyValue<String> {

    MD5("MD5"),
    SHA_256("SHA-256"),
    SHA_384("SHA-384"),
    SHA_512("SHA-512"),
    SHA3_256("SHA3-256"),
    SHA3_384("SHA3-384"),
    SHA3_512("SHA3-512");

    private final String algorithm;


    HashAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }


    @Override
    public String getInternalPropertyValue() {
        return this.algorithm;
    }


    /**
     * Gets the {@link HashAlgorithm} that matches with the given one.
     *
     * @param algorithm
     *    Internal algorithm value to search
     *
     * @return {@link Optional} with {@link HashAlgorithm} if {@code key} matches with existing one,
     *         {@link Optional#empty()} otherwise
     */
    public static Optional<HashAlgorithm> getByAlgorithm(@Nullable final String algorithm) {
        return EnumUtil.getByInternalProperty(
                HashAlgorithm.class,
                algorithm,
                HashAlgorithm::getAlgorithm
        );
    }


    /**
     * Returns the list of available hash algorithms.
     *
     * @return {{@link String} with the current available hash algorithms
     */
    public static String getAvailableAlgorithms() {
        return StringUtil.join(
                Arrays.stream(
                        HashAlgorithm.values()
                )
                .map(HashAlgorithm::getAlgorithm)
                .collect(toList())
        );
    }

}
