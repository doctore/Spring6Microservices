package com.spring6microservices.common.spring.resources.data;

import com.spring6microservices.common.spring.validator.enums.IEnumInternalPropertyValue;

public enum PizzaEnum implements IEnumInternalPropertyValue<String> {
    MARGUERITA("Margherita"),
    CARBONARA("Carbonara");

    private String databaseValue;

    PizzaEnum(String databaseValue) {
        this.databaseValue = databaseValue;
    }

    @Override
    public String getInternalPropertyValue() {
        return this.databaseValue;
    }
}
