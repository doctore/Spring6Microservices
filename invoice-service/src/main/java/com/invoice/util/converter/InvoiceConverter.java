package com.invoice.util.converter;

import com.invoice.dto.InvoiceDto;
import com.invoice.dto.OrderDto;
import com.invoice.model.Invoice;
import com.spring6microservices.common.core.converter.BaseConverter;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.Optional.ofNullable;

/**
 * Utility class to convert from {@link Invoice} to {@link InvoiceDto} and vice versa.
 */
@Mapper
@DecoratedWith(
        InvoiceConverterDecorator.class
)
public interface InvoiceConverter extends BaseConverter<Invoice, InvoiceDto> {
}


/**
 * Overwrites default converter methods included in {@link InvoiceConverter}.
 */
abstract class InvoiceConverterDecorator implements InvoiceConverter {

    @Autowired
    private InvoiceConverter converter;

    @Autowired
    private CustomerConverter customerConverter;


    @Override
    public Invoice fromDtoToModel(final InvoiceDto dto) {
        return ofNullable(dto)
                .map(d -> {
                    Invoice model = converter.fromDtoToModel(
                            d
                    );
                    model.setCustomer(
                            customerConverter.fromDtoToModel(
                                    d.getCustomer()
                            )
                    );
                    if (null != d.getOrder()) {
                        model.setOrderId(
                                d.getOrder().getId()
                        );
                    }
                    return model;
                })
                .orElse(null);
    }


    @Override
    public List<Invoice> fromDtosToModels(final Collection<InvoiceDto> dtos) {
        return ofNullable(dtos)
                .map(d ->
                        d.stream()
                                .map(this::fromDtoToModel)
                                .toList()
                )
                .orElseGet(ArrayList::new);
    }


    @Override
    public InvoiceDto fromModelToDto(final Invoice model) {
        return ofNullable(model)
                .map(m -> {
                    InvoiceDto dto = converter.fromModelToDto(
                            m
                    );
                    dto.setCustomer(
                            customerConverter.fromModelToDto(
                                    m.getCustomer()
                            )
                    );
                    if (null != m.getOrderId()) {
                        dto.setOrder(
                                OrderDto.builder()
                                        .id(m.getOrderId())
                                        .build()
                        );
                    }
                    return dto;
                })
                .orElse(null);
    }


    @Override
    public List<InvoiceDto> fromModelsToDtos(final Collection<Invoice> models) {
        return ofNullable(models)
                .map(m ->
                        m.stream()
                                .map(this::fromModelToDto)
                                .toList()
                )
                .orElseGet(ArrayList::new);
    }

}

