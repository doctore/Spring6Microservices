package com.invoice.service;

import com.invoice.repository.InvoiceRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class InvoiceService {

    private final InvoiceRepository repository;


    @Autowired
    public InvoiceService(@Lazy final InvoiceRepository repository) {
        this.repository = repository;
    }

}
