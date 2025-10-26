package com.atm.dao;

import java.math.BigDecimal;
import java.sql.*;

import com.atm.domain.Customer;
import com.atm.exception.*;


public class CustomerDAO {

    private static final String READ = "SELECT * FROM customers WHERE customer_id = ?";
    private static final String INSERT = """
                INSERT INTO customers (customer_name, card_number, pin_hash, email, balance)\s
                VALUES (? , ? , ? , ? , ?)
                RETURNING customer_id;
                """;
    private static final String UPDATE = "UPDATE customers SET ";
    private static final String DELETE = "DELETE FROM customers WHERE customer_id = ?";
    private static final String FIND_BY_CARDNUMBER = "SELECT * FROM customers WHERE card_number = ?";
    private static final String FIND_BALANCE_BY_ID = "SELECT * FROM customers WHERE customer_id = ?";
    private static final String FIND_BY_EMAIL = "SELECT * FROM customers WHERE email = ?";
    private static final String UPDATE_BALANCE = "UPDATE customers SET balance = balance + ? WHERE customer_id = ? ";


    public int create(String validatedName, String formattedCardNumber, String validatedEmail, BigDecimal validStartingBalance, String pin_hash) {

        try (Connection con = DB.connect()) {
            PreparedStatement myStmt;

            myStmt = con.prepareStatement(INSERT);
            myStmt.setString(1, validatedName);
            myStmt.setString(2, formattedCardNumber);
            myStmt.setString(3, pin_hash);
            myStmt.setString(4, validatedEmail);
            myStmt.setBigDecimal(5, validStartingBalance);

            try (ResultSet resultSet = myStmt.executeQuery()) {
                if (resultSet.next()) {
                    int customerId = resultSet.getInt(1);
                    myStmt.close();
                    return customerId;
                } else {
                    myStmt.close();
                    throw new DatabaseException("There was a problem registering your entry into the data base when creating the user. Check the logs.");
                }
            }
            catch (SQLException e) {
                throw new DatabaseException("There was a problem with the data input or the database: ", e);
            }
        } catch (SQLException e) {
            throw new DatabaseException("There was a problem setting up the prepared statement: ", e);
        }
    }
    public Customer read(int id) {
        try (Connection con =  DB.connect()){

            PreparedStatement myStmt;
            myStmt = con.prepareStatement(READ);
            myStmt.setInt(1, id);
            try (ResultSet resultSet = myStmt.executeQuery()) {
                if (resultSet.next()) {
                    String name = resultSet.getString("customer_name");
                    String cardNumber = resultSet.getString("card_number");
                    String email = resultSet.getString("email");
                    BigDecimal balance = resultSet.getBigDecimal("balance");

                    myStmt.close();
                    return new Customer(id, name, cardNumber, email, balance);
                } else {
                    throw new CustomerNotFoundException("No such user id is already registered");
                }
            }
        }
        catch (SQLException e) {
            throw new DatabaseException("Database error:", e);
        }
    }
    public void update(int customerId, String validatedName, String formattedCardNumber, String validatedEmail, String pin_hash) {

        String updateSql = buildUpdateSql(validatedName, formattedCardNumber, validatedEmail, pin_hash);
        try (Connection con = DB.connect();
             PreparedStatement stmt = con.prepareStatement(updateSql)) {

            bindUpdateParams(stmt, customerId, validatedName, formattedCardNumber, validatedEmail, pin_hash);
            stmt.executeUpdate();

        }
        catch (SQLException e) {
            throw new DatabaseException("ERROR! Account couldn't be updated: ", e);
        }


    }
    public boolean delete(int customerId) {

        try (Connection con = DB.connect();
             PreparedStatement stmt = con.prepareStatement(DELETE)) {
            stmt.setInt(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
        catch (SQLException e) {
            throw new DatabaseException("ERROR! There was a problem deleting the record: ", e);
        }
    }
    private String buildUpdateSql(String validatedName,
                                  String formattedCardNumber,
                                  String validatedEmail,
                                  String pin_hash) {

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
        if (pin_hash != null && !pin_hash.isBlank()) {
            updateSql.append(first ? "" : ", ").append("pin_hash = ?");
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
                                  String pin_hash) throws SQLException {

        int stmtAttIndex = 1;
        if (validatedName != null && !validatedName.isBlank()) {stmt.setString(stmtAttIndex++, validatedName);}
        if (formattedCardNumber != null && !formattedCardNumber.isBlank()) {stmt.setString(stmtAttIndex++, formattedCardNumber);}
        if (validatedEmail != null && !validatedEmail.isBlank()) {stmt.setString(stmtAttIndex++, validatedEmail);}
        if (pin_hash != null && !pin_hash.isBlank()) {stmt.setString(stmtAttIndex++, pin_hash);}
        stmt.setInt(stmtAttIndex++, customerId);

    }
    public BigDecimal adjustBalanceReturningBalance(int id, BigDecimal delta) {
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
        } else { // Withdraw
            updateBalance.append("AND balance >= ? RETURNING balance");
        }

        try(Connection con = DB.connect();
            PreparedStatement myStmtUpdate = con.prepareStatement(updateBalance.toString())) {

            con.setAutoCommit(false);

            myStmtUpdate.setBigDecimal(1, delta);
            myStmtUpdate.setInt(2, id);
            if (!isDeposit) {
                myStmtUpdate.setBigDecimal(3, amount);
            }

            try (ResultSet result = myStmtUpdate.executeQuery()) {

                // If no rows were affected, check if it's due to invalid ID or insufficient funds
                if (!result.next()) {
                    try (PreparedStatement myStmtId = con.prepareStatement(READ)) {
                        myStmtId.setInt(1, id);
                        try (ResultSet resultSetForId = myStmtId.executeQuery()) {
                            if (!resultSetForId.next()) {
                                con.rollback();
                                throw new CustomerNotFoundException("Error! Customer not found.");
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
    public boolean checkIfRegisteredCard(String cardNumber) {

        try (Connection con =  DB.connect();
             PreparedStatement myStmt = con.prepareStatement(FIND_BY_CARDNUMBER)){

            myStmt.setString(1, cardNumber);
            try(ResultSet resultSet = myStmt.executeQuery()) {
                if (resultSet.next()) {
                    myStmt.close();
                    return true;
                } else {
                    myStmt.close();
                    return false;
                }
            }
        }
        catch (SQLException e) {
            throw new DatabaseException("There was a problem with the database: ", e);
        }
    }
    public boolean checkIfRegisteredMail(String email) {
        try (Connection con =  DB.connect();
             PreparedStatement myStmt = con.prepareStatement(FIND_BY_EMAIL)){

            myStmt.setString(1, email);
            try(ResultSet resultSet = myStmt.executeQuery()) {
                if (resultSet.next()) {
                    myStmt.close();
                    return true;
                } else {
                    myStmt.close();
                    return false;
                }
            }
        }
        catch (SQLException e) {
            throw new DatabaseException("There was a problem with the database: ", e);
        }
    }
    public int getIdByCardNumber(String cardNumber) {
        try (Connection con =  DB.connect();
             PreparedStatement myStmt = con.prepareStatement(FIND_BY_CARDNUMBER)) {

            myStmt.setString(1, cardNumber);
            try(ResultSet resultSet = myStmt.executeQuery()) {
                if (resultSet.next()) {
                    int customerId = resultSet.getInt(1);
                    myStmt.close();
                    return customerId;
                } else {
                    myStmt.close();
                    throw new CustomerNotFoundException("User not found!");
                }
            }
        }
        catch (SQLException e) {
            throw new DatabaseException("There was a problem with the database: ", e);
        }
    }
    public BigDecimal getBalanceById(int id) {
        try (Connection con =  DB.connect();
             PreparedStatement myStmt = con.prepareStatement(FIND_BALANCE_BY_ID)) {

            myStmt.setInt(1, id);

            try (ResultSet rs = myStmt.executeQuery()) {
                if (rs.next()) {
                    BigDecimal balance = rs.getBigDecimal("balance");
                    myStmt.close();
                    return balance;
                } else {
                    myStmt.close();
                    throw new DatabaseException("User not found! Couldn't fetch customer balance.");
                }
            }
            catch (SQLException e){
                myStmt.close();
                throw new DatabaseException("There was a problem trying to fetch the data from the database: ", e);
            }
        }
        catch(SQLException e){
            throw new DatabaseException("There was a problem connecting the database: ", e);
        }
    }

}
