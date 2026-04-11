package com.spring6microservices.common.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@EqualsAndHashCode
@Data
@NoArgsConstructor
public class UserDto implements Comparable<UserDto> {

    private Long id;
    private String name;
    private String address;
    private Integer age;
    private String birthday;
    private String email;


    @Override
    public int compareTo(final UserDto other) {
        if (other == null) {
            return 1;
        }
        if (this.id == null && other.id == null) {
            return 0;
        }
        if (this.id == null) {
            return -1;
        }
        if (other.id == null) {
            return 1;
        }
        return this.id.compareTo(
                other.id
        );
    }

}
