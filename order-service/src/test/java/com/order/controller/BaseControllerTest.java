package com.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring6microservices.common.spring.dto.ErrorResponseDto;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public abstract class BaseControllerTest {

    @Autowired
    protected ApplicationContext context;

    @Autowired
    protected ObjectMapper objectMapper;


    /**
     * Checks the expected result in Controller layer related tests when the endpoint should return an error.
     *
     * @param webResult
     *    {@link ResultActions} with the result of {@link MockMvc#perform(RequestBuilder)}
     * @param expectedHttpCode
     *    {@link HttpStatus} with expected returned Http code
     * @param errorResponse
     *    {@link ErrorResponseDto} with information about the problem
     */
    @SneakyThrows
    protected void thenHttpErrorIsReturned(final ResultActions webResult,
                                           final HttpStatus expectedHttpCode,
                                           final ErrorResponseDto errorResponse) {
        webResult.andExpect(
                status().is(
                        expectedHttpCode.value()
                )
        );
        assertEquals(
                errorResponse,
                objectMapper.readValue(
                        webResult.andReturn().getResponse().getContentAsString(),
                        ErrorResponseDto.class
                )
        );
    }

}
