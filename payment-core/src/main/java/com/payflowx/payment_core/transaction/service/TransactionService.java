package com.payflowx.payment_core.transaction.service;

import com.payflowx.payment_core.common.enums.TransactionStatus;
import com.payflowx.payment_core.common.enums.TransactionType;
import com.payflowx.payment_core.exception.TransactionAccessDeniedException;
import com.payflowx.payment_core.exception.TransactionNotFoundException;
import com.payflowx.payment_core.transaction.dto.TransactionDetailsResponse;
import com.payflowx.payment_core.transaction.dto.TransactionFilterRequest;
import com.payflowx.payment_core.transaction.dto.TransactionResponse;
import com.payflowx.payment_core.transaction.entity.Transaction;
import com.payflowx.payment_core.transaction.repository.TransactionRepository;
import com.payflowx.payment_core.transaction.specification.TransactionSpecification;
import com.payflowx.payment_core.wallet.entity.Wallet;
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
}

