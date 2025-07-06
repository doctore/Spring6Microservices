package com.order.service;

import com.order.mapper.OrderMapper;
import com.order.model.Order;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

@Log4j2
@Service
public class OrderService {

    private final OrderMapper mapper;

    private final OrderLineService orderLineService;


    @Autowired
    public OrderService(@Lazy final OrderMapper mapper,
                        @Lazy final OrderLineService orderLineService) {
        this.mapper = mapper;
        this.orderLineService = orderLineService;
    }


    /**
     * Returns how many {@link Order}s exist.
     *
     * @return number of existing {@link Order}s
     */
    public long count() {
        return mapper.count();
    }


    /**
     *    Deletes the {@link Order} which identifier matches with to provided {@code id}. This method will also remove
     * the {@link Order#getOrderLines()} related with the given {@code id}.
     *
     * @param id
     *    {@link Order#getId()} to search
     *
     * @return {@code true} if the {@link Order} was deleted,
     *         {@code false} otherwise
     */
    public boolean deleteById(final Integer id) {
        if (null == id) {
            return false;
        }
        orderLineService.deleteByOrderId(id);
        return 0 != mapper.deleteById(id);
    }


    /**
     *    Deletes the {@link Order} which identifier matches with to provided {@code code}. This method will also remove
     * the {@link Order#getOrderLines()} related with the given {@code code}.
     *
     * @param code
     *    {@link Order#getCode()} to search
     *
     * @return {@code true} if the {@link Order} was deleted,
     *         {@code false} otherwise
     */
    public boolean deleteByCode(final String code) {
        if (null == code) {
            return false;
        }
        return ofNullable(
                mapper.findByCode(code)
        )
        .map(order ->
                this.deleteById(
                        order.getId()
                )
        )
        .orElse(false);
    }


    /**
     *    Returns an {@link Optional} with the {@link Order} if there is one which {@link Order#getId()}
     * matches with {@code id}, {@link Optional#empty()} otherwise.
     *
     * @param id
     *    {@link Order#getId()} to find
     *
     * @return {@link Optional} with the {@link Order} which identifier matches with the given one.
     *         {@link Optional#empty()} otherwise
     */
    public Optional<Order> findById(final Integer id) {
        return ofNullable(id)
                .map(mapper::findById);
    }


    /**
     *    Returns an {@link Optional} with the {@link Order} if there is one which {@link Order#getCode()}
     * matches with {@code code}, {@link Optional#empty()} otherwise.
     *
     * @param code
     *    {@link Order#getCode()} to find
     *
     * @return {@link Optional} with the {@link Order} which code matches with the given one.
     *         {@link Optional#empty()} otherwise
     */
    public Optional<Order> findByCode(final String code) {
        return ofNullable(code)
                .map(mapper::findByCode);
    }


    /**
     *    Persists the information included in the given {@link Order}, inserting if it is new or updating
     * when the {@code order} exists. This method will also save the {@link Order#getOrderLines()} related
     * with the given {@code order}.
     *
     * @param order
     *    {@link Order} to save
     *
     * @return {@link Optional} with the updated {@link Order},
     *         {@link Optional#empty()} if {@code order} is {@code null}
     */
    public Optional<Order> save(final Order order) {
        return ofNullable(order)
                .map(o -> {
                    if (o.isNew()) {
                        log.info(
                                format("Saving new order with code: %s",
                                        order.getCode()
                                )
                        );
                        o.setCreatedAt(
                                LocalDateTime.now()
                        );
                        mapper.insert(
                                o
                        );
                    }
                    else {
                        log.info(
                                format("Updating existing order: %s",
                                        order.getId()
                                )
                        );
                        mapper.update(
                                o
                        );
                    }
                    orderLineService.saveAll(
                            o.getOrderLines()
                    );
                    return o;
                });
    }


    /**
     *    Persists the information included in the given {@link Collection} of {@link Order}s, inserting the new
     * and updating the existing ones.
     *
     * @param orders
     *    {@link Collection} of {@link Order}s to save
     *
     * @return {@link List} with the updated {@link Order}s
     */
    public List<Order> saveAll(final Collection<Order> orders) {
        return ofNullable(orders)
                .map(o ->
                        o.stream()
                                .map(this::save)
                                .filter(Optional::isPresent)
                                .map(Optional::get)
                                .collect(
                                        toList()
                                )
                )
                .orElseGet(ArrayList::new);
    }

}
