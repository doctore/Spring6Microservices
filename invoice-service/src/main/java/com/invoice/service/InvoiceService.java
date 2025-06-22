package com.invoice.service;

import com.invoice.model.Invoice;
import com.invoice.repository.InvoiceRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static java.util.Optional.ofNullable;

@Log4j2
@Service
public class InvoiceService {

    private final InvoiceRepository repository;


    @Autowired
    public InvoiceService(@Lazy final InvoiceRepository repository) {
        this.repository = repository;
    }


    /**
     *    Returns an {@link Optional} with the {@link Invoice} if there is one which {@link Invoice#getCode()}
     * matches with {@code code}, {@link Optional#empty()} otherwise.
     *
     * @param code
     *    {@link Invoice#getCode()} to find
     *
     * @return {@link Optional} with the {@link Invoice} which code matches with the given one.
     *         {@link Optional#empty()} otherwise
     */
    public Optional<Invoice> findByCode(final String code) {
        return ofNullable(code)
                .flatMap(repository::findByCode);
    }


    /**
     *    Returns an {@link Optional} with the {@link Invoice} if there is one which {@link Invoice#getId()}
     * matches with {@code id}, {@link Optional#empty()} otherwise.
     *
     * @param id
     *    {@link Invoice#getId()} to find
     *
     * @return {@link Optional} with the {@link Invoice} which identifier matches with the given one.
     *         {@link Optional#empty()} otherwise
     */
    public Optional<Invoice> findById(final Integer id) {
        return ofNullable(id)
                .flatMap(repository::findById);
    }

}
