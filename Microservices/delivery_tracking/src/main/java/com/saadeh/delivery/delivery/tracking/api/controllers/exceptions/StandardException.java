package com.saadeh.delivery.delivery.tracking.api.controllers.exceptions;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;


@NoArgsConstructor
@Getter
@Setter
public class StandardException implements Serializable {
    private Instant timeStamp;
    private Integer status;
    private String error;
    private String message;
    private String path;
}
