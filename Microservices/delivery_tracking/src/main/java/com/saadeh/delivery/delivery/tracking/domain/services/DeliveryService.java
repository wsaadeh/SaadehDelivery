package com.saadeh.delivery.delivery.tracking.domain.services;

import com.saadeh.delivery.delivery.tracking.api.dto.ContactPointDTO;
import com.saadeh.delivery.delivery.tracking.api.dto.DeliveryDTO;
import com.saadeh.delivery.delivery.tracking.api.dto.ItemDTO;
import com.saadeh.delivery.delivery.tracking.domain.exception.DomainException;
import com.saadeh.delivery.delivery.tracking.domain.model.ContactPoint;
import com.saadeh.delivery.delivery.tracking.domain.model.Delivery;
import com.saadeh.delivery.delivery.tracking.domain.repositories.DeliveryRepository;
import com.saadeh.delivery.delivery.tracking.domain.services.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeliveryService {
    private final DeliveryRepository repository;

    private final DeliveryTimeEstimationService deliveryTimeEstimationService;
    private final CourierPayoutCalculationService courierPayoutCalculationService;

    @Transactional
    public Delivery draft(DeliveryDTO input){
        Delivery delivery = Delivery.draft();
        handlePreparation(input, delivery);
        return repository.saveAndFlush(delivery);
    }

    @Transactional
    public Delivery edit(UUID id, DeliveryDTO input){
        Delivery delivery = repository.findById(id).orElseThrow(() -> new DomainException());
        delivery.removeItems();
        handlePreparation(input, delivery);
        return repository.saveAndFlush(delivery);
    }

    @Transactional(readOnly = true)
    public PagedModel<Delivery> findAll(@PageableDefault Pageable pageable) {
        return new PagedModel<>(
                repository.findAll(pageable)
        );
    }

    @Transactional(readOnly = true)
    public Delivery findById(@PathVariable UUID deliveryId){
        return repository.findById(deliveryId).orElseThrow(()-> new ResourceNotFoundException("Entity not found"));
    }

    private void handlePreparation(DeliveryDTO input, Delivery delivery) {
        ContactPointDTO senderDTO = input.getSender();
        ContactPointDTO recipientDTO = input.getRecipient();

        ContactPoint sender = ContactPoint.builder()
                .phone(senderDTO.getPhone())
                .name(senderDTO.getName())
                .complement(senderDTO.getComplement())
                .number(senderDTO.getNumber())
                .zipCode(senderDTO.getZipCode())
                .street(senderDTO.getStreet())
                .build();
        ContactPoint recipient = ContactPoint.builder()
                .phone(recipientDTO.getPhone())
                .name(recipientDTO.getName())
                .complement(recipientDTO.getComplement())
                .number(recipientDTO.getNumber())
                .zipCode(recipientDTO.getZipCode())
                .street(recipientDTO.getStreet())
                .build();

        DeliveryEstimate estimated = deliveryTimeEstimationService.estimated(sender, recipient);
        BigDecimal payout = courierPayoutCalculationService.calculatePayout(10.0);

        BigDecimal distanceFee = calculateFee(estimated.getDistanceInKm());

        Delivery.PreparationDetails preparationDetails = Delivery.PreparationDetails.builder()
                .recipient(recipient)
                .sender(sender)
                .expectedDeliveryTime(estimated.getEstimatedTime())
                .courierPayout(payout)
                .distanceFee(distanceFee)
                .build();

        delivery.editPreparationDetails(preparationDetails);

        for (ItemDTO itemDTO: input.getItems()){
            delivery.addItem(itemDTO.getName(), itemDTO.getQuantity());
        }

    }

    private BigDecimal calculateFee(Double distanceInKm) {
        return new BigDecimal(3)
                .multiply(new BigDecimal(distanceInKm))
                .setScale(2, RoundingMode.HALF_EVEN);
    }
}
