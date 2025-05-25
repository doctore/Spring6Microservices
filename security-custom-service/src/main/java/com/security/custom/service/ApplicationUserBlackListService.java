package com.security.custom.service;

import com.security.custom.model.ApplicationClientDetails;
import com.security.custom.service.cache.ApplicationUserBlackListCacheService;
import com.spring6microservices.common.spring.exception.UnauthorizedException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Log4j2
@Service
public class ApplicationUserBlackListService {

    private final ApplicationUserBlackListCacheService cacheService;


    @Autowired
    public ApplicationUserBlackListService(final ApplicationUserBlackListCacheService cacheService) {
        this.cacheService = cacheService;
    }


    /**
     * Checks if the given pair {@code applicationClientDetailsId} and {@code username} were blacklisted.
     *
     * @param applicationClientDetailsId
     *    {@link ApplicationClientDetails#getId()}
     * @param username
     *    {@link String} with the user's identifier
     *
     * @return {@code true} if the pair {@code applicationClientDetailsId} and {@code username} are blacklisted,
     *         {@code false} otherwise
     */
    public boolean contains(final String applicationClientDetailsId,
                            final String username) {
        if (!areApplicationClientDetailsIdAndUsernameValid(applicationClientDetailsId, username)) {
            return false;
        }
        return cacheService.contains(
                applicationClientDetailsId,
                username
        );
    }


    /**
     *    Checks if the given pair {@code applicationClientDetailsId} and {@code username} were not blacklisted,
     * otherwise throws a {@link UnauthorizedException}.
     *
     * @param applicationClientDetailsId
     *    {@link ApplicationClientDetails#getId()}
     * @param username
     *    {@link String} with the user's identifier
     *
     * @throws UnauthorizedException if {@code applicationClientDetailsId} and {@code username} were blacklisted
     */
    public void notBlackListedOrThrow(final String applicationClientDetailsId,
                                      final String username) {
        if (contains(applicationClientDetailsId, username)) {
            throw new UnauthorizedException(
                    format("The application identifier: %s and username: %s are blacklisted",
                            applicationClientDetailsId,
                            username
                    )
            );
        }
    }


    /**
     * Removes the given pair {@code applicationClientDetailsId} and {@code username} from the blacklist.
     *
     * @param applicationClientDetailsId
     *    {@link ApplicationClientDetails#getId()}
     * @param username
     *    {@link String} with the user's identifier
     *
     * @return {@code true} if the pair {@code applicationClientDetailsId} and {@code username} were removed,
     *         {@code false} otherwise
     */
    public boolean remove(final String applicationClientDetailsId,
                          final String username) {
        log.info(
                format("Removing the application identifier: %s and username: %s from the blacklist",
                        applicationClientDetailsId,
                        username
                )
        );
        if (!areApplicationClientDetailsIdAndUsernameValid(applicationClientDetailsId, username)) {
            return false;
        }
        final boolean result = cacheService.remove(
                applicationClientDetailsId,
                username
        );
        log.info(
                format("Removing from the blacklist, the result was: %s",
                        result
                )
        );
        return result;
    }


    /**
     * Includes the given pair {@code applicationClientDetailsId} and {@code username} in the blacklist.
     *
     * @param applicationClientDetailsId
     *    {@link ApplicationClientDetails#getId()}
     * @param username
     *    {@link String} with the user's identifier
     *
     * @return {@code true} if the pair {@code applicationClientDetailsId} and {@code username} were added,
     *         {@code false} otherwise
     */
    public boolean save(final String applicationClientDetailsId,
                        final String username) {
        log.info(
                format("Saving the application identifier: %s and username: %s in the blacklist",
                        applicationClientDetailsId,
                        username
                )
        );
        if (!areApplicationClientDetailsIdAndUsernameValid(applicationClientDetailsId, username)) {
            return false;
        }
        final boolean result = cacheService.put(
                applicationClientDetailsId,
                username
        );
        log.info(
                format("Saving in the blacklist, the result was: %s",
                        result
                )
        );
        return result;
    }


    /**
     * Checks if the given pair {@code applicationClientDetailsId} and {@code username} contains valid values.
     *
     * @param applicationClientDetailsId
     *    {@link ApplicationClientDetails#getId()}
     * @param username
     *    {@link String} with the user's identifier
     *
     * @return {@code true} if the pair {@code applicationClientDetailsId} and {@code username} verifies the expected checks,
     *         {@code false} otherwise
     */
    private boolean areApplicationClientDetailsIdAndUsernameValid(final String applicationClientDetailsId,
                                                                  final String username) {
        return null != applicationClientDetailsId &&
                null != username;
    }

}
