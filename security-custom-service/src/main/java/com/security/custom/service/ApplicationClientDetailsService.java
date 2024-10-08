package com.security.custom.service;

import com.security.custom.exception.ApplicationClientNotFoundException;
import com.security.custom.model.ApplicationClientDetails;
import com.security.custom.repository.ApplicationClientDetailsRepository;
import com.security.custom.service.cache.ApplicationClientDetailsCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;

@Service
public class ApplicationClientDetailsService implements ReactiveUserDetailsService {

    private final ApplicationClientDetailsCacheService cacheService;

    private final ApplicationClientDetailsRepository repository;


    @Autowired
    public ApplicationClientDetailsService(@Lazy final ApplicationClientDetailsCacheService cacheService,
                                           @Lazy final ApplicationClientDetailsRepository repository) {
        this.cacheService = cacheService;
        this.repository = repository;
    }


    /**
     * Returns the {@link ApplicationClientDetails#getId()} that matches with {@code id}.
     *
     * @param id
     *    {@link ApplicationClientDetails#getId()} to search
     *
     * @return {@link Optional} of {@link ApplicationClientDetails} if exists, {@link Optional#empty()} otherwise
     *
     * @throws ApplicationClientNotFoundException if the given {@code clientId} does not exist in database
     */
    public ApplicationClientDetails findById(final String id) {
        return ofNullable(id)
                .map(i ->
                        cacheService.get(i)
                                .orElseGet(() ->
                                        repository.findById(i)
                                                .map(acd -> {
                                                    cacheService.put(
                                                            i,
                                                            acd
                                                    );
                                                    return acd;
                                                })
                                                .orElse(null)
                                    )
                )
                .orElseThrow(() ->
                        new ApplicationClientNotFoundException(
                                format("The given id: %s was not found in database",
                                        id)
                        )
                );
    }


    /**
     * Gets {@link UserDetails} information in database related with the given {@link ApplicationClientDetails#getId()}.
     *
     * @param id
     *    Identifier to search a coincidence in {@link ApplicationClientDetails#getId()}
     *
     * @return {@link Mono} of {@link UserDetails}
     *
     * @throws ApplicationClientNotFoundException if the given {@code clientId} does not exist in database.
     *         {@link AccountStatusUserDetailsChecker#check(UserDetails)} for more information about the {@link Exception}s
     *         that could be thrown
     */
    @Override
    public Mono<UserDetails> findByUsername(final String id) {
        UserDetails userDetails = findById(id);
        new AccountStatusUserDetailsChecker()
                .check(userDetails);
        return Mono.just(userDetails);
    }

}
