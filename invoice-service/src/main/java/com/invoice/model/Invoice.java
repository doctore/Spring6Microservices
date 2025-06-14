package com.invoice.model;

import com.invoice.configuration.persistence.PersistenceConfiguration;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
        name = PersistenceConfiguration.TABLE.INVOICE,
        schema = PersistenceConfiguration.SCHEMA
)
public class Invoice implements Serializable {

    @Serial
    private static final long serialVersionUID = -132447389628211311L;

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
    private LocalDateTime createdAt;

}
