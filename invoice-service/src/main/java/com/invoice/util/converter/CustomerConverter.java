package com.invoice.util.converter;

import com.invoice.dto.CustomerDto;
import com.invoice.model.Customer;
import com.spring6microservices.common.core.converter.BaseConverter;
import org.mapstruct.Mapper;

/**
 * Utility class to convert from {@link Customer} to {@link CustomerDto} and vice versa.
 */
@Mapper
public interface CustomerConverter extends BaseConverter<Customer, CustomerDto> {}
