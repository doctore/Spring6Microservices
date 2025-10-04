package com.invoice.service;

import com.invoice.configuration.jms.JmsConsumerConfiguration;
import com.invoice.configuration.security.configuration.AuthorizationServerConfiguration;
import com.invoice.model.Invoice;
import com.spring6microservices.common.core.collection.tuple.Tuple2;
import com.spring6microservices.common.core.util.ExceptionUtil;
import com.spring6microservices.common.core.util.StringUtil;
import com.spring6microservices.common.spring.jms.JmsHeader;
import com.spring6microservices.common.spring.jms.dto.EventDto;
import com.spring6microservices.common.spring.jms.dto.OrderEventDto;
import com.spring6microservices.common.spring.util.HttpUtil;
import io.grpc.Status;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static java.lang.String.format;
import static java.util.Optional.*;

@Log4j2
@Service
public class JmsService {

    private final AuthorizationServerConfiguration authorizationConfiguration;

    private final InvoiceService invoiceService;


    @Autowired
    public JmsService(@Lazy final AuthorizationServerConfiguration authorizationConfiguration,
                      @Lazy final InvoiceService invoiceService) {
        this.authorizationConfiguration = authorizationConfiguration;
        this.invoiceService = invoiceService;
    }


    /**
     *    Process a new {@link EventDto} containing an {@link OrderEventDto} to use its information to create a new
     * {@link Invoice} based on it.
     *
     * @param eventDto
     *    {@link EventDto} with an instance of {@link OrderEventDto} as {@link EventDto#getBody()}
     *
     * @return {@link Optional} with the saved {@link Invoice} if provided {@code eventDto} is not {@code null},
     *         {@link Optional#empty()} otherwise
     */
    @KafkaListener(
            topics = JmsConsumerConfiguration.DEFAULT_TOPIC,
            groupId = JmsConsumerConfiguration.DEFAULT_GROUP_ID
    )
    public Optional<Invoice> processNewOrder(final EventDto<OrderEventDto> eventDto) {
        return ofNullable(eventDto)
                .flatMap(e -> {
                    try {
                        log.info(
                                format("Received a new event with id: %s and body: %s",
                                        e.getId(),
                                        e.getBody()
                                )
                        );
                        String resultVerifyingRequest = verifyBasicAuthRequest(
                                StringUtil.getOrElse(
                                        e.getMetadata().get(
                                                JmsHeader.AUTHORIZATION.name()
                                        ),
                                        ""
                                )
                        )
                        .orElse(StringUtil.EMPTY_STRING);

                        if (StringUtil.isNotBlank(resultVerifyingRequest)) {
                            log.error(
                                    format("There was an error verifying the request, the root cause was: %s",
                                            resultVerifyingRequest
                                    )
                            );
                            return empty();
                        }
                        return invoiceService.save(
                                e.getBody()
                        );

                    } catch (Throwable t) {
                        log.error(
                                format("There was an error managing the received event. %s",
                                        ExceptionUtil.getFormattedCurrentAndRootError(
                                                t
                                        )
                                ),
                                t
                        );
                        return empty();
                    }
                });
    }


    /**
     * Verifies the given Basic authentication data, returning the {@link Status} based on required checks.
     *
     * @param basicAuthentication
     *    {@link String} with Basic authentication data, that is, base64-encoded username and password
     *
     * @return {@link Status}
     */
    private Optional<String> verifyBasicAuthRequest(final String basicAuthentication) {
        if (StringUtil.isBlank(basicAuthentication)) {
            return of(
                    "Authentication data is missing"
            );
        }
        try {
            Tuple2<String, String> usernameAndPassword = HttpUtil.decodeBasicAuthentication(basicAuthentication);
            log.info(
                    format("Verifying authentication data of the JMS client identifier: %s",
                            usernameAndPassword._1
                    )
            );
            if (!usernameAndPassword._1.equals(authorizationConfiguration.getClientId())) {
                log.error(
                        format("Provided JMS client identifier: %s does not match with configured one: %s",
                                usernameAndPassword._1,
                                authorizationConfiguration.getClientId()
                        )
                );
                return of(
                        "Provided authentication is not valid"
                );
            }
            if (!usernameAndPassword._2.equals(authorizationConfiguration.getClientPassword())) {
                log.error("Provided JMS client password does not match with configured one");
                return of(
                        "Provided authentication is not valid"
                );
            }
            return empty();

        } catch (Exception e) {
            log.error(
                    format("There was an error trying to verify the basic authentication data: %s",
                            basicAuthentication
                    ),
                    e
            );
            return of(
                    "There was an error trying to verify provided authentication"
            );
        }
    }

}
