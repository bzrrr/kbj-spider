package com.bzrrr.kbjspider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement(proxyTargetClass = true)
public class KbjSpiderApplication {

	public static void main(String[] args) {
		SpringApplication.run(KbjSpiderApplication.class, args);
	}

}
