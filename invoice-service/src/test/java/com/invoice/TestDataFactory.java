package com.invoice;

import com.invoice.model.Customer;
import com.invoice.model.Invoice;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

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

}
