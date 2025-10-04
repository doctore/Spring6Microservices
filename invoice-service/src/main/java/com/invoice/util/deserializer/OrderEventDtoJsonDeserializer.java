package com.invoice.util.deserializer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring6microservices.common.core.util.ExceptionUtil;
import com.spring6microservices.common.spring.jms.dto.EventDto;
import com.spring6microservices.common.spring.jms.dto.OrderEventDto;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.nio.charset.StandardCharsets;

import static java.lang.String.format;

@Log4j2
public class OrderEventDtoJsonDeserializer implements Deserializer<EventDto<OrderEventDto>> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public EventDto<OrderEventDto> deserialize(final String topic,
                                               final byte[] data) {
        try {
            if (null == data) {
                log.warn(
                        format("For the topic: %s, null data received at deserializing",
                                topic
                        )
                );
                return null;
            }
            return objectMapper.readValue(
                    new String(
                            data,
                            StandardCharsets.UTF_8

                    ),
                    new TypeReference<>() {}
            );

        } catch (Throwable t) {
            log.error(
                    format("There was an error deserializing data. %s",
                            ExceptionUtil.getFormattedCurrentAndRootError(
                                    t
                            )
                    ),
                    t
            );
            throw new SerializationException(
                    "Error when deserializing byte[] to EventDto<OrderEventDto>",
                    t
            );
        }
    }

}
