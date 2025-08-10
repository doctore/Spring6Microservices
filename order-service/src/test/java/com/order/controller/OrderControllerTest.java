package com.order.controller;

import com.order.configuration.Constants;
import com.order.configuration.rest.RestRoutes;
import com.order.model.Order;
import com.order.model.OrderLine;
import com.order.service.OrderService;
import com.order.util.converter.OrderConverter;
import com.spring6microservices.common.core.util.StringUtil;
import com.spring6microservices.common.spring.dto.ErrorResponseDto;
import com.spring6microservices.common.spring.dto.order.OrderDto;
import com.spring6microservices.common.spring.dto.order.OrderLineDto;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
import static java.util.Optional.empty;
import static java.util.Optional.of;

import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(
        addFilters = false
)
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
                        status().isUnauthorized()
                );

        verifyNoInteractions(mockConverter);
        verifyNoInteractions(mockService);
    }


    @Test
    @SneakyThrows
    @WithMockUser(
            authorities = { Constants.PERMISSIONS.GET_ORDER }
    )
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
                List.of("Field error in object: orderDto on field: code due to: must not be null")
        );
        ErrorResponseDto responseDtoWithLongCode = new ErrorResponseDto(
                VALIDATION,
                List.of("Field error in object: orderDto on field: code due to: size must be between 1 and 64")
        );
        ErrorResponseDto responseDtoWithOrderLineWithoutConcept = new ErrorResponseDto(
                VALIDATION,
                List.of("Field error in object: orderDto on field: orderLines[0].concept due to: must not be null")
        );
        ErrorResponseDto responseDtoWithOrderLineWithLongConcept = new ErrorResponseDto(
                VALIDATION,
                List.of("Field error in object: orderDto on field: orderLines[0].concept due to: size must be between 1 and 255")
        );
        ErrorResponseDto responseDtoWithOrderLineWithNegativeAmount = new ErrorResponseDto(
                VALIDATION,
                List.of("Field error in object: orderDto on field: orderLines[0].amount due to: must be greater than 0")
        );
        ErrorResponseDto responseDtoWithOrderLineWithNegativeCost = new ErrorResponseDto(
                VALIDATION,
                List.of("Field error in object: orderDto on field: orderLines[0].cost due to: must be greater than 0")
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
    @WithMockUser(
            authorities = { Constants.PERMISSIONS.CREATE_ORDER }
    )
    @MethodSource("create_invalidParametersTestCases")
    @DisplayName("create: when given parameters do not verify validations then bad request error is returned with validation errors")
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


    @Test
    @SneakyThrows
    @WithMockUser(
            authorities = { Constants.PERMISSIONS.CREATE_ORDER }
    )
    @DisplayName("create: when given parameters verifies validations but service returns empty then Http code Unprocessable Entity is returned")
    public void create_whenGivenParametersVerifiesValidationsButServiceReturnsEmpty_thenHttpCodeUnprocessableEntityIsReturned() {
        OrderDto dto = buildNewOrderDtoWithOrderLine();
        Order model = buildNewOrderWithOrderLine();

        when(mockConverter.fromDtoToModel(dto))
                .thenReturn(
                        model
                );
        when(mockService.save(model))
                .thenReturn(
                        empty()
                );

        ResultActions result = mockMvc.perform(
                        post(RestRoutes.ORDER.ROOT)
                                .contentType(APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(dto)
                                )
                );

        thenBodyIsReturned(
                result,
                UNPROCESSABLE_ENTITY,
                null,
                OrderDto.class
        );

        verify(mockConverter, times(1))
                .fromDtoToModel(
                        dto
                );
        verify(mockService, times(1))
                .save(
                        model
                );
        verify(mockConverter, never())
                .fromModelToDto(
                        any(Order.class)
                );
    }


    @Test
    @SneakyThrows
    @WithMockUser(
            authorities = { Constants.PERMISSIONS.CREATE_ORDER }
    )
    @DisplayName("create: when given parameters verifies validations and service returns a model then Http code Created with the new Dto is returned")
    public void create_whenGivenParametersVerifiesValidationsAndServiceReturnAModel_thenHttpCodeCreatedWithTheNewDtoIsReturned() {
        OrderDto beforeDto = buildNewOrderDtoWithOrderLine();
        OrderDto afterDto = buildNewOrderDtoWithOrderLine();
        afterDto.setId(1);

        Order beforeModel = buildNewOrderWithOrderLine();
        Order afterModel = buildNewOrderWithOrderLine();
        afterModel.setId(
                afterDto.getId()
        );

        when(mockConverter.fromDtoToModel(beforeDto))
                .thenReturn(
                        beforeModel
                );
        when(mockService.save(beforeModel))
                .thenReturn(
                        of(afterModel)
                );
        when(mockConverter.fromModelToDto(afterModel))
                .thenReturn(
                        afterDto
                );

        ResultActions result = mockMvc.perform(
                        post(RestRoutes.ORDER.ROOT)
                                .contentType(APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(beforeDto)
                                )
                )
                .andExpect(
                        content().contentType(APPLICATION_JSON)
                );

        thenBodyIsReturned(
                result,
                CREATED,
                afterDto,
                OrderDto.class
        );

        verify(mockConverter, times(1))
                .fromDtoToModel(
                        beforeDto
                );
        verify(mockService, times(1))
                .save(
                        beforeModel
                );
        verify(mockConverter, times(1))
                .fromModelToDto(
                        afterModel
                );
    }


    @Test
    @SneakyThrows
    @DisplayName("deleteByCode: when no logged user is given then unauthorized Http code is returned")
    public void deleteByCode_whenNoLoggedUserIsGiven_thenUnauthorizedHttpCodeIsReturned() {
        String orderCode = StringUtil.urlEncode(
                "Order 1"
        );

        mockMvc.perform(
                        delete(RestRoutes.ORDER.ROOT + RestRoutes.ORDER.BY_CODE + "/" + orderCode)
                )
                .andExpect(
                        status().isUnauthorized()
                );

        verifyNoInteractions(mockService);
    }


    @Test
    @SneakyThrows
    @WithMockUser(
            authorities = { Constants.PERMISSIONS.GET_ORDER }
    )
    @DisplayName("deleteByCode: when no valid authority is given then forbidden Http code is returned")
    public void deleteByCode_whenNotValidAuthorityIsGiven_thenForbiddenHttpCodeIsReturned() {
        String orderCode = StringUtil.urlEncode(
                "Order 1"
        );

        mockMvc.perform(
                        delete(RestRoutes.ORDER.ROOT + RestRoutes.ORDER.BY_CODE + "/" + orderCode)
                )
                .andExpect(
                        status().isForbidden()
                );

        verifyNoInteractions(mockService);
    }


    @Test
    @SneakyThrows
    @WithMockUser(
            authorities = { Constants.PERMISSIONS.DELETE_ORDER }
    )
    @DisplayName("deleteByCode: when based on provided parameters the service returns false then Http code Not Found is returned")
    public void deleteByCode_whenBasedOnProvidedParametersTheServiceReturnsFalse_thenHttpCodeNotFoundIsReturned() {
        String orderCode = StringUtil.urlEncode(
                "Order 1"
        );

        when(mockService.deleteByCode(orderCode))
                .thenReturn(
                        false
                );

        mockMvc.perform(
                        delete(RestRoutes.ORDER.ROOT + RestRoutes.ORDER.BY_CODE + "/" + orderCode)
                )
                .andExpect(
                        status().isNotFound()
                );

        verify(mockService, times(1))
                .deleteByCode(
                        orderCode
                );
    }


    @Test
    @SneakyThrows
    @WithMockUser(
            authorities = { Constants.PERMISSIONS.DELETE_ORDER }
    )
    @DisplayName("deleteByCode: when based on provided parameters the service returns true then Http code No Content is returned")
    public void deleteByCode_whenBasedOnProvidedParametersTheServiceReturnsTrue_thenHttpCodeNoContentIsReturned() {
        String orderCode = StringUtil.urlEncode(
                "Order 1"
        );

        when(mockService.deleteByCode(orderCode))
                .thenReturn(
                        true
                );

        mockMvc.perform(
                        delete(RestRoutes.ORDER.ROOT + RestRoutes.ORDER.BY_CODE + "/" + orderCode)
                )
                .andExpect(
                        status().isNoContent()
                );

        verify(mockService, times(1))
                .deleteByCode(
                        orderCode
                );
    }


    @Test
    @SneakyThrows
    @DisplayName("deleteById: when no logged user is given then unauthorized Http code is returned")
    public void deleteById_whenNoLoggedUserIsGiven_thenUnauthorizedHttpCodeIsReturned() {
        int orderId = 1;

        mockMvc.perform(
                        delete(RestRoutes.ORDER.ROOT + RestRoutes.ORDER.BY_ID + "/" + orderId)
                )
                .andExpect(
                        status().isUnauthorized()
                );

        verifyNoInteractions(mockService);
    }


    @Test
    @SneakyThrows
    @WithMockUser(
            authorities = { Constants.PERMISSIONS.GET_ORDER }
    )
    @DisplayName("deleteById: when no valid authority is given then forbidden Http code is returned")
    public void deleteById_whenNotValidAuthorityIsGiven_thenForbiddenHttpCodeIsReturned() {
        int orderId = 1;

        mockMvc.perform(
                        delete(RestRoutes.ORDER.ROOT + RestRoutes.ORDER.BY_ID + "/" + orderId)
                )
                .andExpect(
                        status().isForbidden()
                );

        verifyNoInteractions(mockService);
    }


    static Stream<Arguments> deleteById_invalidParametersTestCases() {
        ErrorResponseDto responseInvalidOrderId = new ErrorResponseDto(
                VALIDATION,
                List.of("Error in path: 'deleteById.id' due to: must be greater than 0")
        );
        return Stream.of(
                //@formatter:off
                //            orderId,   expectedResponse
                Arguments.of( -1,        responseInvalidOrderId ),
                Arguments.of( 0,         responseInvalidOrderId )
        ); //@formatter:on
    }

    @ParameterizedTest
    @SneakyThrows
    @WithMockUser(
            authorities = { Constants.PERMISSIONS.DELETE_ORDER }
    )
    @MethodSource("deleteById_invalidParametersTestCases")
    @DisplayName("deleteById: when given parameters do not verify validations then bad request error is returned with validation errors")
    public void deleteById_whenGivenParametersDoNotVerifyValidations_thenBadRequestHttpCodeAndValidationErrorsAreReturned(Integer orderId,
                                                                                                                          ErrorResponseDto expectedResponse) {
        ResultActions result = mockMvc.perform(
                        delete(RestRoutes.ORDER.ROOT + RestRoutes.ORDER.BY_ID + "/" + orderId)
                );

        thenHttpErrorIsReturned(
                result,
                BAD_REQUEST,
                expectedResponse
        );
        verifyNoInteractions(mockService);
    }


    @Test
    @SneakyThrows
    @WithMockUser(
            authorities = { Constants.PERMISSIONS.DELETE_ORDER }
    )
    @DisplayName("deleteById: when based on provided parameters the service returns false then Http code Not Found is returned")
    public void deleteById_whenBasedOnProvidedParametersTheServiceReturnsFalse_thenHttpCodeNotFoundIsReturned() {
        Integer orderId = 1;

        when(mockService.deleteById(orderId))
                .thenReturn(
                        false
                );

        mockMvc.perform(
                        delete(RestRoutes.ORDER.ROOT + RestRoutes.ORDER.BY_ID + "/" + orderId)
                )
                .andExpect(
                        status().isNotFound()
                );

        verify(mockService, times(1))
                .deleteById(
                        orderId
                );
    }


    @Test
    @SneakyThrows
    @WithMockUser(
            authorities = { Constants.PERMISSIONS.DELETE_ORDER }
    )
    @DisplayName("deleteById: when based on provided parameters the service returns true then Http code No Content is returned")
    public void deleteById_whenBasedOnProvidedParametersTheServiceReturnsTrue_thenHttpCodeNoContentIsReturned() {
        Integer orderId = 1;

        when(mockService.deleteById(orderId))
                .thenReturn(
                        true
                );

        mockMvc.perform(
                        delete(RestRoutes.ORDER.ROOT + RestRoutes.ORDER.BY_ID + "/" + orderId)
                )
                .andExpect(
                        status().isNoContent()
                );

        verify(mockService, times(1))
                .deleteById(
                        orderId
                );
    }


    @Test
    @SneakyThrows
    @DisplayName("findByCode: when no logged user is given then unauthorized Http code is returned")
    public void findByCode_whenNoLoggedUserIsGiven_thenUnauthorizedHttpCodeIsReturned() {
        String code = StringUtil.urlEncode(
                "Order 1"
        );

        mockMvc.perform(
                        get(RestRoutes.ORDER.ROOT + RestRoutes.ORDER.BY_CODE + "/" + code)
                )
                .andExpect(
                        status().isUnauthorized()
                );

        verifyNoInteractions(mockService);
        verifyNoInteractions(mockConverter);
    }


    @Test
    @SneakyThrows
    @WithMockUser(
            authorities = { Constants.PERMISSIONS.CREATE_ORDER }
    )
    @DisplayName("findByCode: when no valid authority is given then forbidden Http code is returned")
    public void findByCode_whenNotValidAuthorityIsGiven_thenForbiddenHttpCodeIsReturned() {
        String code = StringUtil.urlEncode(
                "Order 1"
        );

        mockMvc.perform(
                        get(RestRoutes.ORDER.ROOT + RestRoutes.ORDER.BY_CODE + "/" + code)
                )
                .andExpect(
                        status().isForbidden()
                );

        verifyNoInteractions(mockService);
        verifyNoInteractions(mockConverter);
    }


    @Test
    @SneakyThrows
    @WithMockUser(
            authorities = { Constants.PERMISSIONS.GET_ORDER }
    )
    @DisplayName("findByCode: when based on provided parameters the service returns empty then Http code Not Found is returned")
    public void findByCode_whenBasedOnProvidedParametersTheServiceReturnsEmpty_thenHttpCodeNotFoundIsReturned() {
        String code = StringUtil.urlEncode(
                "Order 1"
        );

        when(mockService.findByCode(code))
                .thenReturn(
                        empty()
                );

        ResultActions result = mockMvc.perform(
                        get(RestRoutes.ORDER.ROOT + RestRoutes.ORDER.BY_CODE + "/" + code)
                )
                .andExpect(
                        status().isNotFound()
                );

        thenBodyIsReturned(
                result,
                NOT_FOUND,
                null,
                OrderDto.class
        );

        verify(mockService, times(1))
                .findByCode(
                        code
                );
        verifyNoInteractions(mockConverter);
    }


    @Test
    @SneakyThrows
    @WithMockUser(
            authorities = { Constants.PERMISSIONS.GET_ORDER }
    )
    @DisplayName("findByCode: when based on provided parameters the service returns a model then Http code Ok with the found Dto is returned")
    public void findByCode_whenBasedOnProvidedParametersTheServiceReturnsTrue_thenHttpCodeOkWithTheFoundDtoIsReturned() {
        OrderDto dto = buildNewOrderDtoWithOrderLine();
        Order model = buildNewOrderWithOrderLine();

        when(mockService.findByCode(dto.getCode()))
                .thenReturn(
                        of(model)
                );
        when(mockConverter.fromModelToDto(model))
                .thenReturn(
                        dto
                );

        ResultActions result = mockMvc.perform(
                        get(RestRoutes.ORDER.ROOT + RestRoutes.ORDER.BY_CODE + "/" + dto.getCode())
                                .contentType(APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(dto)
                                )
                )
                .andExpect(
                        content().contentType(APPLICATION_JSON)
                );

        thenBodyIsReturned(
                result,
                OK,
                dto,
                OrderDto.class
        );

        verify(mockService, times(1))
                .findByCode(
                        dto.getCode()
                );
        verify(mockConverter, times(1))
                .fromModelToDto(
                        model
                );
    }


    @Test
    @SneakyThrows
    @DisplayName("findById: when no logged user is given then unauthorized Http code is returned")
    public void findById_whenNoLoggedUserIsGiven_thenUnauthorizedHttpCodeIsReturned() {
        int id = 1;

        mockMvc.perform(
                        get(RestRoutes.ORDER.ROOT + RestRoutes.ORDER.BY_ID + "/" + id)
                )
                .andExpect(
                        status().isUnauthorized()
                );

        verifyNoInteractions(mockService);
        verifyNoInteractions(mockConverter);
    }


    @Test
    @SneakyThrows
    @WithMockUser(
            authorities = { Constants.PERMISSIONS.CREATE_ORDER }
    )
    @DisplayName("findById: when no valid authority is given then forbidden Http code is returned")
    public void findById_whenNotValidAuthorityIsGiven_thenForbiddenHttpCodeIsReturned() {
        int id = 1;

        mockMvc.perform(
                        get(RestRoutes.ORDER.ROOT + RestRoutes.ORDER.BY_ID + "/" + id)
                )
                .andExpect(
                        status().isForbidden()
                );

        verifyNoInteractions(mockService);
        verifyNoInteractions(mockConverter);
    }


    static Stream<Arguments> findById_invalidParametersTestCases() {
        ErrorResponseDto responseInvalidOrderId = new ErrorResponseDto(
                VALIDATION,
                List.of("Error in path: 'findById.id' due to: must be greater than 0")
        );
        return Stream.of(
                //@formatter:off
                //            id,   expectedResponse
                Arguments.of( -1,   responseInvalidOrderId ),
                Arguments.of( 0,    responseInvalidOrderId )
        ); //@formatter:on
    }

    @ParameterizedTest
    @SneakyThrows
    @WithMockUser(
            authorities = { Constants.PERMISSIONS.GET_ORDER }
    )
    @MethodSource("findById_invalidParametersTestCases")
    @DisplayName("findById: when given parameters do not verify validations then bad request error is returned with validation errors")
    public void findById_whenGivenParametersDoNotVerifyValidations_thenBadRequestHttpCodeAndValidationErrorsAreReturned(Integer id,
                                                                                                                        ErrorResponseDto expectedResponse) {
        ResultActions result = mockMvc.perform(
                get(RestRoutes.ORDER.ROOT + RestRoutes.ORDER.BY_ID + "/" + id)
        );

        thenHttpErrorIsReturned(
                result,
                BAD_REQUEST,
                expectedResponse
        );
        verifyNoInteractions(mockService);
    }


    @Test
    @SneakyThrows
    @WithMockUser(
            authorities = { Constants.PERMISSIONS.GET_ORDER }
    )
    @DisplayName("findById: when based on provided parameters the service returns empty then Http code Not Found is returned")
    public void findById_whenBasedOnProvidedParametersTheServiceReturnsEmpty_thenHttpCodeNotFoundIsReturned() {
        Integer id = 1;

        when(mockService.findById(id))
                .thenReturn(
                        empty()
                );

        ResultActions result = mockMvc.perform(
                        get(RestRoutes.ORDER.ROOT + RestRoutes.ORDER.BY_ID + "/" + id)
                )
                .andExpect(
                        status().isNotFound()
                );

        thenBodyIsReturned(
                result,
                NOT_FOUND,
                null,
                OrderDto.class
        );

        verify(mockService, times(1))
                .findById(
                        id
                );
        verifyNoInteractions(mockConverter);
    }


    @Test
    @SneakyThrows
    @WithMockUser(
            authorities = { Constants.PERMISSIONS.GET_ORDER }
    )
    @DisplayName("findById: when based on provided parameters the service returns a model then Http code Ok with the found Dto is returned")
    public void findById_whenBasedOnProvidedParametersTheServiceReturnsTrue_thenHttpCodeOkWithTheFoundDtoIsReturned() {
        OrderDto dto = buildOrderDtoWithOrderLine();
        Order model = buildOrderWithOrderLine();

        when(mockService.findById(dto.getId()))
                .thenReturn(
                        of(model)
                );
        when(mockConverter.fromModelToDto(model))
                .thenReturn(
                        dto
                );

        ResultActions result = mockMvc.perform(
                        get(RestRoutes.ORDER.ROOT + RestRoutes.ORDER.BY_ID + "/" + dto.getId())
                                .contentType(APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(dto)
                                )
                )
                .andExpect(
                        content().contentType(APPLICATION_JSON)
                );

        thenBodyIsReturned(
                result,
                OK,
                dto,
                OrderDto.class
        );

        verify(mockService, times(1))
                .findById(
                        dto.getId()
                );
        verify(mockConverter, times(1))
                .fromModelToDto(
                        model
                );
    }


    @Test
    @SneakyThrows
    @DisplayName("update: when no logged user is given then unauthorized Http code is returned")
    public void update_whenNoLoggedUserIsGiven_thenUnauthorizedHttpCodeIsReturned() {
        OrderDto dto = buildOrderDtoWithOrderLine();

        mockMvc.perform(
                        put(RestRoutes.ORDER.ROOT)
                                .contentType(APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(dto)
                                )
                )
                .andExpect(
                        status().isUnauthorized()
                );

        verifyNoInteractions(mockConverter);
        verifyNoInteractions(mockService);
    }


    @Test
    @SneakyThrows
    @WithMockUser(
            authorities = { Constants.PERMISSIONS.GET_ORDER }
    )
    @DisplayName("update: when no valid authority is given then forbidden Http code is returned")
    public void update_whenNotValidAuthorityIsGiven_thenForbiddenHttpCodeIsReturned() {
        OrderDto dto = buildOrderDtoWithOrderLine();

        mockMvc.perform(
                        put(RestRoutes.ORDER.ROOT)
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


    static Stream<Arguments> update_invalidParametersTestCases() {
        String longString = String.join("", Collections.nCopies(300, "a"));

        OrderDto dtoWithNoId = buildNewOrderDtoWithOrderLine();

        OrderDto dtoWithNoCode = buildOrderDtoWithOrderLine();
        dtoWithNoCode.setCode(null);

        OrderDto dtoWithLongCode = buildOrderDtoWithOrderLine();
        dtoWithLongCode.setCode(longString);

        OrderDto dtoWithOrderLineWithoutConcept = buildOrderDtoWithOrderLine();
        dtoWithOrderLineWithoutConcept.getOrderLines().getFirst().setConcept(null);

        OrderDto dtoWithOrderLineWithLongConcept = buildOrderDtoWithOrderLine();
        dtoWithOrderLineWithLongConcept.getOrderLines().getFirst().setConcept(longString);

        OrderDto dtoWithOrderLineWithNegativeAmount = buildOrderDtoWithOrderLine();
        dtoWithOrderLineWithNegativeAmount.getOrderLines().getFirst().setAmount(-1);

        OrderDto dtoWithOrderLineWithNegativeCost = buildOrderDtoWithOrderLine();
        dtoWithOrderLineWithNegativeCost.getOrderLines().getFirst().setCost(-2d);

        ErrorResponseDto responseDtoWithNoId = new ErrorResponseDto(
                VALIDATION,
                List.of("Field error in object: orderDto on field: id due to: must not be null")
        );
        ErrorResponseDto responseDtoWithNoCode = new ErrorResponseDto(
                VALIDATION,
                List.of("Field error in object: orderDto on field: code due to: must not be null")
        );
        ErrorResponseDto responseDtoWithLongCode = new ErrorResponseDto(
                VALIDATION,
                List.of("Field error in object: orderDto on field: code due to: size must be between 1 and 64")
        );
        ErrorResponseDto responseDtoWithOrderLineWithoutConcept = new ErrorResponseDto(
                VALIDATION,
                List.of("Field error in object: orderDto on field: orderLines[0].concept due to: must not be null")
        );
        ErrorResponseDto responseDtoWithOrderLineWithLongConcept = new ErrorResponseDto(
                VALIDATION,
                List.of("Field error in object: orderDto on field: orderLines[0].concept due to: size must be between 1 and 255")
        );
        ErrorResponseDto responseDtoWithOrderLineWithNegativeAmount = new ErrorResponseDto(
                VALIDATION,
                List.of("Field error in object: orderDto on field: orderLines[0].amount due to: must be greater than 0")
        );
        ErrorResponseDto responseDtoWithOrderLineWithNegativeCost = new ErrorResponseDto(
                VALIDATION,
                List.of("Field error in object: orderDto on field: orderLines[0].cost due to: must be greater than 0")
        );
        return Stream.of(
                //@formatter:off
                //            dtoToCreate,                          expectedResponse
                Arguments.of( dtoWithNoId,                          responseDtoWithNoId ),
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
    @WithMockUser(
            authorities = { Constants.PERMISSIONS.UPDATE_ORDER }
    )
    @MethodSource("update_invalidParametersTestCases")
    @DisplayName("update: when given parameters do not verify validations then bad request error is returned with validation errors")
    public void update_whenGivenParametersDoNotVerifyValidations_thenBadRequestHttpCodeAndValidationErrorsAreReturned(OrderDto dtoToCreate,
                                                                                                                      ErrorResponseDto expectedResponse) {
        ResultActions result = mockMvc.perform(
                        put(RestRoutes.ORDER.ROOT)
                                .contentType(APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(dtoToCreate)
                                )
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


    @Test
    @SneakyThrows
    @WithMockUser(
            authorities = { Constants.PERMISSIONS.UPDATE_ORDER }
    )
    @DisplayName("update: when given parameters verifies validations but service returns empty then Http code Not Found is returned")
    public void update_whenGivenParametersVerifiesValidationsButServiceReturnsEmpty_thenHttpCodeNotFoundIsReturned() {
        OrderDto dto = buildOrderDtoWithOrderLine();
        Order model = buildOrderWithOrderLine();

        when(mockConverter.fromDtoToModel(dto))
                .thenReturn(
                        model
                );
        when(mockService.save(model))
                .thenReturn(
                        empty()
                );

        ResultActions result = mockMvc.perform(
                put(RestRoutes.ORDER.ROOT)
                        .contentType(APPLICATION_JSON)
                        .content(
                                objectMapper.writeValueAsString(dto)
                        )
        );

        thenBodyIsReturned(
                result,
                NOT_FOUND,
                null,
                OrderDto.class
        );

        verify(mockConverter, times(1))
                .fromDtoToModel(
                        dto
                );
        verify(mockService, times(1))
                .save(
                        model
                );
        verify(mockConverter, never())
                .fromModelToDto(
                        any(Order.class)
                );
    }


    @Test
    @SneakyThrows
    @WithMockUser(
            authorities = { Constants.PERMISSIONS.UPDATE_ORDER }
    )
    @DisplayName("update: when given parameters verifies validations and service returns a model then Http code Ok with the updated Dto is returned")
    public void update_whenGivenParametersVerifiesValidationsAndServiceReturnAModel_thenHttpCodeOkdWithTheUpdatedDtoIsReturned() {
        OrderDto dto = buildOrderDtoWithOrderLine();
        Order model = buildOrderWithOrderLine();

        when(mockConverter.fromDtoToModel(dto))
                .thenReturn(
                        model
                );
        when(mockService.save(model))
                .thenReturn(
                        of(model)
                );
        when(mockConverter.fromModelToDto(model))
                .thenReturn(
                        dto
                );

        ResultActions result = mockMvc.perform(
                        put(RestRoutes.ORDER.ROOT)
                                .contentType(APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(dto)
                                )
                )
                .andExpect(
                        content().contentType(APPLICATION_JSON)
                );

        thenBodyIsReturned(
                result,
                OK,
                dto,
                OrderDto.class
        );

        verify(mockConverter, times(1))
                .fromDtoToModel(
                        dto
                );
        verify(mockService, times(1))
                .save(
                        model
                );
        verify(mockConverter, times(1))
                .fromModelToDto(
                        model
                );
    }


    private static Order buildOrderWithOrderLine() {
        Order order = buildNewOrderWithOrderLine();
        order.setId(
                1
        );
        return order;
    }


    private static Order buildNewOrderWithOrderLine() {
        Order order = buildOrder(
                "Order 1",
                new ArrayList<>()
        );
        OrderLine orderLine = buildOrderLine(
                order,
                "Keyboard",
                2,
                10.1d
        );
        order.setOrderLines(
                List.of(
                        orderLine
                )
        );
        return order;
    }


    private static OrderDto buildOrderDtoWithOrderLine() {
        OrderDto orderDto = buildNewOrderDtoWithOrderLine();
        orderDto.setId(
                1
        );
        orderDto.getOrderLines()
                .getFirst()
                .setOrderId(
                        1
                );
        return orderDto;
    }


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
