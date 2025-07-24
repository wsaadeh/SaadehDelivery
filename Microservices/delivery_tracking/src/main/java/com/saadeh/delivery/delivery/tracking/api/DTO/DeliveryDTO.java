package com.saadeh.delivery.delivery.tracking.api.DTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DeliveryDTO {
    @NotNull
    @Valid
    private ContactPointDTO sender;

    @NotNull
    @Valid
    private ContactPointDTO recipient;

    @NotEmpty
    @Valid
    @Size(min = 1)
    private List<ItemDTO> items;

}
