package com.invoice.service;

import com.invoice.configuration.jms.JmsConsumerConfiguration;
import com.invoice.configuration.security.configuration.AuthorizationServerConfiguration;
import com.invoice.model.Invoice;
import com.invoice.repository.CustomerRepository;
import com.invoice.repository.InvoiceRepository;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;
import static java.util.Optional.*;
import static java.util.Optional.of;

@Log4j2
@Service
public class InvoiceService {

    private final InvoiceRepository repository;

    private final AuthorizationServerConfiguration authorizationConfiguration;

    private final CustomerRepository customerRepository;


    @Autowired
    public InvoiceService(@Lazy final InvoiceRepository repository,
                          @Lazy final AuthorizationServerConfiguration authorizationConfiguration,
                          @Lazy final CustomerRepository customerRepository) {
        this.repository = repository;
        this.authorizationConfiguration = authorizationConfiguration;
        this.customerRepository = customerRepository;
    }


    /**
     * Returns how many {@link Invoice}s exist.
     *
     * @return number of existing {@link Invoice}s
     */
    public long count() {
        return repository.count();
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
    public Page<Invoice> findAll(@Nullable final Pageable pageable) {
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
     *    Returns an {@link Optional} with the {@link Invoice} if there is one which {@link Invoice#getOrderId()}
     * matches with {@code id}, {@link Optional#empty()} otherwise.
     *
     * @param orderId
     *    {@link Invoice#getOrderId()} to find
     *
     * @return {@link Optional} with the {@link Invoice} which {@code orderId} matches with the given one.
     *         {@link Optional#empty()} otherwise
     */
    public Optional<Invoice> findByOrderId(final Integer orderId) {
        return ofNullable(orderId)
                .flatMap(repository::findByOrderId);
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
                .map(i -> {
                    if (i.isNew()) {
                        i.setCreatedAt(
                                LocalDateTime.now()
                        );
                    }
                    return i;
                })
                .map(repository::save);
    }


    /**
     * Persist the information included in the given {@link OrderEventDto}.
     *
     * @param orderEventDto
     *    Source {@link OrderEventDto} to create the new {@link Invoice}
     *
     * @return {@link Optional} with the saved {@link Invoice} if provided {@code orderEventDto} is not {@code null},
     *         {@link Optional#empty()} if {@code orderEventDto} is {@code null}
     */
    public Optional<Invoice> save(final OrderEventDto orderEventDto) {
        return ofNullable(orderEventDto)
                .map(o ->
                    Invoice.builder()
                            .code(
                                    "Invoice of orderId: " + o.getId()
                            )
                            .customer(
                                    customerRepository.findByCode(
                                            o.getCustomerCode()
                                    )
                                    .orElseThrow(
                                            () -> new RuntimeException(
                                                    "Customer: " + o.getCustomerCode() + " not found"
                                            )
                                    )
                            )
                            .orderId(
                                    o.getId()
                            )
                            .cost(
                                    o.getCost()
                            )
                            .createdAt(
                                    LocalDateTime.now()
                            )
                            .build()
                    )
                .map(repository::save);
    }


    /**
     *    Persists the information included in the given {@link Collection} of {@link Invoice}s, inserting the new
     * and updating the existing ones.
     *
     * @param invoices
     *    {@link Collection} of {@link Invoice}s to save
     *
     * @return {@link List} with the updated {@link Invoice}s
     */
    public List<Invoice> saveAll(final Collection<Invoice> invoices) {
        return ofNullable(invoices)
                .map(repository::saveAll)
                .orElseGet(ArrayList::new);
    }


    /**
     * Process a new {@link OrderEventDto} to use its information to create a new {@link Invoice} based on it.
     *
     * @param eventDto
     *    {@link EventDto} with an instance of {@link OrderEventDto} as {@link EventDto#getBody()}
     *
     * @return {@link Optional} with the saved {@link Invoice} if provided {@code orderEventDto} is not {@code null},
     *         {@link Optional#empty()} if {@code orderEventDto} is {@code null}
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
                        return save(
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
