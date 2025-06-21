package com.invoice;

import com.invoice.model.Customer;
import com.invoice.model.Invoice;
import lombok.experimental.UtilityClass;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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

}
