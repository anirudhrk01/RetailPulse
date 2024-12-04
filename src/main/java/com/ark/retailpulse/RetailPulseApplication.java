package com.ark.retailpulse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EntityScan(basePackages = "com.ark.retailpulse.model")
@EnableJpaRepositories(basePackages="com.ark.retailpulse.repository")
@EnableScheduling
public class RetailPulseApplication {

    public static void main(String[] args) {
        SpringApplication.run(RetailPulseApplication.class, args);
    }

}
