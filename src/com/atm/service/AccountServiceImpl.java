package com.atm.service;
import java.math.BigDecimal;
import com.atm.dao.CustomerDAOImpl;
import com.atm.exception.CustomerNotFoundException;
import com.atm.exception.InsufficientFundsException;
import com.atm.exception.InvalidAmountException;
import com.atm.session.SessionManagerImpl;


public class AccountServiceImpl implements AccountService {

    private static final CustomerDAOImpl CUSTOMER_DAO = new CustomerDAOImpl();
    private final SessionManagerImpl sessionManager;

    public AccountServiceImpl(SessionManagerImpl session) {
        this.sessionManager = session;
    }

    @Override
    public BigDecimal deposit(int customerId, BigDecimal amount) {

        sessionManager.requireActive();
        BigDecimal newBalance;

        if (amount == null) {
            throw new InvalidAmountException("Amount can't be blank.");
        } else if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("You can't deposit zero or less dollars.");
        } else if (customerId <= 0) {
            throw new CustomerNotFoundException("Invalid user ID.");
        } else {
            newBalance = CUSTOMER_DAO.adjustBalanceReturningBalance(customerId, amount);
            sessionManager.touch();
            return newBalance;
        }
    }

    @Override
    public BigDecimal withdraw(int customerId, BigDecimal amount) {

        sessionManager.requireActive();
        sessionManager.touch();
        BigDecimal newBalance;

        if (amount == null) {
            throw new InvalidAmountException("Invalid input.");
        } else if (amount.compareTo(BigDecimal.valueOf(0.01)) < 0) {
            throw new InvalidAmountException("You can't withdraw less than 1 cent.");
        } else if (customerId <= 0) {
            throw new CustomerNotFoundException("Invalid user ID.");
        } else if (amount.compareTo(CUSTOMER_DAO.getBalanceById(customerId)) > 0) {
            throw new InsufficientFundsException("Insufficient funds.");
        } else {
            newBalance = CUSTOMER_DAO.adjustBalanceReturningBalance(customerId, amount.negate());
            return newBalance;
        }
    }
}

