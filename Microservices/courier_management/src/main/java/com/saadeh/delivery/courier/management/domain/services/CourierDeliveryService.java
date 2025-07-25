package com.saadeh.delivery.courier.management.domain.services;

import com.saadeh.delivery.courier.management.domain.model.Courier;
import com.saadeh.delivery.courier.management.domain.repositories.CourierRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CourierDeliveryService {

    private final CourierRepository courierRepository;

    public void assign(UUID deliveryId){
        Courier courier = courierRepository.findTop1ByOrderByLastFulFilledDeliveryAtAsc()
                .orElseThrow();

        courier.assign(deliveryId);

        courierRepository.saveAndFlush(courier);

        log.info("Courier {} assigned to delivery {}", courier.getId(), deliveryId);
    }

    public void fullfill(UUID deliveryId){
        Courier courier = courierRepository.findByPendingDeliveries_id(deliveryId).orElseThrow();

        courier.fulFill(deliveryId);

        courierRepository.saveAndFlush(courier);

        log.info("Courier {} fulfilled the delivery {} ", courier.getId(), deliveryId);

    }
}
