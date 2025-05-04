package com.order.model;

/**
 * Main interface implemented by all models defined in the project.
 */
public interface IModel {

    /**
     * Returns {@code true} if the current model has not been persisted yet, {@code false} otherwise.
     */
    boolean isNew();

}