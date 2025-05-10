package com.order.service;

import com.order.mapper.OrderLineMapper;
import com.order.model.OrderLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

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
                        mapper.insert(
                                ol
                        );
                    }
                    else {
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
