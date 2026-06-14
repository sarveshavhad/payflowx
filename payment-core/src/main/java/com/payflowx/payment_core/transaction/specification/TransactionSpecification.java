package com.payflowx.payment_core.transaction.specification;

import com.payflowx.payment_core.common.enums.TransactionStatus;
import com.payflowx.payment_core.common.enums.TransactionType;
import com.payflowx.payment_core.transaction.entity.Transaction;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class TransactionSpecification {
    public static Specification<Transaction> belongsToUser(UUID userId){
        
        return (root, query, criteriaBuilder) -> 
                criteriaBuilder.or(
                        criteriaBuilder.equal(
                                root.get("senderWallet")
                                        .get("user")
                                        .get("id"),
                                userId
                        ),
                        
                        criteriaBuilder.equal(
                                root.get("receiverWallet")
                                        .get("user")
                                        .get("id"),
                                userId
                        )
                );
    }

    public static Specification<Transaction> hasType(TransactionType type) {

        return(root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("type"),
                        type
                );
    }

    public static Specification<Transaction> hasStatus(TransactionStatus status) {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("status"),
                        status
                );
    }
}
