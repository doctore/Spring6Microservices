package com.order;

import com.order.model.Order;
import com.order.model.OrderLine;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.util.List;

@UtilityClass
public class TestDataFactory {

    public static Order buildOrder(final Integer id,
                                   final String code,
                                   final List<OrderLine> orderLines) {
        return new Order(
                id,
                code,
                LocalDateTime.now(),
                orderLines
        );
    }


    public static OrderLine buildOrderLine(final Integer id,
                                           final Order order,
                                           final String concept,
                                           final int amount,
                                           final double cost) {
        return new OrderLine(
                id,
                order,
                concept,
                amount,
                cost
        );
    }

}
