package com.saadeh.delivery.courier.management.domain.model;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.*;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Setter(AccessLevel.PRIVATE)
public class Courier {
    @EqualsAndHashCode.Include
    private UUID id;

    @Setter(AccessLevel.PUBLIC)
    private String name;

    @Setter(AccessLevel.PUBLIC)
    private String phone;

    private Integer fulFilledDeliveriesQuantity;

    private Integer pendingDeliveriesQuantity;

    private OffsetDateTime lastFulFilledDeliveryAt;

    private List<AssignedDelivery> pendingDeliveries = new ArrayList<>();

    public List<AssignedDelivery> getPendingDeliveries(){
        return Collections.unmodifiableList(this.pendingDeliveries);
    }

    public static Courier brandNew(String name, String phone){
        Courier courier = new Courier();
        courier.setId(UUID.randomUUID());
        courier.setName(name);
        courier.setPhone(phone);
        courier.setPendingDeliveriesQuantity(0);
        courier.setFulFilledDeliveriesQuantity(0);
        return courier;
    }

    public void assign(UUID deliveryId){
        this.pendingDeliveries.add(
                AssignedDelivery.pending(deliveryId)
        );
        this.pendingDeliveriesQuantity++;
    }

    public void fulFill(UUID deliveryId){
        AssignedDelivery delivery = this.pendingDeliveries.stream().filter(
                d -> d.getId().equals(deliveryId)
        ).findFirst().orElseThrow();

        this.pendingDeliveries.remove(delivery);

        this.pendingDeliveriesQuantity--;
        this.fulFilledDeliveriesQuantity++;
        this.lastFulFilledDeliveryAt = OffsetDateTime.now();

    }

}
