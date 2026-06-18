package com.payflowx.payment_core.transaction.service;

import com.payflowx.payment_core.common.enums.TransactionStatus;
import com.payflowx.payment_core.common.enums.TransactionType;
import com.payflowx.payment_core.exception.RefundNotAllowedException;
import com.payflowx.payment_core.exception.TransactionAccessDeniedException;
import com.payflowx.payment_core.exception.TransactionAlreadyRefundedException;
import com.payflowx.payment_core.exception.TransactionNotFoundException;
import com.payflowx.payment_core.transaction.dto.RefundResponse;
import com.payflowx.payment_core.transaction.dto.TransactionDetailsResponse;
import com.payflowx.payment_core.transaction.dto.TransactionFilterRequest;
import com.payflowx.payment_core.transaction.dto.TransactionResponse;
import com.payflowx.payment_core.transaction.entity.Transaction;
import com.payflowx.payment_core.transaction.repository.TransactionRepository;
import com.payflowx.payment_core.transaction.specification.TransactionSpecification;
import com.payflowx.payment_core.wallet.entity.Wallet;
import com.payflowx.payment_core.wallet.repository.WalletRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    private final WalletRepository walletRepository;

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

    public Page<TransactionResponse> getMyTransactions(UUID userId, TransactionFilterRequest filter, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Specification<Transaction> specification = TransactionSpecification.belongsToUser(userId);

        if(filter.getType() != null){
            specification = specification.and(TransactionSpecification.hasType(filter.getType()));
        }

        if(filter.getStatus() != null){
            specification = specification.and(
                    TransactionSpecification.hasStatus(filter.getStatus()));
        }

        Page<Transaction> transactions =
                transactionRepository.findAll(
                        specification,
                        pageable
                );

        return transactions
                .map(this::toResponse);

    }

    private TransactionResponse toResponse(Transaction transaction){

        return  new TransactionResponse(
                transaction.getId(),
                transaction.getAmount(),
                transaction.getType(),
                transaction.getStatus(),
                transaction.getSenderWallet().getUser().getEmail(),
                transaction.getReceiverWallet().getUser().getEmail(),
                transaction.getCreatedAt()
        );

    }

    public TransactionDetailsResponse getTransaction(UUID transactionId, UUID currentUserid) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found."));

        UUID senderId = transaction.getSenderWallet().getUser().getId();

        UUID receiverId = transaction.getReceiverWallet().getUser().getId();

        if(!senderId.equals(currentUserid) && !receiverId.equals(currentUserid)){
            throw new TransactionAccessDeniedException("You're not authorized to view this transaction");
        }

        return new TransactionDetailsResponse(
                transaction.getId(),
                transaction.getSenderWallet().getUser().getEmail(),
                transaction.getReceiverWallet().getUser().getEmail(),
                transaction.getAmount(),
                transaction.getType(),
                transaction.getStatus(),
                transaction.getDescription(),
                transaction.getCreatedAt()
        );

    }

    @Transactional
    public RefundResponse refundTransaction(UUID transactionId, UUID currentUserId) {
        Transaction originalTransaction = transactionRepository.findById(transactionId)
                .orElseThrow(() ->new TransactionNotFoundException("Transaction not found"));

        UUID originalSenderId = originalTransaction.getSenderWallet().getUser().getId();

        if(!originalSenderId.equals(currentUserId)){
            throw new TransactionAccessDeniedException("Only transaction sender can request refund");
        }

        if(originalTransaction.getType() != TransactionType.WALLET_TRANSFER){
            throw new RefundNotAllowedException("Only wallet transfer can be refunded");
        }

        if(originalTransaction.getStatus() != TransactionStatus.SUCCESS){
            throw new RefundNotAllowedException("Only successful transaction can be refunded");
        }

        if(transactionRepository.existsByOriginalTransactionIdAndType(transactionId, TransactionType.REFUND)){
            throw new TransactionAlreadyRefundedException("Transaction already refunded");
        }

        Wallet refundSender = originalTransaction.getReceiverWallet();

        Wallet refundReceiver = originalTransaction.getSenderWallet();

        BigDecimal refundAmount = originalTransaction.getAmount();

        if(refundSender.getBalance().compareTo(refundAmount) < 0){
            throw new RefundNotAllowedException("Insufficient balance for refund");
        }

        Transaction refundTransaction = createPendingTransaction(
                refundSender,
                refundReceiver,
                refundAmount,
                TransactionType.REFUND,
                "Refund for transaction: " + originalTransaction.getId()
        );

        refundTransaction.setOriginalTransaction(originalTransaction);

        transactionRepository.save(refundTransaction);

        refundSender.setBalance(refundSender.getBalance().subtract(refundAmount));

        refundReceiver.setBalance(refundReceiver.getBalance().add(refundAmount));

        walletRepository.save(refundSender);
        walletRepository.save(refundReceiver);

        markSuccess(refundTransaction);

        originalTransaction.setStatus(
                TransactionStatus.REFUNDED
        );

        transactionRepository.save(originalTransaction);

        return new RefundResponse(
                refundTransaction.getId(),
                refundTransaction.getAmount(),
                refundTransaction.getStatus()
        );

    }
}

