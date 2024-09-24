package com.security.custom.repository;

import com.security.custom.model.ApplicationClientDetails;
import com.spring6microservices.common.spring.repository.ExtendedJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationClientDetailsRepository extends ExtendedJpaRepository<ApplicationClientDetails, String> {
}
