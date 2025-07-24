package com.saadeh.delivery.delivery.tracking.infrastructure.fake;

import com.saadeh.delivery.delivery.tracking.domain.model.ContactPoint;
import com.saadeh.delivery.delivery.tracking.domain.services.DeliveryEstimate;
import com.saadeh.delivery.delivery.tracking.domain.services.DeliveryTimeEstimationService;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class DeliveryTimeEstationServiceFakeImpl
        implements DeliveryTimeEstimationService {
    @Override
    public DeliveryEstimate estimated(ContactPoint sender, ContactPoint receiver) {
        return new DeliveryEstimate(
                Duration.ofHours(3),
                3.1
        );
    }
}
