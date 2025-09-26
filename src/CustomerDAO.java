import java.math.BigDecimal;
import java.sql.*;
import exception.*;



public class CustomerDAO {

    public int findByCardNumber(String cardNumber) {
        try (Connection con =  DB.connect()){

            // SELECT query
            PreparedStatement myStmt;
            myStmt = con.prepareStatement("SELECT * FROM customers WHERE card_number = ?");
            myStmt.setString(1, cardNumber);
            try(ResultSet resultSet = myStmt.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                } else {
                    throw new CustomerNotFoundException("User not found!");
                }
            }
        }
        catch (SQLException e) {
            throw new CustomerNotFoundException("User not found: ", e);
        }
    }

    public Customer getCustomerById(int id) {
        try (Connection con =  DB.connect()){

            // SELECT query
            PreparedStatement myStmt;
            myStmt = con.prepareStatement("SELECT * FROM customers WHERE costumer_id = ?");
            myStmt.setInt(1, id);
            try (ResultSet resultSet = myStmt.executeQuery()) {
                if (resultSet.next()) {
                    String name = resultSet.getString(2);
                    String cardNumber = resultSet.getString(3);
                    String pin = resultSet.getString(4);
                    String email = resultSet.getString(5);
                    BigDecimal balance = resultSet.getBigDecimal(6);

                    return new Customer(id, name, cardNumber, pin, email, balance);
                } else {
                    throw new CustomerNotFoundException("No such user id is already registered");
                }
            }
        }
        catch (SQLException e) {
            throw new DatabaseException("Database error:", e);
        }
    }

    public BigDecimal adjustBalanceReturningBalance(int id, BigDecimal delta, Connection con) throws DatabaseException {
        // UPDATE balance depending on withdraw/deposit. As it's validated on Account Service, no need for further check aside from - or + amount.
        String query;
        BigDecimal amount = delta.abs();
        boolean isDeposit = delta.signum() > 0;

        if(isDeposit){ // Deposit
            query = "UPDATE customers SET balance = balance + ? WHERE customer_id = ? RETURNING balance";
        } else { // Withdraw
            query = "UPDATE customers SET balance = balance + ? WHERE customer_id = ? AND balance >= ? RETURNING balance";
        }

        try(PreparedStatement myStmtUpdate = con.prepareStatement(query)) {
            myStmtUpdate.setBigDecimal(1, delta);
            myStmtUpdate.setInt(2, id);
            if(!isDeposit) {
                myStmtUpdate.setBigDecimal(3, amount);
            }
            try (ResultSet result = myStmtUpdate.executeQuery()) {
                // If no row fetched, will proceed to check if It's because ID not found or not enough funds.
                if (!result.next()) {
                    String idQuery = "SELECT 1 FROM customers WHERE customer_id = ?";
                    try (PreparedStatement myStmtId = con.prepareStatement(idQuery)){
                        myStmtId.setInt(1, id);
                        try (ResultSet resultSetForId = myStmtId.executeQuery()) {
                            if (!resultSetForId.next()) {
                                throw new CustomerNotFoundException("Error! Customer not found.");
                            } else {
                                throw new InvalidAmountException("There are not enough funds.");
                            }
                        }
                    }
                } else {
                    return result.getBigDecimal("balance");
                }
            }
            catch (SQLTimeoutException e){
                    throw new DatabaseException("Timeout Error! There was a technical problem with the database.", e);
            }
        } catch (SQLException e) {
            throw new DatabaseException("There was a problem with the database.", e);
        }
    }

    public BigDecimal getBalanceById(int id) {
        try (Connection con =  DB.connect()) {

            // QUERY balance
            PreparedStatement myStmtBalance;
            myStmtBalance = con.prepareStatement("SELECT * FROM customers WHERE customer_id = ?");
            myStmtBalance.setInt(1, id);
            try (ResultSet resultBalance = myStmtBalance.executeQuery()) {
                if (resultBalance.next()) {
                    return resultBalance.getBigDecimal("balance");
                } else {
                    throw new DatabaseException("User not found! Couldn't fetch customer balance.");
                }
            }
            catch (SQLException e){
                throw new DatabaseException("There was a problem trying to fetch the data from the database: ", e);
            }
        }
        catch(SQLException e){
            throw new DatabaseException("There was a problem connecting the database: ", e);
        }
    }
}
