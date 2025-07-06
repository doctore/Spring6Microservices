package com.invoice.model;

import com.invoice.configuration.persistence.PersistenceConfiguration;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
@Entity
@Getter
@NoArgsConstructor
@Setter
@Table(
        name = PersistenceConfiguration.TABLE.INVOICE,
        schema = PersistenceConfiguration.SCHEMA
)
@SqlResultSetMappings(
        value = {
                @SqlResultSetMapping(
                        name = Invoice.INVOICE_CUSTOMER_MAPPING,
                        entities = {
                                @EntityResult(
                                        entityClass = Invoice.class
                                ),
                                @EntityResult(
                                        entityClass = Customer.class,
                                        fields = {
                                                @FieldResult(
                                                        name = Customer.ID_PROPERTY,
                                                        column = PersistenceConfiguration.TABLE.CUSTOMER + "_" + Customer.ID_COLUMN
                                                ),
                                                @FieldResult(
                                                        name = Customer.CODE_PROPERTY,
                                                        column = PersistenceConfiguration.TABLE.CUSTOMER + "_" + Customer.CODE_COLUMN
                                                ),
                                                @FieldResult(
                                                        name = Customer.ADDRESS_PROPERTY,
                                                        column = PersistenceConfiguration.TABLE.CUSTOMER + "_" + Customer.ADDRESS_COLUMN
                                                ),
                                                @FieldResult(
                                                        name = Customer.PHONE_PROPERTY,
                                                        column = PersistenceConfiguration.TABLE.CUSTOMER + "_" + Customer.PHONE_COLUMN
                                                ),
                                                @FieldResult(
                                                        name = Customer.EMAIL_PROPERTY,
                                                        column = PersistenceConfiguration.TABLE.CUSTOMER + "_" + Customer.EMAIL_COLUMN
                                                ),
                                                @FieldResult(
                                                        name = Customer.CREATED_AT_PROPERTY,
                                                        column = PersistenceConfiguration.TABLE.CUSTOMER + "_" + Customer.CREATED_AT_COLUMN
                                                )
                                        }
                                )
                        }
                )
        }
)
public class Invoice implements Serializable {

    @Serial
    private static final long serialVersionUID = -132447389628211311L;

    public static final String INVOICE_CUSTOMER_MAPPING = "InvoiceCustomerMapping";

    // Properties defined in the POJO
    public static final String ID_PROPERTY = "id";
    public static final String CODE_PROPERTY = "code";
    public static final String ORDER_ID_PROPERTY = "orderId";
    public static final String COST_PROPERTY = "cost";
    public static final String CREATED_AT_PROPERTY = "createdAt";

    // Columns defined in the table of database
    public static final String ID_COLUMN = ID_PROPERTY;
    public static final String CODE_COLUMN = CODE_PROPERTY;
    public static final String ORDER_ID_COLUMN = "order_id";
    public static final String COST_COLUMN = COST_PROPERTY;
    public static final String CREATED_AT_COLUMN = "created_at";


    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = PersistenceConfiguration.SCHEMA + "." + PersistenceConfiguration.TABLE.INVOICE + "_id_seq"
    )
    private Integer id;

    @NotNull
    @Size(
            min = 1,
            max = 64
    )
    private String code;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @NotNull
    private Integer orderId;

    @NotNull
    @Positive
    private double cost;

    @NotNull
    @ColumnDefault(
            value = "CURRENT_TIMESTAMP"
    )
    private LocalDateTime createdAt;


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Invoice invoice = (Invoice) o;
        return code.equals(
                invoice.code
        );
    }


    @Override
    public int hashCode() {
        return code.hashCode();
    }

}
