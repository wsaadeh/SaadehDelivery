package com.saadeh.delivery.delivery.tracking.domain.services;

import com.saadeh.delivery.delivery.tracking.domain.exception.DomainException;
import com.saadeh.delivery.delivery.tracking.domain.model.Delivery;
import com.saadeh.delivery.delivery.tracking.domain.repositories.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class DeliveryCheckpointService {
    private final DeliveryRepository repository;

    public void place(UUID id){
        Delivery delivery = repository.findById(id)
                .orElseThrow(() -> new DomainException());
        delivery.place();
        repository.saveAndFlush(delivery);
    }

    public void pickUp(UUID id, UUID courierId){
        Delivery delivery = repository.findById(id)
                .orElseThrow(() -> new DomainException());
        delivery.pickUp(courierId);
        repository.saveAndFlush(delivery);
    }

    public void complete(UUID id){
        Delivery delivery = repository.findById(id)
                .orElseThrow(() -> new DomainException());
        delivery.markAsDelivered();
        repository.saveAndFlush(delivery);

    }
}
