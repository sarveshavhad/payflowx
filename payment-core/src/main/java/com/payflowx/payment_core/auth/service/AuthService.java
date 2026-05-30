package com.payflowx.payment_core.auth.service;

import com.payflowx.payment_core.auth.dto.LoginRequest;
import com.payflowx.payment_core.auth.dto.LoginResponse;
import com.payflowx.payment_core.auth.dto.RegisterRequest;
import com.payflowx.payment_core.auth.dto.RegisterResponse;
import com.payflowx.payment_core.common.enums.UserRole;
import com.payflowx.payment_core.security.jwt.JwtService;
import com.payflowx.payment_core.user.entity.User;
import com.payflowx.payment_core.user.repository.UserRepository;
import com.payflowx.payment_core.wallet.entity.Wallet;
import com.payflowx.payment_core.wallet.repository.WalletRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private  final WalletRepository walletRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public RegisterResponse register(RegisterRequest request){

        if(userRepository.existsByEmail(request.getEmail())){
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.CUSTOMER);
        User savedUser = userRepository.save(user);

        Wallet wallet = new Wallet();
        wallet.setUser(savedUser);
        wallet.setBalance(BigDecimal.ZERO);
        walletRepository.save(wallet);

        return new RegisterResponse("User registered successfully");
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->new RuntimeException("Invalid credentials"));

        boolean isPasswordValid = passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        );

        if (!isPasswordValid){
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtService.generateToken(user.getId());
        return new LoginResponse(token);
    }
}
