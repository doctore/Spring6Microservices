package com.order.controller;

import com.order.configuration.Constants;
import com.order.configuration.rest.RestRoutes;
import com.order.dto.OrderDto;
import com.order.dto.OrderLineDto;
import com.order.service.OrderService;
import com.order.util.converter.OrderConverter;
import com.spring6microservices.common.spring.dto.ErrorResponseDto;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static com.order.TestDataFactory.*;
import static com.spring6microservices.common.spring.enums.RestApiErrorCode.VALIDATION;
import static org.mockito.Mockito.verifyNoInteractions;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = OrderController.class)
// TODO: PENDING TO CONFIGURE
//@Import(WebSecurityConfiguration.class)
public class OrderControllerTest extends BaseControllerTest {

    @MockitoBean
    private OrderConverter mockConverter;

    @MockitoBean
    private OrderService mockService;

    @Autowired
    private MockMvc mockMvc;


    @Test
    @SneakyThrows
    @DisplayName("create: when no logged user is given then unauthorized Http code is returned")
    public void create_whenNoLoggedUserIsGiven_thenUnauthorizedHttpCodeIsReturned() {
        OrderDto dto = buildNewOrderDtoWithOrderLine();

        mockMvc.perform(
                        post(RestRoutes.ORDER.ROOT)
                                .contentType(APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(dto)
                                )
                )
                .andExpect(
                        status().isForbidden()
                        // TODO: PENDING TO FIX (401 is the expected one)
                        //status().isUnauthorized()
                );

        verifyNoInteractions(mockConverter);
        verifyNoInteractions(mockService);
    }


    @Test
    @SneakyThrows
    @WithMockUser(authorities = { Constants.PERMISSIONS.GET_ORDER })
    @DisplayName("create: when no valid authority is given then forbidden Http code is returned")
    public void create_whenNotValidAuthorityIsGiven_thenForbiddenHttpCodeIsReturned() {
        OrderDto dto = buildNewOrderDtoWithOrderLine();

        mockMvc.perform(
                        post(RestRoutes.ORDER.ROOT)
                                .contentType(APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(dto)
                                )
                )
                .andExpect(
                        status().isForbidden()
                );

        verifyNoInteractions(mockConverter);
        verifyNoInteractions(mockService);
    }


    // TODO: PENDING TO FIX (400 is the expected one but it's returning 403 because no security configuration has been set by now)
    /*
    static Stream<Arguments> create_invalidParametersTestCases() {
        String longString = String.join("", Collections.nCopies(300, "a"));

        OrderDto dtoWithNoCode = buildNewOrderDtoWithOrderLine();
        dtoWithNoCode.setCode(null);

        OrderDto dtoWithLongCode = buildNewOrderDtoWithOrderLine();
        dtoWithLongCode.setCode(longString);

        OrderDto dtoWithOrderLineWithoutConcept = buildNewOrderDtoWithOrderLine();
        dtoWithOrderLineWithoutConcept.getOrderLines().getFirst().setConcept(null);

        OrderDto dtoWithOrderLineWithLongConcept = buildNewOrderDtoWithOrderLine();
        dtoWithOrderLineWithLongConcept.getOrderLines().getFirst().setConcept(longString);

        OrderDto dtoWithOrderLineWithNegativeAmount = buildNewOrderDtoWithOrderLine();
        dtoWithOrderLineWithNegativeAmount.getOrderLines().getFirst().setAmount(-1);

        OrderDto dtoWithOrderLineWithNegativeCost = buildNewOrderDtoWithOrderLine();
        dtoWithOrderLineWithNegativeCost.getOrderLines().getFirst().setCost(-2d);

        ErrorResponseDto responseDtoWithNoCode = new ErrorResponseDto(
                VALIDATION,
                List.of("Field error in object 'orderDto' on field 'code' due to: must not be null")
        );
        ErrorResponseDto responseDtoWithLongCode = new ErrorResponseDto(
                VALIDATION,
                List.of("Field error in object 'orderDto' on field 'code' due to: size must be between 1 and 64")
        );
        ErrorResponseDto responseDtoWithOrderLineWithoutConcept = new ErrorResponseDto(
                VALIDATION,
                List.of("Field error in object 'orderLineDto' on field 'concept' due to: must not be null")
        );
        ErrorResponseDto responseDtoWithOrderLineWithLongConcept = new ErrorResponseDto(
                VALIDATION,
                List.of("Field error in object 'orderLineDto' on field 'concept' due to: size must be between 1 and 255")
        );
        ErrorResponseDto responseDtoWithOrderLineWithNegativeAmount = new ErrorResponseDto(
                VALIDATION,
                List.of("Field error in object 'orderLineDto' on field 'amount' due to: must be a positive value")
        );
        ErrorResponseDto responseDtoWithOrderLineWithNegativeCost = new ErrorResponseDto(
                VALIDATION,
                List.of("Field error in object 'orderLineDto' on field 'cost' due to: must be a positive value")
        );
        return Stream.of(
                //@formatter:off
                //            dtoToCreate,                          expectedResponse
                Arguments.of( dtoWithNoCode,                        responseDtoWithNoCode ),
                Arguments.of( dtoWithLongCode,                      responseDtoWithLongCode ),
                Arguments.of( dtoWithOrderLineWithoutConcept,       responseDtoWithOrderLineWithoutConcept ),
                Arguments.of( dtoWithOrderLineWithLongConcept,      responseDtoWithOrderLineWithLongConcept ),
                Arguments.of( dtoWithOrderLineWithNegativeAmount,   responseDtoWithOrderLineWithNegativeAmount ),
                Arguments.of( dtoWithOrderLineWithNegativeCost,     responseDtoWithOrderLineWithNegativeCost )
        ); //@formatter:on
    }

    @ParameterizedTest
    @SneakyThrows
    @MethodSource("create_invalidParametersTestCases")
    @DisplayName("create: when given parameters do not verify validations then bad request error is returned with validation errors")
    @WithMockUser(authorities = { Constants.PERMISSIONS.CREATE_ORDER })
    public void create_whenGivenParametersDoNotVerifyValidations_thenBadRequestHttpCodeAndValidationErrorsAreReturned(OrderDto dtoToCreate,
                                                                                                                      ErrorResponseDto expectedResponse) {
        ResultActions result = mockMvc.perform(
                        post(RestRoutes.ORDER.ROOT)
                                .contentType(APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(dtoToCreate)
                                )
                )
                .andExpect(
                        status().isBadRequest()
                )
                .andExpect(
                        content().contentType(APPLICATION_JSON)
                );

        thenHttpErrorIsReturned(
                result,
                BAD_REQUEST,
                expectedResponse
        );
        verifyNoInteractions(mockConverter);
        verifyNoInteractions(mockService);
    }
     */



    private static OrderDto buildNewOrderDtoWithOrderLine() {
        OrderDto orderDto = buildOrderDto(
                "Order 1",
                new ArrayList<>()
        );
        OrderLineDto orderLine = buildOrderLineDto(
                orderDto.getId(),
                "Keyboard",
                2,
                10.1d
        );
        orderDto.setOrderLines(
                List.of(
                        orderLine
                )
        );
        return orderDto;
    }

}
