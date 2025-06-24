package com.invoice.service;

import com.invoice.model.Invoice;
import com.invoice.repository.InvoiceRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
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
     * Gets paged all the {@link Invoice}s using the given {@link Pageable} to configure the required one.
     *
     * @apiNote
     *    If {@code pageable} is {@code null} then {@link InvoiceRepository#findAll()} will be used.
     *
     * @param pageable
     *    {@link Pageable} with the desired page to get
     *
     * @return {@link Page} of {@link Invoice}
     */
    Page<Invoice> findAll(@Nullable final Pageable pageable) {
        return ofNullable(pageable)
                .map(repository::findAllNoMemoryPagination)
                .orElseGet(()  ->
                        new PageImpl<>(
                                repository.findAll()
                        )
                );
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


    /**
     * Persist the information included in the given {@link Invoice}.
     *
     * @param invoice
     *    {@link Invoice} to save
     *
     * @return {@link Optional} with the saved {@link Invoice} if provided {@code invoice} is not {@code null},
     *         {@link Optional#empty()} if {@code invoice} is {@code null}
     */
    public Optional<Invoice> save(final Invoice invoice) {
        return ofNullable(invoice)
                .map(repository::save);
    }

}
