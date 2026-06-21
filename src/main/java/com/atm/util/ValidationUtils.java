package com.atm.util;

import com.atm.dao.AuthDAOImpl;
import com.atm.dto.AuthRecord;
import com.atm.exception.InvalidCredentialsException;
import com.atm.exception.InvalidInputException;
import com.atm.exception.LockedCustomerException;

import java.math.BigDecimal;
import java.util.regex.Pattern;

public class ValidationUtils {

    private ValidationUtils(){}

    public static void verifyName(String plainName) {

        if (plainName == null || plainName.isBlank()) {
            throw new InvalidInputException("Name can't be blank.");
        } else if (!plainName.matches("[a-zA-Z\\s]+$")) {
            throw new InvalidInputException("Only alphabet accepted for names.");
        } else if (plainName.length() > 30) {
            throw new InvalidInputException("Name length cannot be over 30 characters.");
        }
    }
    public static void verifyCardNumberFormat(String cardNumber) {

        if (cardNumber == null || cardNumber.isBlank()) {
            throw new InvalidInputException("Card number cannot be blank!");
        } else if (cardNumber.length() < 13 || cardNumber.length() > 19) {
            throw new InvalidInputException("Card length not valid.");
        } else if (!cardNumber.matches("^[0-9]+$")) {
            throw new InvalidInputException("Only digits are valid for a card number.");
        }
    }
    public static void verifyEmailFormat(String email) {

        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        if (email == null || email.isBlank()){
            throw new InvalidInputException("Email address cannot be blank.");
        } else if (!pattern.matcher(email).matches()) {
            throw new InvalidInputException("Invalid email format address.");
        }
    }
    public static void verifyPin(String plainPin) {

        if (plainPin == null || plainPin.isBlank()) {
            throw new InvalidInputException("Pin can't be blank.");
        } else if (plainPin.length() != 4) {
            throw new InvalidInputException("Invalid length! Password must have 4 digits.");
        } else if (!plainPin.matches("\\d+")) {
            throw new InvalidInputException("Invalid format! Password must be digits.");
        }
    }
    public static void checkAuthStatus(AuthDAOImpl authDAO, AuthRecord record, int customerId) {

        if (record == null) {
            throw new InvalidCredentialsException("Invalid credentials.");
        }
        if (record.getIsLocked()) {
            throw new LockedCustomerException("Account locked! Contact the administration.");
        }
        if (record.getFailedAttempts() >= 5) {
            authDAO.lockCustomer(customerId);
            authDAO.resetAttempts(customerId);
            throw new LockedCustomerException("User is locked! Please contact the administrator.");
        }
    }
    public static void verifyStartingBalance(BigDecimal startingBalance) {

        if (startingBalance == null) {
            throw new InvalidInputException("Input missing! Starting balance needs to be added.");
        } else if (startingBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidInputException("Balance cannot be less than zero.");
        }
    }
}
