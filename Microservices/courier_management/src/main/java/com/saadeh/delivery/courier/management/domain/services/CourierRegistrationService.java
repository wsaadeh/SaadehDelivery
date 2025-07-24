package com.saadeh.delivery.courier.management.domain.services;

import com.saadeh.delivery.courier.management.api.dto.CourierDTO;
import com.saadeh.delivery.courier.management.domain.model.Courier;
import com.saadeh.delivery.courier.management.domain.repositories.CourierRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class CourierRegistrationService {

    private final CourierRepository repository;

    public Courier create(@Valid CourierDTO input) {
        Courier courier = Courier.brandNew(input.getName(), input.getPhone());
        return repository.saveAndFlush(courier);
    }

    public Courier update(UUID id, @Valid CourierDTO input) {
        Courier courier = repository.findById(id).orElseThrow();
        courier.setName(input.getName());
        courier.setPhone(input.getPhone());
        return repository.saveAndFlush(courier);
    }
}
