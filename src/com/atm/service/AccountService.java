package com.atm.service;
import java.math.BigDecimal;
import com.atm.dao.CustomerDAO;
import com.atm.exception.CustomerNotFoundException;
import com.atm.exception.InsufficientFundsException;
import com.atm.exception.InvalidAmountException;


public class AccountService {

    private static final CustomerDAO CUSTOMER_DAO = new CustomerDAO();

    public BigDecimal deposit(int customerId, BigDecimal amount) {

        BigDecimal newBalance;

        if (amount == null) {
            throw new InvalidAmountException("Amount can't be blank.");
        } else if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("You can't deposit zero or less dollars.");
        } else if (customerId <= 0) {
            throw new CustomerNotFoundException("Invalid user ID.");
        } else {
            newBalance = CUSTOMER_DAO.adjustBalanceReturningBalance(customerId, amount);
            return newBalance;
        }
    }
    public BigDecimal withdraw(int customerId, BigDecimal amount) {

        BigDecimal newBalance;

        if (amount == null) {
            throw new InvalidAmountException("Invalid input.");
        } else if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InsufficientFundsException("You can't withdraw zero or less dollars.");
        } else if (customerId <= 0) {
            throw new CustomerNotFoundException("Invalid user ID.");
        } else {
            newBalance = CUSTOMER_DAO.adjustBalanceReturningBalance(customerId, amount.negate());
            return newBalance;
        }
    }
}

