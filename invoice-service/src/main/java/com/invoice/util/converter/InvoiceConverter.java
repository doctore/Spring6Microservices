package com.invoice.util.converter;

import com.invoice.dto.InvoiceDto;
import com.invoice.model.Invoice;
import com.spring6microservices.common.core.converter.BaseConverter;
import org.mapstruct.Mapper;

/**
 * Utility class to convert from {@link Invoice} to {@link InvoiceDto} and vice versa.
 */
@Mapper(
        uses = { CustomerConverter.class }
)
public interface InvoiceConverter extends BaseConverter<Invoice, InvoiceDto> {}
