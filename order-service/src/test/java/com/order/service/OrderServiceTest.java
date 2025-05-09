package com.order.service;

import com.order.mapper.OrderMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderMapper mockMapper;

    @Mock
    private OrderLineService mockOrderLineService;

    private OrderService service;


    @BeforeEach
    public void init() {
        service = new OrderService(
                mockMapper,
                mockOrderLineService
        );
    }


    @Test
    @DisplayName("count: then mapper result is returned")
    public void count_thenMapperResultIsReturned() {
        long expectedResult = 12;

        when(mockMapper.count())
                .thenReturn(expectedResult);

        assertEquals(
                expectedResult,
                service.count()
        );
    }

}
