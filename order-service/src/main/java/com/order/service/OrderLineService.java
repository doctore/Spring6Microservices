package com.order.service;

import com.order.mapper.OrderLineMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class OrderLineService {

    private final OrderLineMapper mapper;


    @Autowired
    public OrderLineService(@Lazy final OrderLineMapper mapper) {
        this.mapper = mapper;
    }


}
