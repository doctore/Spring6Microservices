package com.security.jwt.util;

import com.security.jwt.enums.token.TokenSignatureAlgorithm;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class JwsUtilTest {

    static Stream<Arguments> generateTokenTestCases() {
        Map<String, Object> informationToInclude = new HashMap<>();
        String doesNotCareSecret = "ItDoesNotCare";
        return Stream.of(
                //@formatter:off
                //            informationToInclude,   signatureAlgorithm,            signatureSecret,          expirationTimeInSeconds,   expectedException
                Arguments.of( null,                   null,                          null,                     90,                        IllegalArgumentException.class ),
                Arguments.of( null,                   null,                          "",                       90,                        IllegalArgumentException.class ),
                Arguments.of( null,                   HS256_SIGNATURE_ALGORITHM,     doesNotCareSecret,        90,                        IllegalArgumentException.class ),
                Arguments.of( informationToInclude,   null,                          doesNotCareSecret,        90,                        IllegalArgumentException.class ),
                Arguments.of( informationToInclude,   HS256_SIGNATURE_ALGORITHM,     null,                     90,                        IllegalArgumentException.class ),
                // Not available algorithms yet
                Arguments.of( informationToInclude,   ED25519_SIGNATURE_ALGORITHM,   doesNotCareSecret,        90,                        IllegalArgumentException.class ),
                Arguments.of( informationToInclude,   ED448_SIGNATURE_ALGORITHM,     doesNotCareSecret,        90,                        IllegalArgumentException.class ),
                Arguments.of( informationToInclude,   EDDSA_SIGNATURE_ALGORITHM,     doesNotCareSecret,        90,                        IllegalArgumentException.class ),
                Arguments.of( informationToInclude,   ES256_SIGNATURE_ALGORITHM,     doesNotCareSecret,        90,                        IllegalArgumentException.class ),
                Arguments.of( informationToInclude,   ES256K_SIGNATURE_ALGORITHM,    doesNotCareSecret,        90,                        IllegalArgumentException.class ),
                Arguments.of( informationToInclude,   ES384_SIGNATURE_ALGORITHM,     doesNotCareSecret,        90,                        IllegalArgumentException.class ),
                Arguments.of( informationToInclude,   ES512_SIGNATURE_ALGORITHM,     doesNotCareSecret,        90,                        IllegalArgumentException.class ),
                Arguments.of( informationToInclude,   PS256_SIGNATURE_ALGORITHM,     doesNotCareSecret,        90,                        IllegalArgumentException.class ),
                Arguments.of( informationToInclude,   PS384_SIGNATURE_ALGORITHM,     doesNotCareSecret,        90,                        IllegalArgumentException.class ),
                Arguments.of( informationToInclude,   PS512_SIGNATURE_ALGORITHM,     doesNotCareSecret,        90,                        IllegalArgumentException.class ),
                // Available algorithms
                Arguments.of( informationToInclude,   HS256_SIGNATURE_ALGORITHM,     HS256_SIGNATURE_SECRET,   90,                        null ),
                Arguments.of( informationToInclude,   HS384_SIGNATURE_ALGORITHM,     HS384_SIGNATURE_SECRET,   90,                        null ),
                Arguments.of( informationToInclude,   HS512_SIGNATURE_ALGORITHM,     HS512_SIGNATURE_SECRET,   90,                        null ),
                Arguments.of( informationToInclude,   RS256_SIGNATURE_ALGORITHM,     RS_PRIVATE_KEY,           90,                        null ),
                Arguments.of( informationToInclude,   RS384_SIGNATURE_ALGORITHM,     RS_PRIVATE_KEY,           90,                        null ),
                Arguments.of( informationToInclude,   RS512_SIGNATURE_ALGORITHM,     RS_PRIVATE_KEY,           90,                        null )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("generateTokenTestCases")
    @DisplayName("generateToken: test cases")
    public void generateToken_testCases(Map<String, Object> informationToInclude,
                                        TokenSignatureAlgorithm signatureAlgorithm,
                                        String signatureSecret,
                                        long expirationTimeInSeconds,
                                        Class<? extends Exception> expectedException) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> JwsUtil.generateToken(informationToInclude, signatureAlgorithm, signatureSecret, expirationTimeInSeconds)
            );
        }
        else {
            assertNotNull(
                    JwsUtil.generateToken(informationToInclude, signatureAlgorithm, signatureSecret, expirationTimeInSeconds)
            );
        }
    }


    private static final TokenSignatureAlgorithm ED25519_SIGNATURE_ALGORITHM = TokenSignatureAlgorithm.Ed25519;

    private static final TokenSignatureAlgorithm ED448_SIGNATURE_ALGORITHM = TokenSignatureAlgorithm.Ed448;

    private static final TokenSignatureAlgorithm EDDSA_SIGNATURE_ALGORITHM = TokenSignatureAlgorithm.EdDSA;

    private static final TokenSignatureAlgorithm ES256_SIGNATURE_ALGORITHM = TokenSignatureAlgorithm.ES256;

    private static final TokenSignatureAlgorithm ES256K_SIGNATURE_ALGORITHM = TokenSignatureAlgorithm.ES256K;

    private static final TokenSignatureAlgorithm ES384_SIGNATURE_ALGORITHM = TokenSignatureAlgorithm.ES384;

    private static final TokenSignatureAlgorithm ES512_SIGNATURE_ALGORITHM = TokenSignatureAlgorithm.ES512;

    private static final TokenSignatureAlgorithm PS256_SIGNATURE_ALGORITHM = TokenSignatureAlgorithm.PS256;

    private static final TokenSignatureAlgorithm PS384_SIGNATURE_ALGORITHM = TokenSignatureAlgorithm.PS384;

    private static final TokenSignatureAlgorithm PS512_SIGNATURE_ALGORITHM = TokenSignatureAlgorithm.PS512;

    private static final TokenSignatureAlgorithm HS256_SIGNATURE_ALGORITHM = TokenSignatureAlgorithm.HS256;

    private static final TokenSignatureAlgorithm HS384_SIGNATURE_ALGORITHM = TokenSignatureAlgorithm.HS384;

    private static final TokenSignatureAlgorithm HS512_SIGNATURE_ALGORITHM = TokenSignatureAlgorithm.HS512;

    private static final TokenSignatureAlgorithm RS256_SIGNATURE_ALGORITHM = TokenSignatureAlgorithm.RS256;

    private static final TokenSignatureAlgorithm RS384_SIGNATURE_ALGORITHM = TokenSignatureAlgorithm.RS384;

    private static final TokenSignatureAlgorithm RS512_SIGNATURE_ALGORITHM = TokenSignatureAlgorithm.RS512;

    private static final String HS256_SIGNATURE_SECRET = "hs256SignatureSecret#secret#789(jwt)$3411781_GTDSAET-569016310k";

    private static final String HS384_SIGNATURE_SECRET = "hs384SignatureSecret#secret#789(jwt)$3411781_GTDSAET-569016310k_extra_384";

    private static final String HS512_SIGNATURE_SECRET = "hs512SignatureSecret#secret#789(jwt)$3411781_GTDSAET-569016310k_extra_required_512";

    private static final String RS_PRIVATE_KEY =
            "-----BEGIN PRIVATE KEY-----\n" +
            "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCXHK07oaqx8fnY\n" +
            "r3UbfUS6HRXQFRvQ0J8qqzgq+UH4ZqtgxV44ciSOwzL65E2aZrixXxB+s7Kbbw1q\n" +
            "R0oUNvay8QhMlmwUZwXjCZbeNbQI8LXoXSU1l9xx2GZ7BS3/huFGHSyGzjrSYJdJ\n" +
            "cZKOYij26aCPIx7VYEeIUmPAbkCA1VVUhaOic81aQAdhqrKjqpBcYTwYW4YF2zcy\n" +
            "Dx8YLrRLJbjFzM94eg9oErqIsptyZ83daoNytVTbijzDoXAmkHrx58NfZnuJ0JfH\n" +
            "UKGZiMlt6fBAqDR3+Dls6hemJA+VxCO2dKBDp2vSGfIDc1mr1kQozFK3JqFINcWI\n" +
            "537vnPWVAgMBAAECggEAA/VAagMFx3k/p/05MMdi8l9afSkJtw+Of7hc4APyhlOw\n" +
            "HPiGdi2H3MUVnoHg23thzo7feHtzS+7Id+vBRQ7HKZrhHVpvnx2EsgnurZ1+p0ug\n" +
            "xCLpG4KBsmoD4yiDUtcBAGG5aG2El709G94cQ9uj2DXN2rnwL+VrR5GQOHqFeNUI\n" +
            "rTKUG4lwCPcvPOvnpdYj2jv4oj4uO2cbmgbZThcl4KdHK/Eo/jHr0UOhiT5J9ocm\n" +
            "RKryYYjEXE/t57tR2e0Rsel74fTmcgNygiixMjKDC1cmqX4R+g67m1gfR+/+SXR8\n" +
            "S9f9VzcwugcTnxIhke3TRta53QgfPNLOidpMM1tLwQKBgQC9faOxEVJ2KTQaAAMw\n" +
            "Nx8bBxhev5mifi+f8d14ERkG7XFb4SzPeUY29oB0KVxDyBwR8rgNars+GpUnquZv\n" +
            "91PVs5fYD3W+HwtOD/UOL0z3UtKnNI8nvtK08ru0PFDVzwzqEapy8dLkmbG556GP\n" +
            "HZ5WVn+8QeTX7GqbSU3xtPp21QKBgQDMJpTMzneQ+GrupU1lzdlD8GKF2RbsZ0Ui\n" +
            "rtIx4UYgIQV0lbvPhneJrGy16woOBUZ7jkCEDXKqofGumwCVfhpjjYzIqPfZzXaa\n" +
            "t5a6l2cLuwt0JnjluwqmIfWf1z+GdqCxgqUwdUgzxcPmzxcHwOCX1YFQQ8WONd6s\n" +
            "Id9DfAFjwQKBgQCLsKhQq11oAD4JgMLY83m52gQsLQEcWfvP5GSI08noYnhz7E61\n" +
            "cEjD0fqmJ6t9yHJxBMaMFYeNY9lbEdCo7+JcovWocNUy3/3cgUT9PP93QBZM7yEt\n" +
            "gq4geOTJHMHWrLlvgLBv5je7EFaFnu1p7MLCESg/ZzBFwWJhsauFKQ6PNQKBgFDc\n" +
            "PzfX15f+LSyVINDf9dxpDD0DvYapaMLSB8Nl/Qagza5d2GPcWOCZAP4VOIhRIpex\n" +
            "wnALe42GU1nbXyHXLtCbslWQR4tnTED/0p3ZdiE5VtIMovorWY5wCP/km+7Acemd\n" +
            "W5yT96M6A9wZzn9tsAezs2J9VXR8ddQsHmh2Z36BAoGBAIkFBge0QbWZGYCr3uk9\n" +
            "K0AhZUekGSzhakqp60XQs5kw8zb+TllCRxtYsQlyaHp1M8AH3Di/Uw+EhBt6h4Uw\n" +
            "fAPCZRg8vdG8Hp26PwXxybZ/M9u7NaKJ0BT4AwKKtZTUxZVxz/kPhdHT+MpoQqJf\n" +
            "JuzuwXVAAcl1GME2OiqkZhww\n" +
            "-----END PRIVATE KEY-----";

    private static final String RS_PUBLIC_KEY =
            "-----BEGIN PUBLIC KEY-----\n" +
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlxytO6GqsfH52K91G31E\n" +
            "uh0V0BUb0NCfKqs4KvlB+GarYMVeOHIkjsMy+uRNmma4sV8QfrOym28NakdKFDb2\n" +
            "svEITJZsFGcF4wmW3jW0CPC16F0lNZfccdhmewUt/4bhRh0shs460mCXSXGSjmIo\n" +
            "9umgjyMe1WBHiFJjwG5AgNVVVIWjonPNWkAHYaqyo6qQXGE8GFuGBds3Mg8fGC60\n" +
            "SyW4xczPeHoPaBK6iLKbcmfN3WqDcrVU24o8w6FwJpB68efDX2Z7idCXx1ChmYjJ\n" +
            "benwQKg0d/g5bOoXpiQPlcQjtnSgQ6dr0hnyA3NZq9ZEKMxStyahSDXFiOd+75z1\n" +
            "lQIDAQAB\n" +
            "-----END PUBLIC KEY-----";

}
