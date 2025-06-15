package com.invoice.service;

import com.invoice.repository.CustomerRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class CustomerService {

    private final CustomerRepository repository;


    @Autowired
    public CustomerService(@Lazy final CustomerRepository repository) {
        this.repository = repository;
    }

}
