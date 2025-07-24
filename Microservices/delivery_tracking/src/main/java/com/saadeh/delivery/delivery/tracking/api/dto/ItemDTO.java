package com.saadeh.delivery.delivery.tracking.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Setter
@Getter
public class ItemDTO {

    @NotBlank
    private String name;

    @NotNull
    @Min(1)
    private Integer quantity;

}
