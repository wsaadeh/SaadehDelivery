package com.saadeh.delivery.courier.management.domain.repositories;

import com.saadeh.delivery.courier.management.domain.model.Courier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CourierRepository extends JpaRepository<Courier, UUID> {
    Optional<Courier> findTop1ByOrderByLastFulFilledDeliveryAtAsc();

    Optional<Courier> findByPendingDeliveries_id(UUID deliveryId);
}
