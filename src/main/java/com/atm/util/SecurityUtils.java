package com.atm.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public final class SecurityUtils {

    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

    private SecurityUtils() {}

    public static String encodePin(String plainPin) {
        return ENCODER.encode(plainPin);
    }
    public static boolean validatePin(String plainPin, String hashedPin) {
        return ENCODER.matches(plainPin, hashedPin);
    }
}
