package com.atm.dao;

import com.atm.dto.AuthRecord;

public interface AuthDAO {

    AuthRecord findCustomerById(int customerId);
    AuthRecord findCustomerByCardNumber(String cardNumber);
    void registerFailedAttempts(int customerId);
    void resetAttempts(int customerId);
    void lockCustomer(int customerId);
    void unlockCustomer(int customerId);

}
