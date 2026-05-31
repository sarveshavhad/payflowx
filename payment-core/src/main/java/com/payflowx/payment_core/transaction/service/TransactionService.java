package com.payflowx.payment_core.transaction.service;

import com.payflowx.payment_core.common.enums.TransactionStatus;
import com.payflowx.payment_core.common.enums.TransactionType;
import com.payflowx.payment_core.transaction.entity.Transaction;
import com.payflowx.payment_core.transaction.repository.TransactionRepository;
import com.payflowx.payment_core.wallet.entity.Wallet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public Transaction createPendingTransaction(
            Wallet sender,
            Wallet reciever,
            BigDecimal amount,
            TransactionType type,
            String description) {

                Transaction transaction = new Transaction();
                transaction.setSenderWallet(sender);
                transaction.setReceiverWallet(reciever);
                transaction.setAmount(amount);
                transaction.setType(type);
                transaction.setStatus(TransactionStatus.PENDING);
                transaction.setDescription(description);

                return transactionRepository.save(transaction);

    }

    public void markSuccess(Transaction transaction){

        transaction.setStatus(TransactionStatus.SUCCESS);

        transactionRepository.save(transaction);

    }

    public void markFailed(Transaction transaction, String reason){

        transaction.setStatus(TransactionStatus.FAILED);

        transaction.setFailureReason(reason);

        transactionRepository.save(transaction);

    }
}
