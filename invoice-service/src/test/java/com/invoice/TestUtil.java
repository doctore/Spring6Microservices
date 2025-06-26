package com.invoice;

import com.invoice.dto.CustomerDto;
import com.invoice.dto.InvoiceDto;
import com.invoice.model.Customer;
import com.invoice.model.Invoice;
import lombok.experimental.UtilityClass;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.*;

@UtilityClass
public class TestUtil {

    public static void compareCustomers(final Customer expected,
                                        final Customer actual) {
        assertNotNull(expected);
        assertNotNull(actual);
        assertThat(
                actual,
                samePropertyValuesAs(
                        expected,
                        "createdAt"
                )
        );
    }


    public static void compareCustomerDtos(final CustomerDto expected,
                                           final CustomerDto actual) {
        assertNotNull(expected);
        assertNotNull(actual);
        assertThat(
                actual,
                samePropertyValuesAs(
                        expected,
                        "createdAt"
                )
        );
    }


    public static void compareInvoices(final Invoice expected,
                                       final Invoice actual) {
        assertNotNull(expected);
        assertNotNull(actual);
        assertThat(
                actual,
                samePropertyValuesAs(
                        expected,
                        "createdAt",
                        "customer"
                )
        );
        compareCustomers(
                actual.getCustomer(),
                expected.getCustomer()
        );
    }


    public static void compareInvoiceDtos(final InvoiceDto expected,
                                          final InvoiceDto actual) {
        assertNotNull(expected);
        assertNotNull(actual);
        assertThat(
                actual,
                samePropertyValuesAs(
                        expected,
                        "createdAt",
                        "customer"
                )
        );
        compareCustomerDtos(
                actual.getCustomer(),
                expected.getCustomer()
        );
    }


    public static void verifyEmptyCustomer(final Customer customer) {
        assertNotNull(customer);
        assertNull(customer.getId());
        assertNull(customer.getCode());
        assertNull(customer.getAddress());
        assertNull(customer.getPhone());
        assertNull(customer.getEmail());
        assertNull(customer.getCreatedAt());
    }


    public static void verifyEmptyCustomerDto(final CustomerDto customer) {
        assertNotNull(customer);
        assertNull(customer.getId());
        assertNull(customer.getCode());
        assertNull(customer.getAddress());
        assertNull(customer.getPhone());
        assertNull(customer.getEmail());
        assertNull(customer.getCreatedAt());
    }


    public static void verifyEmptyInvoice(final Invoice invoice) {
        assertNotNull(invoice);
        assertNull(invoice.getId());
        assertNull(invoice.getCustomer());
        assertNull(invoice.getOrderId());
        assertEquals(
                0d,
                invoice.getCost()
        );
        assertNull(invoice.getCreatedAt());
    }


    public static void verifyEmptyInvoiceDto(final InvoiceDto invoice) {
        assertNotNull(invoice);
        assertNull(invoice.getId());
        assertNull(invoice.getCustomer());
        assertNull(invoice.getOrderId());
        assertEquals(
                0d,
                invoice.getCost()
        );
        assertNull(invoice.getCreatedAt());
    }

}
