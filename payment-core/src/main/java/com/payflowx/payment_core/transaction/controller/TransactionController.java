package com.payflowx.payment_core.transaction.controller;


import com.payflowx.payment_core.transaction.dto.RefundResponse;
import com.payflowx.payment_core.transaction.dto.TransactionDetailsResponse;
import com.payflowx.payment_core.transaction.dto.TransactionFilterRequest;
import com.payflowx.payment_core.transaction.dto.TransactionResponse;
import com.payflowx.payment_core.transaction.service.TransactionService;
import com.payflowx.payment_core.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("/my")
    public Page<TransactionResponse> getMyTransactions(

            TransactionFilterRequest filter,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size){

        User currentUser = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return transactionService.getMyTransactions(currentUser.getId(),filter, page, size);
    }

    @GetMapping("/{transactionId}")
    public TransactionDetailsResponse getTransaction(@PathVariable UUID transactionId){

        User currentUser = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return transactionService.getTransaction(transactionId, currentUser.getId());
    }

    @PostMapping("/{transactionId}/refund")
    public RefundResponse refundTransaction(@PathVariable UUID transactionId){

        User currentUser = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return transactionService.refundTransaction(transactionId, currentUser.getId());
    }
}
