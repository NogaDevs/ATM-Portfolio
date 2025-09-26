import java.math.BigDecimal;

public class Main {
    public static void main(String[] args) {
        CustomerDAO dao = new CustomerDAO();
        AccountService accService = new AccountService(dao);
        int userid = dao.findByCardNumber("2424-2424-2424-2424");

        BigDecimal amount = BigDecimal.valueOf(20000);
        BigDecimal newBalance;
        newBalance = accService.withdraw(userid, amount);
        System.out.println(newBalance);
    }
}