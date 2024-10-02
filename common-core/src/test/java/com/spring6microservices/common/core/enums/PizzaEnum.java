package com.spring6microservices.common.core.enums;

public enum PizzaEnum  {

    MARGUERITA("Margherita"),
    CARBONARA("Carbonara"),
    NULL(null);

    private String databaseValue;

    PizzaEnum(String databaseValue) {
        this.databaseValue = databaseValue;
    }

    public String getDatabaseValue() {
        return this.databaseValue;
    }

}
