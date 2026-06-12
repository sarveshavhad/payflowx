package com.payflowx.payment_core.transaction.repository;

import com.payflowx.payment_core.transaction.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    Page<Transaction> findBySenderWalletUserIdOrReceiverWalletUserId(
            UUID senderUserId,
            UUID receiverUserId,
            Pageable pageable
    );

}
