package com.spring6microservices.common.spring.resources.data;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@Entity
@EqualsAndHashCode(of = {"username"})
@NoArgsConstructor
@Table
public class User {

    @Id
    private Long id;

    @NotNull
    @Size(min = 1, max = 128)
    private String name;

    @NotNull
    @Size(min = 1, max = 64)
    private String username;

    @NotNull
    @Size(min = 1, max = 128)
    private String address;

    @NotNull
    private Integer age;

    @NotNull
    @Size(min = 1, max = 128)
    private String email;

}

