package com.spring6microservices.common.core;

public enum PizzaEnum  {

    MARGUERITA("Margherita"),
    CARBONARA("Carbonara");

    private String databaseValue;

    PizzaEnum(String databaseValue) {
        this.databaseValue = databaseValue;
    }

    public String getInternalPropertyValue() {
        return this.databaseValue;
    }

}
