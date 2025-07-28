package com.saadeh.delivery.courier.management.infrastructure.kafka;

import com.saadeh.delivery.courier.management.domain.services.CourierDeliveryService;
import com.saadeh.delivery.courier.management.infrastructure.event.DeliveryFulfilledIntegrationEvent;
import com.saadeh.delivery.courier.management.infrastructure.event.DeliveryPlacedIntegrationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(topics = {
        "deliveries.v1.events"
}, groupId = "courier-management")
@Slf4j
@RequiredArgsConstructor
public class KafkaDeliveriesMessageHandler {

    private final CourierDeliveryService courierDeliveryService;

    @KafkaHandler(isDefault = true)
    public void defaultHandler(@Payload Object object,
                               @Headers MessageHeaders headers) {
        log.info("Default Handler: {}", object.getClass());
        log.info("Payload content: {}", object);
        log.info("Headers: {}", headers);
    }

    @KafkaHandler
    public void handle(@Payload DeliveryPlacedIntegrationEvent event) {
        log.info("Received: {} ", event);
        courierDeliveryService.assign(event.getDeliveryId());
    }

    @KafkaHandler
    public void handle(@Payload DeliveryFulfilledIntegrationEvent event) {
        log.info("Received: {} ", event);
        courierDeliveryService.fullfill(event.getDeliveryId());
    }

}
