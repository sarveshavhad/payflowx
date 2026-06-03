package com.payflowx.payment_core.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ErrorResponse {

    private String message;

    private int status;

    private LocalDateTime timestamp;

}
