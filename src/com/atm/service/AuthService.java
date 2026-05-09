package com.atm.service;

import com.atm.dto.AuthRecord;

public interface AuthService {

    public AuthRecord authenticatePin(int customerId, char[] plainPin);
    public void loginByCard(String cardNumber, char[] plainPin);
    public void logout();

}
