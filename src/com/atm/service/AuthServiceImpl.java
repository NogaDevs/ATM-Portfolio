package com.atm.service;

import com.atm.dao.AuthDAOImpl;
import com.atm.dto.AuthRecord;
import com.atm.exception.InvalidCredentialsException;
import com.atm.session.SessionManagerImpl;
import com.atm.util.SecurityUtils;
import com.atm.util.ValidationUtils;
import java.util.Arrays;

public class AuthServiceImpl implements AuthService {

    private static final AuthDAOImpl AUTH_DAO = new AuthDAOImpl();
    private final SessionManagerImpl sessionManager;

    public AuthServiceImpl (SessionManagerImpl session) {
        this.sessionManager = session;
    }

    @Override
    public AuthRecord authenticatePin(int customerId, char[] plainPin) {

        sessionManager.requireActive();

        AuthRecord customerRecord = AUTH_DAO.findCustomerById(customerId);
        ValidationUtils.checkAuthStatus(AUTH_DAO, customerRecord, customerId);

        String charToStringPin = new String(plainPin);
        if (SecurityUtils.validatePin(charToStringPin, customerRecord.getHashedPin())) {
            Arrays.fill(plainPin, '\0');
            AUTH_DAO.resetAttempts(customerId);
            return customerRecord;
        } else {
            Arrays.fill(plainPin, '\0');
            throw new InvalidCredentialsException("Pin not valid.");
        }
    }

    @Override
    public void loginByCard(String cardNumber, char[] plainPin) {

        AuthRecord customerRecord = AUTH_DAO.findCustomerByCardNumber(cardNumber);
        ValidationUtils.checkAuthStatus(AUTH_DAO, customerRecord, customerRecord.getCustomerId());

        String charToStringPin = new String(plainPin);
        if (SecurityUtils.validatePin(charToStringPin, customerRecord.getHashedPin())) {
            Arrays.fill(plainPin, '\0');
            AUTH_DAO.resetAttempts(customerRecord.getCustomerId());
            sessionManager.createSession(
                    customerRecord.getCustomerId(),
                    customerRecord.getCustomerName(),
                    customerRecord.getCustomerRole()
                    );
        } else {
            Arrays.fill(plainPin, '\0');
            AUTH_DAO.registerFailedAttempts(customerRecord.getCustomerId());
            throw new InvalidCredentialsException("Invalid credentials.");
        }
    }

    @Override
    public void logout() {
        sessionManager.logout();
    }
}
