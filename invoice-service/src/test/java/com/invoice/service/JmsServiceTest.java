package com.invoice.service;

import com.invoice.configuration.security.configuration.AuthorizationServerConfiguration;
import com.invoice.model.Invoice;
import com.spring6microservices.common.spring.jms.JmsHeader;
import com.spring6microservices.common.spring.jms.dto.EventDto;
import com.spring6microservices.common.spring.jms.dto.OrderEventDto;
import com.spring6microservices.common.spring.util.HttpUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Stream;

import static com.invoice.TestDataFactory.*;
import static com.invoice.TestUtil.compareInvoices;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(
        SpringExtension.class
)
public class JmsServiceTest {

    @Mock
    private AuthorizationServerConfiguration mockAuthorizationConfiguration;

    @Mock
    private InvoiceService mockInvoiceService;

    private JmsService service;


    @BeforeEach
    public void init() {
        service = new JmsService(
                mockAuthorizationConfiguration,
                mockInvoiceService
        );
    }


    static Stream<Arguments> processNewOrderTestCases() {
        EventDto<OrderEventDto> emptyDto = new EventDto<>();
        EventDto<OrderEventDto> noMetadataDto = EventDto.<OrderEventDto>builder()
                .id("1")
                .body(
                        buildOrderEventDto()
                )
                .build();
        EventDto<OrderEventDto> noBodyDto = EventDto.<OrderEventDto>builder()
                .id("1")
                .metadata(
                        new HashMap<>() {{
                            put(
                                    JmsHeader.AUTHORIZATION.name(),
                                    HttpUtil.encodeBasicAuthentication(
                                            "user",
                                            "password"
                                    )
                            );
                        }}
                )
                .build();
        EventDto<OrderEventDto> completeDto = buildEventDto(
                HttpUtil.encodeBasicAuthentication(
                        "user",
                        "password"
                ),
                buildOrderEventDto()
        );
        Invoice invoice = buildInvoice();
        return Stream.of(
                //@formatter:off
                //            eventDto,              invoiceServiceResult,   expectedResult
                Arguments.of( null,            null,                         empty() ),
                Arguments.of( emptyDto,        null,                         empty() ),
                Arguments.of( noMetadataDto,   null,                         empty() ),
                Arguments.of( noBodyDto,       null,                         empty() ),
                Arguments.of( completeDto,     of(invoice),                  of(invoice) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("processNewOrderTestCases")
    @DisplayName("processNewOrder: test cases")
    public void processNewOrder_testCases(EventDto<OrderEventDto> eventDto,
                                          Optional<Invoice> invoiceServiceResult,
                                          Optional<Invoice> expectedResult) {
        when(mockAuthorizationConfiguration.getClientId())
                .thenReturn(
                        "user"
                );
        when(mockAuthorizationConfiguration.getClientPassword())
                .thenReturn(
                        "password"
                );
        when(mockInvoiceService.save(any(OrderEventDto.class)))
                .thenReturn(
                        invoiceServiceResult
                );

        Optional<Invoice> result = service.processNewOrder(
                eventDto
        );

        if (expectedResult.isEmpty()) {
            assertTrue(
                    result.isEmpty()
            );
            verify(mockInvoiceService, never())
                    .save(
                            any(OrderEventDto.class)
                    );
        }
        else {
            assertTrue(
                    result.isPresent()
            );
            compareInvoices(
                    expectedResult.get(),
                    result.get()
            );
            verify(mockInvoiceService, times(1))
                    .save(
                            any(OrderEventDto.class)
                    );
        }
    }

}
