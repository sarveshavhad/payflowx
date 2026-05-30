package com.payflowx.payment_core.wallet.repository;

import com.payflowx.payment_core.wallet.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WalletRepository extends JpaRepository<Wallet, UUID> {

}
