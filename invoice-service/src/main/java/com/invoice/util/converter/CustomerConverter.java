package com.invoice.util.converter;

import com.invoice.model.Customer;
import com.spring6microservices.common.core.converter.BaseConverter;
import com.spring6microservices.common.spring.dto.invoice.CustomerDto;
import org.mapstruct.Mapper;

/**
 * Utility class to convert from {@link Customer} to {@link CustomerDto} and vice versa.
 */
@Mapper
public interface CustomerConverter extends BaseConverter<Customer, CustomerDto> {}
