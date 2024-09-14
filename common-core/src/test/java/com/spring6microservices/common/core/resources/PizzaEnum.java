package com.spring6microservices.common.core.resources;

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
