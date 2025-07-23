package com.saadeh.delivery.delivery.tracking.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DeliveryStatusTest {

    @Test
    void draft_canChangToWaitingForCourier(){
        assertTrue(
                DeliveryStatus.DRAFT.canChangeTo(DeliveryStatus.WAITING_FOR_COURIER)
        );
    }

    @Test
    void draft_canChangToInTransit(){
        assertTrue(
                DeliveryStatus.DRAFT.canNotChangeTo(DeliveryStatus.IN_TRANSIT)
        );
    }

}