package com.payflowx.payment_core.wallet.dto;


import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TransferRequest {

    @Email
    private String receiverEmail;

    @DecimalMin(value = "0.01")
    @DecimalMax(value = "50000")
    private BigDecimal amount;
}
