package com.payflowx.payment_core.wallet.dto;

import com.payflowx.payment_core.common.enums.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class TransferResponse {

    private UUID transactionId;

    private BigDecimal amount;

    private TransactionStatus status;

}
