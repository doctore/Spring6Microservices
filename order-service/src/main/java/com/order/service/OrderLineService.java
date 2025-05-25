package com.order.service;

import com.order.mapper.OrderLineMapper;
import com.order.model.OrderLine;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

@Log4j2
@Service
public class OrderLineService {

    private final OrderLineMapper mapper;


    @Autowired
    public OrderLineService(@Lazy final OrderLineMapper mapper) {
        this.mapper = mapper;
    }


    /**
     * Returns how many {@link OrderLine}s exist.
     *
     * @return number of existing {@link OrderLine}s
     */
    public long count() {
        return mapper.count();
    }


    /**
     * Deletes the {@link OrderLine} which identifier matches with to provided {@code id}.
     *
     * @param id
     *    {@link OrderLine#getId()} to search
     *
     * @return {@code true} if the {@link OrderLine} was deleted,
     *         {@code false} otherwise
     */
    public boolean deleteById(final Integer id) {
        if (null == id) {
            return false;
        }
        return 0 != mapper.deleteById(id);
    }


    /**
     * Deletes the {@link OrderLine}s belonging to the given {@code orderId}.
     *
     * @param orderId
     *    {@link OrderLine#getOrder()}'s identifier to search
     *
     * @return {@code true} if the {@link OrderLine}s were deleted,
     *         {@code false} otherwise
     */
    public boolean deleteByOrderId(final Integer orderId) {
        if (null == orderId) {
            return false;
        }
        return 0 != mapper.deleteByOrderId(orderId);
    }


    /**
     *    Returns an {@link Optional} with the {@link OrderLine} if there is one which {@link OrderLine#getId()}
     * matches with {@code id}, {@link Optional#empty()} otherwise.
     *
     * @param id
     *    {@link OrderLine#getId()} to find
     *
     * @return {@link Optional} with the {@link OrderLine} which identifier matches with the given one.
     *         {@link Optional#empty()} otherwise
     */
    public Optional<OrderLine> findById(final Integer id) {
        return ofNullable(id)
                .map(mapper::findById);
    }


    /**
     *    Returns the {@link List} of {@link OrderLine} which {@link OrderLine#getConcept()} contains provided {@code concept}.
     * The comparison is not case-sensitive, that is, searching "trAvel", for example, {@link OrderLine#getConcept()} like:
     * "Travel to Canary Islands", "TRAVEL TO MALAGA" will be returned.
     *
     * @apiNote
     *    If {@code concept} is {@code null} then an empty {@link List} will be returned.
     *
     * @param concept
     *    {@link OrderLine#getConcept()} to search
     *
     * @return {@link List} of {@link OrderLine}s that contains the given {@code concept}
     */
    public List<OrderLine> findByConcept(final String concept) {
        return ofNullable(concept)
                .map(mapper::findByConcept)
                .orElseGet(ArrayList::new);
    }


    /**
     * Returns the {@link OrderLine}s belonging to the given {@code orderId}.
     *
     * @param orderId
     *    {@link OrderLine#getOrder()}'s identifier to search
     *
     * @return {@link List} of {@link OrderLine} related with provided {@code orderId}
     */
    public List<OrderLine> findByOrderId(final Integer orderId) {
        return ofNullable(orderId)
                .map(mapper::findByOrderId)
                .orElseGet(ArrayList::new);
    }


    /**
     *    Persists the information included in the given {@link OrderLine}, inserting if it is new or updating
     * when the {@code orderLine} exists.
     *
     * @param orderLine
     *    {@link OrderLine} to save
     *
     * @return {@link Optional} with the updated {@link OrderLine},
     *         {@link Optional#empty()} if {@code orderLine} is {@code null}
     */
    public Optional<OrderLine> save(final OrderLine orderLine) {
        return ofNullable(orderLine)
                .map(ol -> {
                    if (ol.isNew()) {
                        log.info(
                                format("Saving new orderLine with concept: %s",
                                        orderLine.getConcept()
                                )
                        );
                        mapper.insert(
                                ol
                        );
                    }
                    else {
                        log.info(
                                format("Updating existing orderLine: %s",
                                        orderLine.getId()
                                )
                        );
                        mapper.update(
                                ol
                        );
                    }
                    return ol;
                });
    }


    /**
     *    Persists the information included in the given {@link Collection} of {@link OrderLine}s, inserting the new
     * and updating the existing ones.
     *
     * @param orderLines
     *    {@link Collection} of {@link OrderLine}s to save
     *
     * @return {@link List} with the updated {@link OrderLine}s
     */
    public List<OrderLine> saveAll(final Collection<OrderLine> orderLines) {
        return ofNullable(orderLines)
                .map(olines ->
                        olines.stream()
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
