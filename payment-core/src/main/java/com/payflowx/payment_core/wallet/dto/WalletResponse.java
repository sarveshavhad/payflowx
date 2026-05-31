package com.payflowx.payment_core.wallet.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class WalletResponse {

    private UUID walletId;

    private BigDecimal balance;
}
