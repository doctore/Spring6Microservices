package com.invoice.model;

import com.invoice.configuration.persistence.PersistenceConfiguration;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

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
        name = PersistenceConfiguration.TABLE.CUSTOMER,
        schema = PersistenceConfiguration.SCHEMA
)
public class Customer implements Serializable {

    @Serial
    private static final long serialVersionUID = -161419389728211353L;

    // Properties defined in the POJO
    public static final String ID_PROPERTY = "id";
    public static final String CODE_PROPERTY = "code";
    public static final String ADDRESS_PROPERTY = "address";
    public static final String PHONE_PROPERTY = "phone";
    public static final String EMAIL_PROPERTY = "email";
    public static final String CREATED_AT_PROPERTY = "createdAt";

    // Columns defined in the table of database
    public static final String ID_COLUMN = ID_PROPERTY;
    public static final String CODE_COLUMN = CODE_PROPERTY;
    public static final String ADDRESS_COLUMN = ADDRESS_PROPERTY;
    public static final String PHONE_COLUMN = PHONE_PROPERTY;
    public static final String EMAIL_COLUMN = EMAIL_PROPERTY;
    public static final String CREATED_AT_COLUMN = "created_at";


    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = PersistenceConfiguration.SCHEMA + "." + PersistenceConfiguration.TABLE.CUSTOMER + "_id_seq"
    )
    private Integer id;

    @NotNull
    @Size(
            min = 1,
            max = 64
    )
    private String code;

    @NotNull
    @Size(
            min = 1,
            max = 64
    )
    private String address;

    @NotNull
    @Size(
            min = 1,
            max = 16
    )
    private String phone;

    @Size(
            max = 64
    )
    private String email;

    @NotNull
    private LocalDateTime createdAt;


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Customer customer = (Customer) o;
        return code.equals(
                customer.code
        );
    }


    @Override
    public int hashCode() {
        return code.hashCode();
    }

}