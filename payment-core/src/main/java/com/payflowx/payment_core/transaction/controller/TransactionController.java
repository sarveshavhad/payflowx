package com.payflowx.payment_core.transaction.controller;


import com.payflowx.payment_core.transaction.dto.TransactionDetailsResponse;
import com.payflowx.payment_core.transaction.dto.TransactionResponse;
import com.payflowx.payment_core.transaction.service.TransactionService;
import com.payflowx.payment_core.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("/my")
    public List<TransactionResponse> getMyTransactions(){

        User currentUser = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return transactionService.getMyTransactions(currentUser.getId());
    }

    @GetMapping("/{transactionId}")
    public TransactionDetailsResponse getTransaction(@PathVariable UUID transactionId){

        User currentUser = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return transactionService.getTransaction(transactionId, currentUser.getId());
    }
}
