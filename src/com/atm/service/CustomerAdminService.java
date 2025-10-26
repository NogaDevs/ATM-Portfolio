package com.atm.service;
import com.atm.dao.CustomerDAO;
import com.atm.dto.CustomerCreateRequest;
import com.atm.dto.CustomerUpdateRequest;
import com.atm.exception.CustomerNotFoundException;
import com.atm.exception.InvalidInputException;
import java.math.BigDecimal;
import java.util.StringJoiner;
import java.util.regex.Pattern;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


public class CustomerAdminService {
    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder(12);
    private static final CustomerDAO CUSTOMER_DAO = new CustomerDAO();


    public int registerCustomer(CustomerCreateRequest input){
        String validatedName = "";
        String validCardNumber = "";
        String validatedEmail = "";
        BigDecimal validStartingBalance = null;
        String pin_hash = "";

        if (verifyName(input.getName())) {
            validatedName = nameFormatter(input.getName());
        }

        if (verifyCardNumber(input.getCardNumber())) {
            validCardNumber = input.getCardNumber();
        }

        String plainPin = input.getPlainPin();
        if (verifyPin(plainPin)) {
            pin_hash = encodePin(plainPin);
        }

        if (verifyEmail(input.getEmail())) {
            validatedEmail = input.getEmail();
        }

        if (verifyStartingBalance(input.getStartingBalance())) {
            validStartingBalance = input.getStartingBalance();
        }

        int newUserId;
        newUserId = CUSTOMER_DAO.create(
                validatedName, validCardNumber, validatedEmail, validStartingBalance, pin_hash
        );
        return newUserId;
    }
    public int updateCustomer(CustomerUpdateRequest input){
        int customerId = input.getCustomerId();
        String validatedName = "";
        String validCardNumber = "";
        String validatedEmail = "";
        String pin_hash = "";

        if (!input.getNewName().isBlank()) {
            verifyName(input.getNewName());
            validatedName = nameFormatter(input.getNewName());
        }

        if (!input.getNewCardNumber().isBlank()) {
            verifyCardNumber(input.getNewCardNumber());
            validCardNumber = input.getNewCardNumber();
        }

        if (!input.getNewPlainPin().isBlank()) {
            verifyPin(input.getNewPlainPin());
            pin_hash = encodePin(input.getNewPlainPin());
        }

        if (!input.getNewEmail().isBlank()) {
            System.out.println(input.getNewEmail());
            boolean verify = verifyEmail(input.getNewEmail());
            validatedEmail = input.getNewEmail();
        }

        CUSTOMER_DAO.update(
                customerId, validatedName, validCardNumber, validatedEmail, pin_hash
        );
        return customerId;
    }
    public void deleteCustomer(int customerId) {

        if (customerId <= 0) {
            throw new InvalidInputException("ERROR! Invalid input: " + customerId);
        }

        boolean deleted = CUSTOMER_DAO.delete(customerId);
        if (!deleted) {
            throw new CustomerNotFoundException("ERROR! Customer not found. ID: " + customerId);
        }
    }
    public static boolean verifyName(String plainName) {
        if (plainName == null || plainName.isBlank()) {
            throw new InvalidInputException("Name can't be blank.");
        } else if (!plainName.matches("[a-zA-Z\\s]+$")) {
            throw new InvalidInputException("Only alphabet accepted for names.");
        } else if (plainName.length() > 30) {
            throw new InvalidInputException("Name length cannot be over 30 characters.");
        } else {
            return true;
        }
    }
    private static String nameFormatter(String name) {

        if (name.isBlank()) {
            throw new InvalidInputException("Name could not be formatted because the name is blank.");
        }

        // Capitalizes the name.
        String[] nameSplit = name.trim().split("\\s+");
        StringJoiner nameJoiner = new StringJoiner(" "); // Appends the StringBuilder inside the loop.

        for (int i = 0; i < nameSplit.length; i++) {
            StringBuilder nameBuilder = new StringBuilder(); //Placeholder for the name to be appended.
            for (int x = 0; x < nameSplit[i].length(); x++) {
                if (x < 1) {
                    nameBuilder.append(Character.toUpperCase(nameSplit[i].charAt(x)));
                } else {
                    nameBuilder.append(Character.toLowerCase(nameSplit[i].charAt(x)));
                }
            }
            nameJoiner.add(nameBuilder.toString());
        }
        return nameJoiner.toString();
    }
    private static boolean verifyCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.isBlank()) {
            throw new InvalidInputException("Card number cannot be blank!");
        } else if (cardNumber.length() < 13 || cardNumber.length() > 19) {
            throw new InvalidInputException("Card length not valid.");
        } else if (!cardNumber.matches("^[0-9]+$")) {
            throw new InvalidInputException("Only digits are valid for a card number.");
        } else {
            if (CUSTOMER_DAO.checkIfRegisteredCard(cardNumber)){
                throw new InvalidInputException("Credit card already in use!");
            } else {
                return true;
            }
        }
    }
    private static boolean verifyEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        if (email == null || email.isBlank()){
            throw new InvalidInputException("Email address cannot be blank.");
        } else if (!pattern.matcher(email).matches()) {
            throw new InvalidInputException("Invalid email format address.");
        } else if (CUSTOMER_DAO.checkIfRegisteredMail(email)) {
            throw new InvalidInputException("Email already registered.");
        } else {
            return true;
        }
    }
    private static boolean verifyPin(String plainPin) {
        if (plainPin == null || plainPin.isBlank()) {
            throw new InvalidInputException("Pin can't be blank.");
        } else if (plainPin.length() != 4) {
            throw new InvalidInputException("Invalid length! Password must have 4 digits.");
        } else if (!plainPin.matches("\\d+")) {
            throw new InvalidInputException("Invalid format! Password must be digits.");
        } else {
            return true;
        }
    }
    private static boolean verifyStartingBalance(BigDecimal startingBalance) {
        if (startingBalance == null) {
            throw new InvalidInputException("Input missing! Starting balance needs to be added.");
        } else if (startingBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidInputException("Balance cannot be less than zero.");
        } else {
            return true;
        }
    }
    public static String encodePin(String plainPin) {
        return ENCODER.encode(plainPin);
    }
    public static boolean validatePin(String plainPin, String hashedPin) {
        return ENCODER.matches(plainPin, hashedPin);
    }
}
