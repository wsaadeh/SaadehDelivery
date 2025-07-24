package com.saadeh.delivery.courier.management.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourierDTO {
    @NotBlank
    private String name;

    @NotBlank
    private String phone;
}
