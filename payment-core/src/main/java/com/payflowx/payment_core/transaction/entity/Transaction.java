package com.payflowx.payment_core.transaction.entity;

import com.payflowx.payment_core.common.entity.BaseEntity;
import com.payflowx.payment_core.common.enums.TransactionStatus;
import com.payflowx.payment_core.common.enums.TransactionType;
import com.payflowx.payment_core.user.entity.User;
import com.payflowx.payment_core.wallet.entity.Wallet;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "transactions")
public class Transaction extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_wallet_id", nullable = false)
    private Wallet senderWallet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_wallet_id", nullable = false)
    private Wallet receiverWallet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "original_transaction_id")
    private Transaction originalTransaction;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Column(length = 255)
    private String description;

    @Column(length = 255)
    private String failureReason;
}
