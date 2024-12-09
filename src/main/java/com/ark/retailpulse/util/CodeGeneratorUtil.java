package com.ark.retailpulse.util;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class CodeGeneratorUtil {

    public String generateConfirmationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // Generates a 6-digit random number
        return String.valueOf(code);
    }
}
