package com.payflowx.payment_core.config.initializer;

import com.payflowx.payment_core.common.constants.SystemConstants;
import com.payflowx.payment_core.common.enums.UserRole;
import com.payflowx.payment_core.user.entity.User;
import com.payflowx.payment_core.user.repository.UserRepository;
import com.payflowx.payment_core.wallet.entity.Wallet;
import com.payflowx.payment_core.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
@RequiredArgsConstructor
public class SystemUserInitializer {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;

    @Bean
    public ApplicationRunner createSystemUser(){

        return args -> {

            if(userRepository.findByEmail(SystemConstants.SYSTEM_EMAIL).isPresent()) {
                return;
            }

            User systemUser = new User();

            systemUser.setName(SystemConstants.SYSTEM_NAME);
            systemUser.setEmail(SystemConstants.SYSTEM_EMAIL);
            systemUser.setPassword("SYSTEM");
            systemUser.setRole(UserRole.ADMIN);

            User savedUser = userRepository.save(systemUser);

            Wallet systemWallet = new Wallet();
            systemWallet.setUser(savedUser);
            systemWallet.setBalance(new BigDecimal("1000000000"));
            walletRepository.save(systemWallet);
        };
    }


}
