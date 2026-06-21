package com.atm.dao;

import com.atm.domain.Customer;
import com.atm.domain.Role;

import java.math.BigDecimal;
import java.sql.SQLException;


/**
 * Provides administrative operations for managing customers,
 * including creation, update, and deletion.
 */
public interface CustomerDAO {

    int create(String validatedName,
               Role role,
               String formattedCardNumber,
               String validatedEmail,
               BigDecimal validStartingBalance,
               String pinHash);

    Customer read(int id);
    void update(int customerId,
                String validatedName,
                String formattedCardNumber,
                String validatedEmail,
                String pinHash);
    void delete(int customerId);
    BigDecimal adjustBalanceReturningBalance(int id, BigDecimal delta);
    boolean existsByCardNumber(String cardNumber);
    boolean existsByEmail(String email);
    int findIdByCardNumber(String cardNumber);
    BigDecimal getBalanceById(int id);

    @FunctionalInterface
    public interface SqlConsumer<T> {
        void accept(T t) throws SQLException;
    }

}
