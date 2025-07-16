package com.spring6microservices.common.spring.validator.group;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * Group value used in conditional Jakarta validations like: {@link NotNull} {@link NotEmpty}, etc. For example:
 * <pre>
 *    @NotNull(
 *       groups = { UpdateAction.class }
 *    )
 *    private Integer id;
 * </pre>
 */
public interface UpdateAction {
}
