package com.security.custom.service;

import com.security.custom.exception.ApplicationClientNotFoundException;
import com.security.custom.model.ApplicationClientDetails;
import com.security.custom.model.validator.ApplicationClientDetailsValidator;
import com.security.custom.repository.ApplicationClientDetailsRepository;
import com.security.custom.service.cache.ApplicationClientDetailsCacheService;
import com.spring6microservices.common.core.functional.validation.Validation;
import com.spring6microservices.common.core.functional.validation.ValidationError;
import com.spring6microservices.common.core.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;

@Service
public class ApplicationClientDetailsService implements ReactiveUserDetailsService {

    private final ApplicationClientDetailsCacheService cacheService;

    private final ApplicationClientDetailsRepository repository;


    @Autowired
    public ApplicationClientDetailsService(final ApplicationClientDetailsCacheService cacheService,
                                           final ApplicationClientDetailsRepository repository) {
        this.cacheService = cacheService;
        this.repository = repository;
    }


    /**
     * Returns the {@link ApplicationClientDetails} that matches with {@code id}.
     *
     * @param id
     *    {@link ApplicationClientDetails#getId()} to search
     *
     * @return {@link ApplicationClientDetails} if exists
     *
     * @throws ApplicationClientNotFoundException if the given {@code id} does not exist neither in database nor cache
     * @throws UnsupportedOperationException if the returned {@link ApplicationClientDetails} is not a valid one
     *                                       (it was not well configured)
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
                .map(this::validateApplicationClientDetailsOrThrow)
                .orElseThrow(() ->
                        new ApplicationClientNotFoundException(
                                format("The given id: %s was not found in database",
                                        id
                                )
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
     * @throws ApplicationClientNotFoundException if the given {@code id} does not exist in database.
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


    /**
     *    Returns the given {@code applicationClientDetails} if it was well configured, otherwise throws a
     * {@link UnsupportedOperationException} containing them.
     *
     * @param applicationClientDetails
     *    {@link ApplicationClientDetails} to verify
     *
     * @return {@code applicationClientDetails} if the instance was well configured,
     *         throws {@link UnsupportedOperationException} otherwise
     *
     * @throws UnsupportedOperationException if {@code applicationClientDetails} was not well configured
     */
    private ApplicationClientDetails validateApplicationClientDetailsOrThrow(final ApplicationClientDetails applicationClientDetails) {
        Validation<ValidationError, ApplicationClientDetails> validation = new ApplicationClientDetailsValidator()
                .validate(applicationClientDetails);

        if (validation.isValid()) {
            return applicationClientDetails;
        }
        throw new UnsupportedOperationException(
                format("The application client details: %s was not well configured. Error messages: %s",
                        applicationClientDetails.getId(),
                        StringUtil.join(
                                validation.getErrors()
                        )
                )
        );
    }

}
