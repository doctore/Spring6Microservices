package com.invoice;

import com.invoice.dto.page.PageDto;
import com.invoice.dto.page.SortDto;
import com.invoice.model.Customer;
import com.invoice.model.Invoice;
import com.spring6microservices.common.core.util.CollectionUtil;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

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

}
