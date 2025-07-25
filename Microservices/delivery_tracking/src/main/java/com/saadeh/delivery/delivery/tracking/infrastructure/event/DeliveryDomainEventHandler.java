package com.saadeh.delivery.delivery.tracking.infrastructure.event;

import com.saadeh.delivery.delivery.tracking.domain.event.DeliveryFulFilledEvent;
import com.saadeh.delivery.delivery.tracking.domain.event.DeliveryPickUpEvent;
import com.saadeh.delivery.delivery.tracking.domain.event.DeliveryPlacedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import static com.saadeh.delivery.delivery.tracking.infrastructure.kafka.kafkaTopicConfig.deliveryEventsTopicName;

@Component
@Slf4j
@RequiredArgsConstructor
public class DeliveryDomainEventHandler {

    private final IntegrationEventPublisher integrationEventPublisher;

    @EventListener
    public void handle(DeliveryPlacedEvent event){
        log.info(event.toString());
        integrationEventPublisher.publish(event, event.getDeliveryId().toString(),deliveryEventsTopicName);
    }

    @EventListener
    public void handle(DeliveryPickUpEvent event){
        log.info(event.toString());
        integrationEventPublisher.publish(event, event.getDeliveryId().toString(),deliveryEventsTopicName);
    }

    @EventListener
    public void handle(DeliveryFulFilledEvent event){
        log.info(event.toString());
        integrationEventPublisher.publish(event, event.getDeliveryId().toString(),deliveryEventsTopicName);
    }

}
