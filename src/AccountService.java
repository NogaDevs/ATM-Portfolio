import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import exception.*;


public class AccountService {
    private final CustomerDAO customerDAO;

    public AccountService(CustomerDAO customerDAO) {
        this.customerDAO = customerDAO;
    }

    public BigDecimal deposit(int customerId, BigDecimal amount) {
        BigDecimal newBalance;
        try(Connection con = DB.connect()) {
            try {
                con.setAutoCommit(false);
                if (amount == null) {
                    throw new InvalidAmountException("Invalid input.");
                } else if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new InvalidAmountException("You can't deposit zero or less dollars.");
                } else if (customerId <= 0) {
                    throw new CustomerNotFoundException("Invalid user ID.");
                } else {
                newBalance = customerDAO.adjustBalanceReturningBalance(customerId, amount, con);
                con.commit();
                return newBalance;
                }
            }
            catch (SQLException e) {
                try {con.rollback();
                    throw new DatabaseException("Deposit failed: ");}
                catch (SQLException rb) {
                    throw new DatabaseException("Deposit failed: ", rb);
                }
            }
        }
        catch (SQLException e) {
            throw new DatabaseException("Connection to the database failed: ", e);
        }
    }
    public BigDecimal withdraw(int customerId, BigDecimal amount) {
        BigDecimal newBalance = null;
        try(Connection con = DB.connect()) {
            try {
                con.setAutoCommit(false);
                if (amount == null) {
                    throw new InvalidAmountException("Invalid input.");
                } else if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new InsufficientFundsException("You can't withdraw zero or less dollars.");
                } else if (customerId <= 0) {
                    throw new CustomerNotFoundException("Invalid user ID.");
                } else {
                    newBalance = customerDAO.adjustBalanceReturningBalance(customerId, amount.negate(), con);
                    con.commit();
                    return newBalance;
                }
            }
            catch (SQLException e) {
                try {con.rollback();
                    throw new DatabaseException("withdraw failed: ");
                }
                catch (SQLException rb) {
                    throw new DatabaseException("Rollback failed: ", rb);
                }
            }
        }
        catch (SQLException e){
            throw new DatabaseException("Connection to the database failed: ", e);
        }
    }
}

