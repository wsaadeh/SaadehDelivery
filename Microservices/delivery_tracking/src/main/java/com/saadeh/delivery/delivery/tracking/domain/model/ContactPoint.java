package com.saadeh.delivery.delivery.tracking.domain.model;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Embeddable
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class ContactPoint {
    @NotBlank
    private String zipCode;

    @NotBlank
    private String street;

    @NotBlank
    private String number;


    private String complement;

    @NotBlank
    private String name;

    @NotBlank
    private String phone;
}
