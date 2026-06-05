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
public class TransactionDetailsResponse {

    private UUID userId;

    private String senderEmail;

    private String receiverEmail;

    private BigDecimal amount;

    private TransactionType type;

    private TransactionStatus status;

    private String description;

    private LocalDateTime createdAt;

}
