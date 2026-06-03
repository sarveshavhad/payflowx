package com.payflowx.payment_core.wallet.service;

import com.payflowx.payment_core.common.constants.SystemConstants;
import com.payflowx.payment_core.common.enums.TransactionType;
import com.payflowx.payment_core.exception.InsufficientBalanceException;
import com.payflowx.payment_core.exception.SelfTransferException;
import com.payflowx.payment_core.exception.UserNotFoundException;
import com.payflowx.payment_core.exception.WalletNotFoundException;
import com.payflowx.payment_core.transaction.entity.Transaction;
import com.payflowx.payment_core.transaction.service.TransactionService;
import com.payflowx.payment_core.user.entity.User;
import com.payflowx.payment_core.user.repository.UserRepository;
import com.payflowx.payment_core.wallet.dto.TransferRequest;
import com.payflowx.payment_core.wallet.dto.TransferResponse;
import com.payflowx.payment_core.wallet.dto.WalletResponse;
import com.payflowx.payment_core.wallet.entity.Wallet;
import com.payflowx.payment_core.wallet.repository.WalletRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;

    private final TransactionService transactionService;

    private final UserRepository userRepository;



    public WalletResponse getMyWallet(UUID userId) {
        Wallet wallet = walletRepository
                .findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Your wallet not found"));

        return new WalletResponse(
                wallet.getId(),
                wallet.getBalance()
        );
    }

    @Transactional
    public WalletResponse topup(UUID userId, BigDecimal amount){

        Wallet customerWallet = walletRepository
                .findByUserId(userId)
                .orElseThrow(() -> new WalletNotFoundException("Receiver Wallet not found"));

        Wallet systemWallet = walletRepository
                .findByUserEmail(SystemConstants.SYSTEM_EMAIL)
                .orElseThrow(() -> new WalletNotFoundException("System wallet not found"));

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

    @Transactional
    public TransferResponse transfer(UUID senderUserId, @Valid TransferRequest request) {

        //Load Sender Wallet
        Wallet senderWallet =  walletRepository
                .findByUserId(senderUserId)
                .orElseThrow(() -> new WalletNotFoundException("Sender wallet not found"));

        System.out.println( "Receiver:"+request.getReceiverEmail());
        // Find Receiver by Email
        User receiverUser = userRepository
                .findByEmail(request.getReceiverEmail())
                .orElseThrow(() -> new UserNotFoundException("Receiver not found"));

        //Prevent Self Transfer
        if(receiverUser.getId().equals(senderUserId)){
            throw new SelfTransferException("Cannot Transfer to yourself");
        }

        //Load Receiver Wallet
        Wallet receiverWallet = walletRepository
                .findByUserEmail(request.getReceiverEmail())
                .orElseThrow(() -> new WalletNotFoundException("Receiver wallet not found"));

        //Validate Balance
        if(senderWallet.getBalance().compareTo(request.getAmount()) < 0) {

            throw new InsufficientBalanceException(
                    "Insufficient balance");
        }

        //Create Transaction(PENDING)
        Transaction transaction = transactionService.createPendingTransaction(
                senderWallet,
                receiverWallet,
                request.getAmount(),
                TransactionType.WALLET_TRANSFER,
                request.getDescription());

        //Debit Sender
        senderWallet.setBalance(
                senderWallet.getBalance()
                        .subtract(request.getAmount()
                        )
        );

        //Credit Receiver
        receiverWallet.setBalance(
                receiverWallet.getBalance()
                        .add(request.getAmount())
        );

        //Save Wallets
        walletRepository.save(senderWallet);
        walletRepository.save(receiverWallet);

        //Mark Transaction Success
        transactionService.markSuccess(transaction);


        //Return TransferResponse
        return new TransferResponse(
                transaction.getId(),
                transaction.getAmount(),
                transaction.getStatus()
        );
    }
}
