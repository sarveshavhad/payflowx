package com.payflowx.payment_core.wallet.service;

import com.payflowx.payment_core.common.constants.SystemConstants;
import com.payflowx.payment_core.common.enums.TransactionType;
import com.payflowx.payment_core.transaction.entity.Transaction;
import com.payflowx.payment_core.transaction.service.TransactionService;
import com.payflowx.payment_core.wallet.dto.WalletResponse;
import com.payflowx.payment_core.wallet.entity.Wallet;
import com.payflowx.payment_core.wallet.repository.WalletRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;

    private final TransactionService transactionService;

    public WalletResponse getMyWallet(UUID userId){

        Wallet wallet = walletRepository
                .findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        return new WalletResponse(
                wallet.getId(),
                wallet.getBalance()
        );

    }

    @Transactional
    public WalletResponse topup(UUID userId, BigDecimal amount){

        Wallet customerWallet = walletRepository
                .findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        Wallet systemWallet = walletRepository
                .findByUserEmail(SystemConstants.SYSTEM_EMAIL)
                .orElseThrow(() -> new RuntimeException("System wallet not found"));

        Transaction transaction = transactionService
                .createPendingTransaction(
                        systemWallet,
                        customerWallet,
                        amount,
                        TransactionType.WALLET_TOPUP,
                        "Wallet Topup");

        systemWallet.setBalance(
                systemWallet
                        .getBalance()
                        .subtract(amount)
        );

        customerWallet.setBalance(
                customerWallet
                        .getBalance()
                        .add(amount)
        );

        walletRepository.save(systemWallet);
        walletRepository.save(customerWallet);

        transactionService.markSuccess(transaction);

        return new WalletResponse(
                customerWallet.getId(),
                customerWallet.getBalance());
    }
}
