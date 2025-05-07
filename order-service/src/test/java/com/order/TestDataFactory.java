package com.order;

import com.order.model.Order;
import com.order.model.OrderLine;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

@UtilityClass
public class TestDataFactory {


    public static Order buildOrder(final String code,
                                   final Collection<OrderLine> orderLines) {
        return buildOrder(
                null,
                code,
                orderLines
        );
    }


    public static Order buildOrder(final Integer id,
                                   final String code,
                                   final Collection<OrderLine> orderLines) {
        return new Order(
                id,
                code,
                LocalDateTime.now(),
                new ArrayList<>(
                        orderLines
                )
        );
    }


    public static OrderLine buildOrderLine(final Order order,
                                           final String concept,
                                           final int amount,
                                           final double cost) {
        return buildOrderLine(
                null,
                order,
                concept,
                amount,
                cost
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
