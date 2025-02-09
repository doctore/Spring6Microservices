package com.security.custom.exception;

import com.security.custom.model.ApplicationClientDetails;
import com.security.custom.service.ApplicationClientDetailsService;
import com.security.custom.service.AuthenticationService;

import java.io.Serial;

/**
 *    Thrown if, using the PKCE flow, the {@link ApplicationClientDetails#getId()} received in the first request is not the same
 * as the second one.
 *
 * @see AuthenticationService
 * @see <a href="http://google.com">https://oauth.net/2/pkce/</a>
 */
public class ApplicationClientMismatchException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 338648360450595760L;


    public ApplicationClientMismatchException() {
        super();
    }

    public ApplicationClientMismatchException(String message) {
        super(message);
    }

    public ApplicationClientMismatchException(String message,
                                              Throwable cause) {
        super(message, cause);
    }

    public ApplicationClientMismatchException(Throwable cause) {
        super(cause);
    }

    protected ApplicationClientMismatchException(String message,
                                                 Throwable cause,
                                                 boolean enableSuppression,
                                                 boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
