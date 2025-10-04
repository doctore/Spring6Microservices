package com.invoice.util.deserializer;

import com.spring6microservices.common.spring.jms.dto.EventDto;
import com.spring6microservices.common.spring.jms.dto.OrderEventDto;
import org.apache.kafka.common.errors.SerializationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(
        SpringExtension.class
)
public class OrderEventDtoJsonDeserializerTest {

    private OrderEventDtoJsonDeserializer deserializer;


    @BeforeEach
    public void setUp() {
        deserializer = new OrderEventDtoJsonDeserializer();
    }


    static Stream<Arguments> deserializeTestCases() {
        byte[] wrongData = new byte[] { 100, 120 };
        byte[] validData = new byte[] { 123, 34, 105, 100, 34, 58, 34, 52, 53, 99, 48, 55, 101, 54, 57, 45, 51, 57, 102, 54,
                45, 52, 57, 98, 52, 45, 98, 102, 48, 49, 45, 102, 97, 56, 54, 51, 100, 53, 57, 49,
                54, 52, 52, 34, 44, 34, 109, 101, 116, 97, 100, 97, 116, 97, 34, 58, 123, 34, 65, 85,
                84, 72, 79, 82, 73, 90, 65, 84, 73, 79, 78, 34, 58, 34, 66, 97, 115, 105, 99, 32,
                85, 51, 66, 121, 97, 87, 53, 110, 78, 107, 49, 112, 89, 51, 74, 118, 99, 50, 86, 121,
                100, 109, 108, 106, 90, 88, 77, 54, 85, 51, 66, 121, 97, 87, 53, 110, 78, 107, 49, 112,
                89, 51, 74, 118, 99, 50, 86, 121, 100, 109, 108, 106, 90, 88, 77, 61, 34, 125, 44, 34,
                98, 111, 100, 121, 34, 58, 123, 34, 105, 100, 34, 58, 51, 54, 44, 34, 99, 117, 115, 116,
                111, 109, 101, 114, 67, 111, 100, 101, 34, 58, 34, 67, 117, 115, 116, 111, 109, 101, 114, 32,
                50, 34, 44, 34, 99, 111, 115, 116, 34, 58, 51, 53, 52, 54, 46, 49, 50, 125, 125 };

        EventDto<OrderEventDto> expectedResultOfValidData = EventDto.<OrderEventDto>builder()
                .id("45c07e69-39f6-49b4-bf01-fa863d591644")
                .metadata(
                        new HashMap<>() {{
                            put(
                                    "AUTHORIZATION",
                                    "Basic U3ByaW5nNk1pY3Jvc2VydmljZXM6U3ByaW5nNk1pY3Jvc2VydmljZXM="
                            );
                        }}
                )
                .body(
                        OrderEventDto.builder()
                                .id(36)
                                .cost(3546.12d)
                                .customerCode("Customer 2")
                                .build()
                )
                .build();
        return Stream.of(
                //@formatter:off
                //            data,        expectedException,              expectedResult
                Arguments.of( null,        null,                           null ),
                Arguments.of( wrongData,   SerializationException.class,   null ),
                Arguments.of( validData,   null,                           expectedResultOfValidData )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("deserializeTestCases")
    @DisplayName("deserialize: test cases")
    public void deserialize_testCases(byte[] data,
                                      Class<? extends Exception> expectedException,
                                      EventDto<OrderEventDto> expectedResult) {
        String topic = "TestTopic";
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> deserializer.deserialize(topic, data)
            );
        }
        else {
            assertEquals(
                    expectedResult,
                    deserializer.deserialize(topic, data)
            );
        }
    }

}
