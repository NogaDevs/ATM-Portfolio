package com.atm.service;

import com.atm.dto.CustomerCreateRequest;
import com.atm.exception.InvalidInputError;

import java.math.BigDecimal;
import java.util.regex.Pattern;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


public class CustomerAdminService {

    void registerCustomer(CustomerCreateRequest input){


        // Name validation
        if (!input.getName().matches("\"[a-zA-Z]+(\\\\s+[a-zA-Z]+)*\"")) {
            throw new InvalidInputError("Invalid format. Only alphabet accepted for the name.");
        } else {
            String validatedName = input.getName().strip();
        }
        // Card validation
        if (input.getPlainPin().matches("[a-zA-Z]")) {
            throw new InvalidInputError("Invalid format. Only numbers are allowed for the card number.");
        } else {
            String formattedCardNumber = formatCardNumber(input.getCardNumber());
        }
        // Validate pin. If pin is not 4 digits and all chars are not digits, throws error.
        String plainPin = input.getPlainPin();
        if (plainPin.length() != 4) {
            throw new InvalidInputError("Invalid length! Password must have 4 digits.");
        } else if (!plainPin.matches("\\d+")) {
            throw new InvalidInputError("Invalid format! Password must be digits.");
        }
        // Email validation
        boolean emailIsValid = isValidEmailAddress(input.getEmail());
        if (!emailIsValid) {
            throw new InvalidInputError("Invalid email address.");
        } else {
            String validatedEmail = input.getEmail();
        }
        // Starting balance validation
        if (input.getStartingBalance().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidInputError("Balance cannot be less than zero.");
        } else {
            BigDecimal validBalance = input.getStartingBalance();
        }

        //TODO Buscar la manera de usar BYcrypt. Parece que esta dentro del framework spring...

        // plainPin to pin_hash encrypt (BCrypt)
        // pass the data through the dao and create the user
        // return the ID afterward




//        query = """
//                INSERT INTO customers (customer_name, card_number, pin_hash, email, balance)\s
//                VALUES (? , ? , ? , ? , ?);""";
    }
    private static String formatCardNumber(String cardNumber) {
        if (cardNumber.length() < 13 || cardNumber.length() > 19) {
            throw new InvalidInputError("Card length not valid.");
        }

        String formattedCardNumber;
        formattedCardNumber = cardNumber.strip().replaceAll("[^0-9]", "");
        return formattedCardNumber;
    }
    private static boolean isValidEmailAddress(String email) {
        // Regex to check if it's a valid format.
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

        Pattern pattern = Pattern.compile(emailRegex);

        return email != null && pattern.matcher(email).matches();
    }
    public static String encodePin(String plainPin) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(16);
        return encoder.encode(plainPin);
    }
}
