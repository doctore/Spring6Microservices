package com.security.custom.service.token.provider;

import com.security.custom.enums.token.TokenType;
import com.security.custom.exception.token.TokenTypeProviderException;
import com.security.custom.interfaces.ITokenTypeProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
public class TokenTypeProviderRegistryTest {

    @Mock
    private EncryptedJweProvider mockEncryptedJweProvider;

    @Mock
    private EncryptedJwsProvider mockEncryptedJwsProvider;

    @Mock
    private JweProvider mockJweProvider;

    @Mock
    private JwsProvider mockJwsProvider;


    static Stream<Arguments> constructorITokenTypeProvidersHasNoElementsTestCases() {
        return Stream.of(
                //@formatter:off
                //            iTokenTypeProviders
                Arguments.of( (Object) null ),
                Arguments.of( List.of() )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("constructorITokenTypeProvidersHasNoElementsTestCases")
    @DisplayName("constructor: iTokenTypeProviders has no elements test cases")
    public void constructorITokenTypeProvidersHasNoElements_testCases(Collection<ITokenTypeProvider> iTokenTypeProviders) {
        assertThrows(
                TokenTypeProviderException.class,
                () -> new TokenTypeProviderRegistry(iTokenTypeProviders)
        );
    }


    @Test
    @DisplayName("constructor: when iTokenTypeProviders do not cover all TokenType values then TokenTypeProviderException is thrown")
    public void constructor_whenITokenTypeProvidersDoNotCoverAllTokenTypeValues_thenTokenTypeProviderExceptionIsThrown() {
        List<ITokenTypeProvider> iTokenTypeProviders = List.of(
                mockEncryptedJweProvider,
                mockJweProvider,
                mockJwsProvider
        );

        assertThrows(
                TokenTypeProviderException.class,
                () -> new TokenTypeProviderRegistry(iTokenTypeProviders)
        );
    }


    @Test
    @DisplayName("constructor: when iTokenTypeProviders have duplicates then TokenTypeProviderException is thrown")
    public void constructor_whenITokenTypeProvidersHaveDuplicates_thenTokenTypeProviderExceptionIsThrown() {
        List<ITokenTypeProvider> iTokenTypeProviders = List.of(
                mockEncryptedJweProvider,
                mockJweProvider,
                mockEncryptedJweProvider
        );

        assertThrows(
                TokenTypeProviderException.class,
                () -> new TokenTypeProviderRegistry(iTokenTypeProviders)
        );
    }


    @Test
    @DisplayName("constructor: when iTokenTypeProviders cover all TokenType values then the instance is created successfully")
    public void constructor_whenITokenTypeProvidersCoverAllTokenTypeValues_thenTheInstanceIsCreatedSuccessfully() {
        List<ITokenTypeProvider> iTokenTypeProviders = List.of(
                mockEncryptedJweProvider,
                mockEncryptedJwsProvider,
                mockJweProvider,
                mockJwsProvider
        );

        TokenTypeProviderRegistry registry = new TokenTypeProviderRegistry(iTokenTypeProviders);

        assertNotNull(registry);
    }


    @Test
    @DisplayName("getTokenTypeProvider: when there is a iTokenTypeProvider related with given tokenType then the instance is returned")
    public void getTokenTypeProvider_whenThereIsAITokenTypeProviderRelatedWithGivenTokenType_thenTheInstanceIsReturned() {
        List<ITokenTypeProvider> iTokenTypeProviders = List.of(
                mockEncryptedJweProvider,
                mockEncryptedJwsProvider,
                mockJweProvider,
                mockJwsProvider
        );

        TokenTypeProviderRegistry registry = new TokenTypeProviderRegistry(iTokenTypeProviders);
        assertNotNull(registry);

        for (TokenType tokenType : TokenType.values()) {
            Optional<ITokenTypeProvider> iTokenTypeProvider = registry.getTokenTypeProvider(tokenType);

            assertNotNull(iTokenTypeProvider);
            assertTrue(iTokenTypeProvider.isPresent());
        }
    }

}
