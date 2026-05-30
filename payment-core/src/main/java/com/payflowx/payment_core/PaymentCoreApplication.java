package com.payflowx.payment_core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class PaymentCoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaymentCoreApplication.class, args);
	}

}
