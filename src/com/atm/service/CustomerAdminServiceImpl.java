package com.atm.service;
import com.atm.dao.CustomerDAOImpl;
import com.atm.domain.Role;
import com.atm.dto.CustomerCreateRequest;
import com.atm.dto.CustomerUpdateRequest;
import com.atm.exception.DuplicateCardNumberException;
import com.atm.exception.InvalidInputException;
import java.math.BigDecimal;

import com.atm.util.NameUtils;
import com.atm.util.SecurityUtils;
import com.atm.util.ValidationUtils;



public class CustomerAdminServiceImpl implements CustomerAdminService {
    private static final CustomerDAOImpl CUSTOMER_DAO = new CustomerDAOImpl();

    @Override
    public int registerCustomer(CustomerCreateRequest input){

        String validatedName;
        String validCardNumber;
        String validatedEmail;
        BigDecimal validStartingBalance;
        String hashedPin;

        // Validation. Check for already registered unique values or invalid inputs.
        if (CUSTOMER_DAO.existsByCardNumber(input.getCardNumber())){
            throw new DuplicateCardNumberException("Card number already in use.");
        }

        if (CUSTOMER_DAO.existsByEmail(input.getEmail())) {
            throw new InvalidInputException("Email address already in use.");
        }

        ValidationUtils.verifyName(input.getName());
        ValidationUtils.verifyCardNumberFormat(input.getCardNumber());
        String charPinToString = new String(input.getPlainPin());
        ValidationUtils.verifyPin(charPinToString);
        ValidationUtils.verifyEmailFormat(input.getEmail());
        ValidationUtils.verifyStartingBalance(input.getStartingBalance());

        validatedName = NameUtils.nameFormatter(input.getName());
        validCardNumber = input.getCardNumber();
        hashedPin = SecurityUtils.encodePin(charPinToString);
        validatedEmail = input.getEmail();
        validStartingBalance = input.getStartingBalance();

        Role role = Role.CUSTOMER;

        return CUSTOMER_DAO.create(
                validatedName, role, validCardNumber, validatedEmail, validStartingBalance, hashedPin
        );
    }

    @Override
    public int updateCustomer(CustomerUpdateRequest input){

        int customerId = input.getCustomerId();
        String validatedName = "";
        String validCardNumber = "";
        String validatedEmail = "";
        String hashedPin = "";

        if (!input.getNewEmail().isBlank()) {
            if (CUSTOMER_DAO.existsByEmail(input.getNewEmail())) {
                throw new InvalidInputException("ERROR! Email already registered.");
            }
        }

        if (!input.getNewName().isBlank()) {
            ValidationUtils.verifyName(input.getNewName());
            validatedName = NameUtils.nameFormatter(input.getNewName());
        }

        if (!input.getNewCardNumber().isBlank()) {
            ValidationUtils.verifyCardNumberFormat(input.getNewCardNumber());
            validCardNumber = input.getNewCardNumber();
        }

        if(input.getNewPlainPin().length != 0){
            String charPinToString = new String(input.getNewPlainPin());
            ValidationUtils.verifyPin(charPinToString);
            hashedPin = SecurityUtils.encodePin(charPinToString);
        }

        if (!input.getNewEmail().isBlank()) {
            ValidationUtils.verifyEmailFormat(input.getNewEmail());
            validatedEmail = input.getNewEmail();
        }

        CUSTOMER_DAO.update(
                customerId, validatedName, validCardNumber, validatedEmail, hashedPin
        );
        return customerId;
    }

    @Override
    public void deleteCustomer(int customerId) {

        if (customerId <= 0) {
            throw new InvalidInputException("ERROR! Invalid input: " + customerId);
        }
        CUSTOMER_DAO.delete(customerId);
    }
}
