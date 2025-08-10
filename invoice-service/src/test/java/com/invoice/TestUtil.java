package com.invoice;

import com.invoice.model.Customer;
import com.invoice.model.Invoice;
import com.spring6microservices.common.core.util.DateTimeUtil;
import com.spring6microservices.common.spring.configuration.Constants;
import com.spring6microservices.common.spring.dto.invoice.CustomerDto;
import com.spring6microservices.common.spring.dto.invoice.InvoiceDto;
import com.spring6microservices.common.spring.dto.order.OrderDto;
import com.spring6microservices.common.spring.dto.order.OrderLineDto;
import com.spring6microservices.grpc.OrderResponseGrpc;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

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
                        "customer",
                        "order"
                )
        );
        compareCustomerDtos(
                actual.getCustomer(),
                expected.getCustomer()
        );
        assertEquals(
                expected.getOrder().getId(),
                actual.getOrder().getId()
        );
        if (null != expected.getOrder().getCode() &&  null != actual.getOrder().getCode()) {
            compareOrderDtos(
                    expected.getOrder(),
                    actual.getOrder()
            );
        }
    }


    public static void compareOrderDtos(final OrderDto expected,
                                        final OrderDto actual) {
        assertNotNull(expected);
        assertNotNull(actual);
        assertEquals(
                expected.getId(),
                actual.getId()
        );
        assertEquals(
                expected.getCode(),
                actual.getCode()
        );
        assertEquals(
                expected.getCustomerCode(),
                actual.getCustomerCode()
        );
        if (null == expected.getOrderLines()) {
            assertNull(actual.getOrderLines());
        }
        else {
            assertEquals(
                    expected.getOrderLines().size(),
                    actual.getOrderLines().size()
            );
            for (int i = 0; i < expected.getOrderLines().size(); i++) {
                compareOrderLinesDtos(
                        expected.getOrderLines().get(i),
                        actual.getOrderLines().get(i)
                );
            }
        }
    }


    public static void compareOrderResponseGrpc(final OrderResponseGrpc expected,
                                                final OrderResponseGrpc actual) {
        assertNotNull(expected);
        assertNotNull(actual);
        assertEquals(
                expected.getId(),
                actual.getId()
        );
        assertEquals(
                expected.getCode(),
                actual.getCode()
        );
        assertEquals(
                expected.getCustomerCode(),
                actual.getCustomerCode()
        );
        if (null == expected.getOrderLinesList()) {
            assertNull(actual.getOrderLinesList());
        }
        else {
            assertEquals(
                    expected.getOrderLinesList().size(),
                    actual.getOrderLinesList().size()
            );
            for (int i = 0; i < expected.getOrderLinesList().size(); i++) {
                assertEquals(
                        expected.getOrderLinesList().get(i).getId(),
                        actual.getOrderLinesList().get(i).getId()
                );
                assertEquals(
                        expected.getOrderLinesList().get(i).getConcept(),
                        actual.getOrderLinesList().get(i).getConcept()
                );
                assertEquals(
                        expected.getOrderLinesList().get(i).getAmount(),
                        actual.getOrderLinesList().get(i).getAmount()
                );
                assertEquals(
                        expected.getOrderLinesList().get(i).getCost(),
                        actual.getOrderLinesList().get(i).getCost()
                );
            }
        }
    }


    public static void compareOrderLinesDtos(final OrderLineDto expected,
                                             final OrderLineDto actual) {
        assertNotNull(expected);
        assertNotNull(actual);
        assertEquals(
                expected.getId(),
                actual.getId()
        );
        assertEquals(
                expected.getConcept(),
                actual.getConcept()
        );
        assertEquals(
                expected.getAmount(),
                actual.getAmount()
        );
        assertEquals(
                expected.getCost(),
                actual.getCost()
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
        assertNull(invoice.getCost());
        assertNull(invoice.getCreatedAt());
    }


    public static void verifyEmptyInvoiceDto(final InvoiceDto invoice) {
        assertNotNull(invoice);
        assertNull(invoice.getId());
        assertNull(invoice.getCustomer());
        assertNull(invoice.getOrder());
        assertNull(invoice.getCost());
        assertNull(invoice.getCreatedAt());
    }


    public static void verifyEmptyOrderDto(final OrderDto order) {
        assertNotNull(order);
        assertEquals(
                0,
                order.getId()
        );
        assertEquals(
                "",
                order.getCode()
        );
        assertEquals(
                "",
                order.getCustomerCode()
        );
        assertNotNull(order.getCreatedAt());
        assertTrue(order.getOrderLines().isEmpty());
    }


    public static void verifyEmptyOrderLineDto(final OrderLineDto orderLine) {
        assertNotNull(orderLine);
        assertEquals(
                0,
                orderLine.getId()
        );
        assertEquals(
                "",
                orderLine.getConcept()
        );
        assertEquals(
                0,
                orderLine.getAmount()
        );
        assertEquals(
                0.0d,
                orderLine.getCost()
        );
    }


    public static String localDateTimeToJSONFormat(final LocalDateTime localDateTime) {
        return DateTimeUtil.format(
                localDateTime,
                Constants.DATETIME_FORMAT
        );
    }

}
