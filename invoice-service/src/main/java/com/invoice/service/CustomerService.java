package com.invoice.service;

import com.invoice.model.Customer;
import com.invoice.repository.CustomerRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@Log4j2
@Service
public class CustomerService {

    private final CustomerRepository repository;


    @Autowired
    public CustomerService(@Lazy final CustomerRepository repository) {
        this.repository = repository;
    }


    /**
     * Returns how many {@link Customer}s exist.
     *
     * @return number of existing {@link Customer}s
     */
    public long count() {
        return repository.count();
    }


    /**
     * Gets paged all the {@link Customer}s using the given {@link Pageable} to configure the required one.
     *
     * @apiNote
     *    If {@code pageable} is {@code null} then {@link CustomerRepository#findAll()} will be used.
     *
     * @param pageable
     *    {@link Pageable} with the desired page to get
     *
     * @return {@link Page} of {@link Customer}
     */
    public Page<Customer> findAll(@Nullable final Pageable pageable) {
        return ofNullable(pageable)
                .map(repository::findAllNoMemoryPagination)
                .orElseGet(()  ->
                        new PageImpl<>(
                                repository.findAll()
                        )
                );
    }


    /**
     *    Returns an {@link Optional} with the {@link Customer} if there is one which {@link Customer#getCode()}
     * matches with {@code code}, {@link Optional#empty()} otherwise.
     *
     * @param code
     *    {@link Customer#getCode()} to find
     *
     * @return {@link Optional} with the {@link Customer} which code matches with the given one.
     *         {@link Optional#empty()} otherwise
     */
    public Optional<Customer> findByCode(final String code) {
        return ofNullable(code)
                .flatMap(repository::findByCode);
    }


    /**
     *    Returns an {@link Optional} with the {@link Customer} if there is one which {@link Customer#getId()}
     * matches with {@code id}, {@link Optional#empty()} otherwise.
     *
     * @param id
     *    {@link Customer#getId()} to find
     *
     * @return {@link Optional} with the {@link Customer} which identifier matches with the given one.
     *         {@link Optional#empty()} otherwise
     */
    public Optional<Customer> findById(final Integer id) {
        return ofNullable(id)
                .flatMap(repository::findById);
    }


    /**
     * Persist the information included in the given {@link Customer}.
     *
     * @param customer
     *    {@link Customer} to save
     *
     * @return {@link Optional} with the saved {@link Customer} if provided {@code customer} is not {@code null},
     *         {@link Optional#empty()} if {@code customer} is {@code null}
     */
    public Optional<Customer> save(final Customer customer) {
        return ofNullable(customer)
                .map(c -> {
                    if (c.isNew()) {
                        c.setCreatedAt(
                                LocalDateTime.now()
                        );
                    }
                    return c;
                })
                .map(repository::save);
    }


    /**
     *    Persists the information included in the given {@link Collection} of {@link Customer}s, inserting the new
     * and updating the existing ones.
     *
     * @param customers
     *    {@link Collection} of {@link Customer}s to save
     *
     * @return {@link List} with the updated {@link Customer}s
     */
    public List<Customer> saveAll(final Collection<Customer> customers) {
        return ofNullable(customers)
                .map(repository::saveAll)
                .orElseGet(ArrayList::new);
    }

}
