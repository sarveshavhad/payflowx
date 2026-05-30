package com.payflowx.payment_core.wallet.controller;


import com.payflowx.payment_core.user.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.Authenticator;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    @GetMapping("/me")
    public String currentUser(Authentication authentication){
        User user = (User) authentication.getPrincipal();

        return "Logged in as : " + user.getEmail();
    }
}
