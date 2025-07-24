package com.saadeh.delivery.delivery.tracking.domain.services;

import java.math.BigDecimal;

public interface CourierPayoutCalculationService {
    BigDecimal calculatePayout(Double distanceInKm);
}
