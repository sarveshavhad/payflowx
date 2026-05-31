package com.payflowx.payment_core.wallet.dto;


import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TopupRequest {

    @DecimalMin(value = "0.01")
    @DecimalMax(value = "50000")
    private BigDecimal amount;
}
