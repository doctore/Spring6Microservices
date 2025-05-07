package com.order.service;

import com.order.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final OrderMapper mapper;

    private final OrderLineService orderLineService;


    @Autowired
    public OrderService(@Lazy final OrderMapper mapper,
                        @Lazy final OrderLineService orderLineService) {
        this.mapper = mapper;
        this.orderLineService = orderLineService;
    }

}
