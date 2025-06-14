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

}