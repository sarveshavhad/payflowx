package com.payflowx.payment_core.transaction.dto;

import com.payflowx.payment_core.common.enums.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class RefundResponse {

    private UUID refundTransactionId;

    private BigDecimal amount;

    private TransactionStatus status;
}
