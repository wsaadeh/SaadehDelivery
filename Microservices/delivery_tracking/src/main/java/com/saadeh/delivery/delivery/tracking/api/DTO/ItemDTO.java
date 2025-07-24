package com.saadeh.delivery.delivery.tracking.api.DTO;

import com.saadeh.delivery.delivery.tracking.domain.model.Delivery;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Setter
@Getter
public class ItemDTO {

    @NotBlank
    private String name;

    @NotNull
    @Min(1)
    private Integer quantity;

}
