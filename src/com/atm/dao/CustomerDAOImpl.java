package com.atm.dao;

import java.math.BigDecimal;
import java.sql.*;

import com.atm.dao.util.JdbcHelper;
import com.atm.domain.Customer;
import com.atm.domain.Role;
import com.atm.exception.*;


public class CustomerDAOImpl implements CustomerDAO {


    private static final String READ = "SELECT customer_name, role, card_number, email, balance FROM customers WHERE customer_id = ?";
    private static final String INSERT = """
                INSERT INTO customers (customer_name, role, card_number, pin_hash, email, balance)\s
                VALUES (? , ? , ? , ? , ? , ?)
                RETURNING customer_id;
                """;
    private static final String UPDATE = "UPDATE customers SET ";
    private static final String DELETE = "DELETE FROM customers WHERE customer_id = ? RETURNING customer_id";
    private static final String FIND_BY_CARDNUMBER = "SELECT customer_id, card_number FROM customers WHERE card_number = ?";
    private static final String FIND_BALANCE_BY_ID = "SELECT balance FROM customers WHERE customer_id = ?";
    private static final String FIND_BY_EMAIL = "SELECT customer_id, email FROM customers WHERE email = ?";
    private static final String UPDATE_BALANCE = "UPDATE customers SET balance = balance + ? WHERE customer_id = ? ";

    @Override
    public int create(String validatedName, Role role, String formattedCardNumber, String validatedEmail, BigDecimal validStartingBalance, String pinHash) {

        return JdbcHelper.executeQueryReturnOne(
                INSERT,
                stmt -> {
                    stmt.setString(1, validatedName);
                    stmt.setString(2, String.valueOf(role));
                    stmt.setString(2, formattedCardNumber);
                    stmt.setString(3, pinHash);
                    stmt.setString(4, validatedEmail);
                    stmt.setBigDecimal(5, validStartingBalance);
                    },
                rs -> rs.getInt("customer_id"),
                "There was a problem registering your entry into the data base when creating the user. Check the logs.");
    }

    @Override
    public Customer read(int id) {

        return JdbcHelper.executeQueryReturnOne(
                READ,
                stmt -> stmt.setInt(1, id),
                rs -> new Customer(
                        id,
                        rs.getString("customer_name"),
                        rs.getString("role"),
                        rs.getString("card_number"),
                        rs.getString("email"),
                        rs.getBigDecimal("balance")),
                "Id not found: " + id);

    }

    @Override
    public void update(int customerId, String validatedName, String formattedCardNumber, String validatedEmail, String pin_hash) {

        String updateSql = buildUpdateSql(validatedName, formattedCardNumber, validatedEmail, pin_hash);

        JdbcHelper.executeUpdate(
                updateSql,
                stmt -> bindUpdateParams(
                    stmt,
                    customerId,
                    validatedName,
                    formattedCardNumber,
                    validatedEmail,
                    pin_hash
                ),
                "There was a problem updating the record."
        );
    }

    @Override
    public void delete(int customerId) {

        JdbcHelper.executeUpdate(
                DELETE,
                stmt -> stmt.setInt(1, customerId),
                "Customer could not be deleted."
        );
    }

    private String buildUpdateSql(String validatedName,
                                  String formattedCardNumber,
                                  String validatedEmail,
                                  String pinHash) {

        StringBuilder updateSql = new StringBuilder(UPDATE);
        boolean first = true;

        if (validatedName != null && !validatedName.isBlank()) {
            updateSql.append("customer_name = ?");
            first = false;
        }
        if (formattedCardNumber != null && !formattedCardNumber.isBlank()) {
            updateSql.append(first ? "" : ", ").append("card_number = ?");
            first = false;
        }
        if (validatedEmail != null && !validatedEmail.isBlank()) {
            updateSql.append(first ? "" : ", ").append("email = ?");
            first = false;
        }
        if (pinHash != null && !pinHash.isBlank()) {
            updateSql.append(first ? "" : ", ").append("pinHash = ?");
            first = false;
        }
        if (first) {
            throw new IllegalArgumentException("Field can't be empty.");
        }
        updateSql.append(", updated_at = NOW() WHERE customer_id = ?");

        return updateSql.toString();
    }
    private void bindUpdateParams(PreparedStatement stmt,
                                  int customerId,
                                  String validatedName,
                                  String formattedCardNumber,
                                  String validatedEmail,
                                  String pinHash) throws SQLException {

        int stmtAttIndex = 1;
        if (validatedName != null && !validatedName.isBlank()) {stmt.setString(stmtAttIndex++, validatedName);}
        if (formattedCardNumber != null && !formattedCardNumber.isBlank()) {stmt.setString(stmtAttIndex++, formattedCardNumber);}
        if (validatedEmail != null && !validatedEmail.isBlank()) {stmt.setString(stmtAttIndex++, validatedEmail);}
        if (pinHash != null && !pinHash.isBlank()) {stmt.setString(stmtAttIndex++, pinHash);}
        stmt.setInt(stmtAttIndex++, customerId);

    }

    @Override
    public BigDecimal adjustBalanceReturningBalance(int customerId, BigDecimal delta) {
        StringBuilder updateBalance = new StringBuilder(UPDATE_BALANCE);
        if (delta == null) {
            throw new InvalidAmountException("Amount cannot be null.");
        }
        if (delta.signum() == 0) {
            throw new InvalidAmountException("Amount cannot be zero.");
        }

        BigDecimal amount = delta.abs();
        boolean isDeposit = delta.signum() > 0;

        if(isDeposit){ // Deposit
            updateBalance.append("RETURNING balance");
        } else {      // Withdraw
            updateBalance.append("AND balance >= ? RETURNING balance");
        }

        try(Connection con = DB.connect();
            PreparedStatement myStmtUpdate = con.prepareStatement(updateBalance.toString())) {

            con.setAutoCommit(false);

            myStmtUpdate.setBigDecimal(1, delta);
            myStmtUpdate.setInt(2, customerId);
            if (!isDeposit) {
                myStmtUpdate.setBigDecimal(3, amount);
            }

            try (ResultSet result = myStmtUpdate.executeQuery()) {

                // If no rows were affected, check if it's due to invalid ID or insufficient funds
                if (!result.next()) {
                    try (PreparedStatement myStmtId = con.prepareStatement(READ)) {
                        myStmtId.setInt(1, customerId);
                        try (ResultSet resultSetForId = myStmtId.executeQuery()) {
                            if (!resultSetForId.next()) {
                                con.rollback();
                                throw new CustomerNotFoundException("Customer not found. Customer ID: " + customerId);
                            } else {
                                con.rollback();
                                throw new InvalidAmountException("There are not enough funds.");
                            }
                        }
                    }
                } else {
                    BigDecimal newBalance  = result.getBigDecimal("balance");
                    con.commit();
                    return newBalance;
                }
            }
            catch (SQLTimeoutException e) {
                con.rollback();
                throw new DatabaseException("Timeout Error! There was a technical problem with the database.", e);
            }
            catch (SQLException e) {
                con.rollback();
                throw new DatabaseException("withdraw failed: ", e);
            }
        }
        catch (SQLException e) {
            throw new DatabaseException("withdraw failed: ", e);
        }
    }

    @Override
    public boolean existsByCardNumber(String cardNumber) {

        return JdbcHelper.executeExists(
                FIND_BY_CARDNUMBER,
                stmt -> stmt.setString(1, cardNumber),
                "There was a problem querying the card number."
        );

    }

    @Override
    public boolean existsByEmail(String email) {

        return JdbcHelper.executeExists(
                FIND_BY_EMAIL,
                stmt -> stmt.setString(1, email),
                "There was a problem querying the email."
        );

    }

    @Override
    public int findIdByCardNumber(String cardNumber) {

        int customerId = JdbcHelper.executeQueryReturnOne(
                FIND_BY_CARDNUMBER,
                stmt -> stmt.setString(1, cardNumber),
                rs -> rs.getInt("customer_id"),
                "There was a problem querying the card number."
        );
        if (customerId > 0) {
            return customerId;
        } else {
            throw new CustomerNotFoundException("User not found!");
        }
    }

    @Override
    public BigDecimal getBalanceById(int id) {

        return JdbcHelper.executeQueryReturnOne(
                FIND_BALANCE_BY_ID,
                stmt -> stmt.setInt(1, id),
                rs -> rs.getBigDecimal("balance"),
                "Customer not found. Could not return balance."
        );
    }
}
