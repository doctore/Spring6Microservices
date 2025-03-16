package com.security.custom.service.token.provider;

import com.security.custom.enums.token.TokenType;
import com.security.custom.exception.token.TokenTypeProviderException;
import com.security.custom.interfaces.ITokenTypeProvider;
import com.spring6microservices.common.core.util.ObjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

import static java.lang.String.format;
import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;

/**
 * Class used to contain and provide the {@link ITokenTypeProvider} used to handle every {@link TokenType}.
 */
@Component
public class TokenTypeProviderRegistry {

    private final Map<TokenType, ITokenTypeProvider> tokenTypeProviders;


    @Autowired
    public TokenTypeProviderRegistry(final Collection<ITokenTypeProvider> iTokenTypeProviders) {
        this.tokenTypeProviders = addITokenProvidersOrThrow(
                ObjectUtil.getOrElse(
                        iTokenTypeProviders,
                        new ArrayList<>()
                )
        );
    }


    /**
     * Returns the {@link ITokenTypeProvider} used to manage the given {@code tokenType}.
     *
     * @param tokenType
     *    {@link TokenType} to search which {@link ITokenTypeProvider} is used to handle it
     *
     * @return {@link Optional} with the {@link ITokenTypeProvider} used to manage the given {@link TokenType},
     *         {@link Optional#empty()} otherwise.
     */
    public Optional<ITokenTypeProvider> getTokenTypeProvider(final TokenType tokenType) {
        return Optional.ofNullable(tokenType)
                .map(tokenTypeProviders::get);
    }


    /**
     *    Using provided {@code iTokenTypeProviders}, returns the {@link Map} containing the {@link ITokenTypeProvider}
     * used to handle ever {@link TokenType}.
     *
     * @param iTokenTypeProviders
     *    {@link Collection} of {@link ITokenTypeProvider}s
     *
     * @return {@link Map} of {@link TokenType} as key and {@link ITokenTypeProvider} as value
     *
     * @throws TokenTypeProviderException if there is more than one {@link ITokenTypeProvider} for the same {@link TokenType} or
     *                                    not all the {@link TokenType}s have a {@link ITokenTypeProvider} assigned
     */
    private Map<TokenType, ITokenTypeProvider> addITokenProvidersOrThrow(final Collection<ITokenTypeProvider> iTokenTypeProviders) {
        Map<TokenType, ITokenTypeProvider> result = new HashMap<>();
        for (ITokenTypeProvider iTokenTypeProvider : iTokenTypeProviders) {
            TokenTypeProvider tokenTypeProvider = findAnnotation(
                    iTokenTypeProvider.getClass(),
                    TokenTypeProvider.class
            );
            if (null != tokenTypeProvider) {
                ITokenTypeProvider registeredProvider = result.get(
                        tokenTypeProvider.value()
                );
                if (null != registeredProvider) {
                    throw new TokenTypeProviderException(
                            format("Multiple token type providers found for token type: %s. The registered provider is: %s and the new one to add: %s",
                                    tokenTypeProvider.value(),
                                    registeredProvider.getClass().getCanonicalName(),
                                    iTokenTypeProvider.getClass().getCanonicalName()
                            )
                    );
                }
                result.put(
                        tokenTypeProvider.value(),
                        iTokenTypeProvider
                );
            }
        }
        if (TokenType.values().length != result.size()) {
            throw new TokenTypeProviderException(
                    format("The expected token type providers: %s do not match with the registered ones: %s",
                            Arrays.toString(
                                    TokenType.values()
                            ),
                            result.keySet()
                    )
            );
        }
        return result;
    }

}
