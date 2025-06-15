package com.invoice.repository;

import com.invoice.model.Customer;
import com.spring6microservices.common.spring.repository.ExtendedJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends ExtendedJpaRepository<Customer, Integer> {}
