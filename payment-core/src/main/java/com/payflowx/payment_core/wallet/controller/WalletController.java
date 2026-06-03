package com.payflowx.payment_core.wallet.controller;


import com.payflowx.payment_core.user.entity.User;
import com.payflowx.payment_core.wallet.dto.TopupRequest;
import com.payflowx.payment_core.wallet.dto.TransferRequest;
import com.payflowx.payment_core.wallet.dto.TransferResponse;
import com.payflowx.payment_core.wallet.dto.WalletResponse;
import com.payflowx.payment_core.wallet.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @GetMapping("/me")
    public WalletResponse getMyWallet(){
        User user = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return walletService.getMyWallet(user.getId());
    }

    @PostMapping("/topup")
    public WalletResponse topup(@Valid @RequestBody TopupRequest request){

        User user = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return walletService.topup(
                        user.getId(),
                        request.getAmount()
                );
    }

    @PostMapping("/transfer")
    public TransferResponse transfer(@Valid @RequestBody TransferRequest request) {

        User currentUser = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        System.out.println("Controller" + request.getReceiverEmail());

        return walletService.transfer(currentUser.getId(),request);
    }


}
