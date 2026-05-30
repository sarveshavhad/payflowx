package com.payflowx.payment_core.auth.controller;

import com.payflowx.payment_core.auth.dto.LoginRequest;
import com.payflowx.payment_core.auth.dto.LoginResponse;
import com.payflowx.payment_core.auth.dto.RegisterRequest;
import com.payflowx.payment_core.auth.dto.RegisterResponse;
import com.payflowx.payment_core.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {


    private final AuthService authService;

    @PostMapping("/register")
    public RegisterResponse register(@RequestBody RegisterRequest request){

        return authService.register(request);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request){
        return authService.login(request);
    }
}
