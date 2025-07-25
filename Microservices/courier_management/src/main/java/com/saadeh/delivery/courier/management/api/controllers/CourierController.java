package com.saadeh.delivery.courier.management.api.controllers;

import com.saadeh.delivery.courier.management.api.dto.CourierDTO;
import com.saadeh.delivery.courier.management.api.dto.CourierPayoutCalculationInput;
import com.saadeh.delivery.courier.management.api.dto.CourierPayoutResultModel;
import com.saadeh.delivery.courier.management.domain.model.Courier;
import com.saadeh.delivery.courier.management.domain.repositories.CourierRepository;
import com.saadeh.delivery.courier.management.domain.services.CourierPayoutService;
import com.saadeh.delivery.courier.management.domain.services.CourierRegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/v1/couriers")
@RequiredArgsConstructor
@Slf4j
public class CourierController {
    private final CourierRegistrationService service;
    private final CourierPayoutService courierPayoutService;
    private final CourierRepository repository;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Courier create(@Valid @RequestBody CourierDTO input){
        return service.create(input);
    }

    @PutMapping("/{id}")
    public Courier update(@PathVariable UUID id,
                          @Valid @RequestBody CourierDTO input){
        return service.update(id, input);
    }

    @GetMapping
    public PagedModel<Courier> findAll(@PageableDefault Pageable pageable){
        log.info("FindAll Request");
        return new PagedModel<>(
                repository.findAll(pageable)
        );
    }

    @GetMapping("/{id}")
    public Courier findById(@PathVariable UUID id){
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/payout-calculation")
    public CourierPayoutResultModel calculate(
            @RequestBody CourierPayoutCalculationInput input){
        BigDecimal payoutFee = courierPayoutService.calculate(
                input.getDistanceInKm());
        return new CourierPayoutResultModel(payoutFee);
    }


}
