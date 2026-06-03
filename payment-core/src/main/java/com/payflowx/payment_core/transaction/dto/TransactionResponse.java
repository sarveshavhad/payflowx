package com.payflowx.payment_core.transaction.dto;

import com.payflowx.payment_core.common.enums.TransactionStatus;
import com.payflowx.payment_core.common.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class TransactionResponse {

    private UUID transactionId;

    private TransactionType transactionType;

    private TransactionStatus transactionStatus;

    private BigDecimal amount;

    private String senderEmail;

    private String receiverEmail;

    private LocalDateTime createdAt;

}
