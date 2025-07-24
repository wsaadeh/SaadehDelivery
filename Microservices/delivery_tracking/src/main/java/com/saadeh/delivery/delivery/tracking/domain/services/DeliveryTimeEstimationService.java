package com.saadeh.delivery.delivery.tracking.domain.services;

import com.saadeh.delivery.delivery.tracking.domain.model.ContactPoint;

public interface DeliveryTimeEstimationService {
    DeliveryEstimate estimated(ContactPoint sender, ContactPoint receiver);
}
