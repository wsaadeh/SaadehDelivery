package com.saadeh.delivery.delivery.tracking.api.controllers;

import com.saadeh.delivery.delivery.tracking.api.dto.CourierDTO;
import com.saadeh.delivery.delivery.tracking.api.dto.DeliveryDTO;
import com.saadeh.delivery.delivery.tracking.domain.model.Delivery;
import com.saadeh.delivery.delivery.tracking.domain.services.DeliveryCheckpointService;
import com.saadeh.delivery.delivery.tracking.domain.services.DeliveryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Random;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/deliveries")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService service;
    private final DeliveryCheckpointService checkpointService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Delivery draft(@RequestBody @Valid DeliveryDTO input) {
        return service.draft(input);
    }

    @PutMapping("/{id}")
    public Delivery edit(@PathVariable UUID id,
                         @RequestBody @Valid DeliveryDTO input) {
        return service.edit(id, input);
    }

    @SneakyThrows
    @GetMapping
    public PagedModel<Delivery> findAll(@PageableDefault Pageable pageable) {
        if (Math.random() < 0.3){
            throw new RuntimeException();
        }
        int millis = new Random().nextInt(400);
        Thread.sleep(millis);
        return service.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Delivery> findById(@PathVariable UUID id){
        return ResponseEntity.ok().body(service.findById(id));
    }

    @PostMapping("/{id}/placement")
    public void place(@PathVariable UUID id){
        checkpointService.place(id);
    }

    @PostMapping("/{id}/pickups")
    public void pickup(@PathVariable UUID id,
                       @Valid @RequestBody CourierDTO input){
        checkpointService.pickUp(id,input.getCourierId());
    }

    @PostMapping("/{id}/completion")
    public void complete(@PathVariable UUID id){
        checkpointService.complete(id);
    }

}
