package com.security.custom.repository;

import com.security.custom.model.ApplicationClientDetails;
import com.spring6microservices.common.spring.repository.ExtendedJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApplicationClientDetailsRepository extends ExtendedJpaRepository<ApplicationClientDetails, String> {

    @Override
    @Query(value = "SELECT acd "
                 + "FROM ApplicationClientDetails acd "
                 + "WHERE acd.id = :id")
    Optional<ApplicationClientDetails> findById(@Nullable @Param("id") String id);

}
