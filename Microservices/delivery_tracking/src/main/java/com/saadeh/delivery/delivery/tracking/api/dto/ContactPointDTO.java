package com.saadeh.delivery.delivery.tracking.api.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@EqualsAndHashCode
public class ContactPointDTO {

    private String zipCode;
    private String street;
    private String number;
    private String complement;
    private String name;
    private String phone;

}
