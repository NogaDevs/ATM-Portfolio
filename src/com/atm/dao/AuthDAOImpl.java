package com.atm.dao;

import com.atm.dao.util.JdbcHelper;
import com.atm.dto.AuthRecord;

public class AuthDAOImpl implements AuthDAO {

    private static final String FIND_BY_ID = "SELECT customer_id, customer_name, role, pin_hash, failed_attempts, is_locked FROM customers WHERE customer_id = ?";
    private static final String FIND_BY_CARD_NUMBER = "SELECT customer_id, customer_name, role, pin_hash, failed_attempts, is_locked FROM customers WHERE card_number = ?";
    private static final String REGISTER_FAILED_ATT = "UPDATE customers SET failed_attempts = failed_attempts + 1 WHERE customer_id = ?";
    private static final String RESET_ATTEMPTS = "UPDATE customers SET failed_attempts = 0 WHERE customer_id = ?";
    private static final String LOCK_UNLOCK_CUSTOMER = "UPDATE customers SET is_locked = ? WHERE customer_id = ?";

    @Override
    public AuthRecord findCustomerById(int customerId) {

        return JdbcHelper.executeQueryReturnOne(
                FIND_BY_ID,
                stmt -> stmt.setInt(1, customerId),
                rs -> new AuthRecord(
                        rs.getInt("customer_id"),
                        rs.getString("customer_name"),
                        rs.getString("role"),
                        rs.getString("pin_hash"),
                        rs.getInt("failed_attempts"),
                        rs.getBoolean("is_locked")),
                "Could not find the customer id: " + customerId
        );
    }

    @Override
    public AuthRecord findCustomerByCardNumber(String cardNumber) {

        return JdbcHelper.executeQueryReturnOne(
                FIND_BY_CARD_NUMBER,
                stmt -> stmt.setString(1, cardNumber),
                rs -> new AuthRecord(
                        rs.getInt("customer_id"),
                        rs.getString("customer_name"),
                        rs.getString("role"),
                        rs.getString("pin_hash"),
                        rs.getInt("failed_attempts"),
                        rs.getBoolean("is_locked")),
                "Could not find the card number: " + cardNumber
        );
    }

    @Override
    public void registerFailedAttempts(int customerId) {

        JdbcHelper.executeUpdate(
                REGISTER_FAILED_ATT,
                stmt -> stmt.setInt(1, customerId),
                "Record not found by that ID");

    }

    @Override
    public void resetAttempts(int customerId) {

        JdbcHelper.executeUpdate(
                RESET_ATTEMPTS,
                stmt -> stmt.setInt(1, customerId),
                "Record not found by that ID");
    }

    @Override
    public void lockCustomer(int customerId) {

        JdbcHelper.executeUpdate(LOCK_UNLOCK_CUSTOMER, stmt -> {
            stmt.setBoolean(1, true);
            stmt.setInt(2, customerId);
        }, "Record not found by that ID");
    }

    @Override
    public void unlockCustomer(int customerId) {

        JdbcHelper.executeUpdate(LOCK_UNLOCK_CUSTOMER, stmt -> {
            stmt.setBoolean(1, false);
            stmt.setInt(2, customerId);
        }, "Record not found by that ID");

        resetAttempts(customerId);
    }
}
