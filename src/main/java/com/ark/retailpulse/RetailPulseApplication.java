package com.ark.retailpulse;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RetailPulseApplication {

    public static void main(String[] args) {
        SpringApplication.run(RetailPulseApplication.class, args);
    }


}
