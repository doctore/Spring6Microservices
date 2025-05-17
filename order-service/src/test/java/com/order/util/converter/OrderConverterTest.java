package com.order.util.converter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(
        classes = {
                OrderConverterImpl.class,
                OrderConverterImpl_.class,
                OrderLineConverterImpl.class
        }
)
public class OrderConverterTest {

    @Autowired
    private OrderConverter converter;


    // TODO:

    @Test
    public void dummy() {
        assertNotNull(converter);
    }

}
