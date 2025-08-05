package com.invoice;

import com.invoice.dto.CustomerDto;
import com.invoice.dto.InvoiceDto;
import com.invoice.dto.OrderDto;
import com.invoice.dto.OrderLineDto;
import com.invoice.model.Customer;
import com.invoice.model.Invoice;
import com.spring6microservices.common.core.util.CollectionUtil;
import com.spring6microservices.common.spring.dto.page.PageDto;
import com.spring6microservices.common.spring.dto.page.SortDto;
import com.spring6microservices.grpc.OrderLineResponseGrpc;
import com.spring6microservices.grpc.OrderResponseGrpc;
import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@UtilityClass
public class TestDataFactory {

    public static Customer buildCustomer(final Integer id,
                                         final String code,
                                         final String address,
                                         final String phone,
                                         final String email) {
        return Customer.builder()
                .id(id)
                .code(code)
                .address(address)
                .phone(phone)
                .email(email)
                .createdAt(
                        LocalDateTime.now()
                )
                .build();
    }


    public static CustomerDto buildCustomerDto(final Integer id,
                                               final String code,
                                               final String address,
                                               final String phone,
                                               final String email) {
        return CustomerDto.builder()
                .id(id)
                .code(code)
                .address(address)
                .phone(phone)
                .email(email)
                .createdAt(
                        LocalDateTime.now()
                )
                .build();
    }


    public static Customer buildExistingCustomer1InDatabase() {
        return buildCustomer(
                1,
                "Customer 1",
                "Address of customer 1",
                "(+34) 123456789",
                "customer1@email.es"
        );
    }


    public static Customer buildExistingCustomer2InDatabase() {
        return buildCustomer(
                2,
                "Customer 2",
                "Address of customer 2",
                "(+34) 987654321",
                "customer2@email.es"
        );
    }


    public static Invoice buildInvoice(final Integer id,
                                       final String code,
                                       final Customer customer,
                                       final Integer orderId,
                                       final double cost) {
        return Invoice.builder()
                .id(id)
                .code(code)
                .customer(customer)
                .orderId(orderId)
                .cost(cost)
                .createdAt(
                        LocalDateTime.now()
                )
                .build();
    }


    public static InvoiceDto buildInvoiceDto(final Integer id,
                                             final String code,
                                             final CustomerDto customer,
                                             final OrderDto order,
                                             final double cost) {
        return InvoiceDto.builder()
                .id(id)
                .code(code)
                .customer(customer)
                .order(order)
                .cost(cost)
                .createdAt(
                        LocalDateTime.now()
                )
                .build();
    }


    public static Invoice buildExistingInvoice1InDatabase() {
        return buildInvoice(
                1,
                "Invoice 1",
                buildExistingCustomer1InDatabase(),
                buildOrderDto().getId(),
                10.1d
        );
    }


    public static Invoice buildExistingInvoice2InDatabase() {
        return buildInvoice(
                2,
                "Invoice 2",
                buildExistingCustomer2InDatabase(),
                2,
                911.5
        );
    }


    public static OrderDto buildOrderDto() {
        return buildOrderDto(
                1,
                "Order 1",
                List.of(
                        buildOrderLineDto()
                )
        );
    }


    public static OrderDto buildOrderDto(final Integer id,
                                         final String code,
                                         final Collection<OrderLineDto> orderLines) {
        return OrderDto.builder()
                .id(id)
                .code(code)
                .customerCode("Customer" + (null == id ? "" : " " + id))
                .createdAt(
                        LocalDateTime.now()
                )
                .orderLines(
                        new ArrayList<>(
                                orderLines
                        )
                )
                .build();
    }


    public static OrderResponseGrpc buildOrderResponseGrpc() {
        return OrderResponseGrpc.newBuilder()
                .setId(1)
                .setCode("Order 1")
                .setCustomerCode("Customer 1")
                .setCreatedAt("2025-11-19T05:30:00")
                .addOrderLines(
                        buildOrderLineResponseGrpc()
                )
                .build();
    }


    public static OrderResponseGrpc buildOrderResponseGrpc(final Integer id,
                                                           final String code,
                                                           final Collection<OrderLineResponseGrpc> orderLines) {
        OrderResponseGrpc.Builder builder = OrderResponseGrpc.newBuilder()
                .setId(id)
                .setCode(code)
                .setCustomerCode("Customer" + (null == id ? "" : " " + id))
                .setCreatedAt("2025-11-19T05:30:00");
        if (null != orderLines) {
            for (OrderLineResponseGrpc orderLine : orderLines) {
                builder.addOrderLines(
                        orderLine
                );
            }
        }
        return builder.build();
    }


    public static OrderLineDto buildOrderLineDto() {
        return buildOrderLineDto(
                1,
                "Order line 1",
                1,
                9.99d
        );
    }


    public static OrderLineDto buildOrderLineDto(final Integer id,
                                                 final String concept,
                                                 final int amount,
                                                 final double cost) {
        return OrderLineDto.builder()
                .id(id)
                .concept(concept)
                .amount(amount)
                .cost(cost)
                .build();
    }


    public static OrderLineResponseGrpc buildOrderLineResponseGrpc() {
        return OrderLineResponseGrpc.newBuilder()
                .setId(1)
                .setConcept("Order line 1")
                .setAmount(1)
                .setCost(9.99d)
                .build();
    }


    public static OrderLineResponseGrpc buildOrderLineResponseGrpc(final Integer id,
                                                                   final String concept,
                                                                   final int amount,
                                                                   final double cost) {
        return OrderLineResponseGrpc.newBuilder()
                .setId(id)
                .setConcept(concept)
                .setAmount(amount)
                .setCost(cost)
                .build();
    }


    public static PageDto buildPageDto(final int page,
                                       final int size,
                                       final Collection<SortDto> sortDtos) {
        return PageDto.builder()
                .page(page)
                .size(size)
                .sort(
                        CollectionUtil.isEmpty(sortDtos)
                                ? new ArrayList<>()
                                : new ArrayList<>(sortDtos)
                )
                .build();
    }


    public static SortDto buildSortDto(final String property,
                                       final boolean isAscending) {
        return SortDto.builder()
                .property(property)
                .isAscending(isAscending)
                .build();
    }


    public static <T> Page<T> buildEmptyPage(final Pageable pageable) {
        return new PageImpl<>(
                List.of(),
                pageable,
                0
        );
    }


    public static <T> Page<T> buildPage(final Pageable pageable,
                                        final List<T> content) {
        return new PageImpl<>(
                content,
                pageable,
                content.size()
        );
    }

}
