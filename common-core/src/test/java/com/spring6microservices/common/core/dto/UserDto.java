package com.spring6microservices.common.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@EqualsAndHashCode
@Data
@NoArgsConstructor
public class UserDto {

    private Long id;
    private String name;
    private String address;
    private Integer age;
    private String birthday;
    private String email;

}
