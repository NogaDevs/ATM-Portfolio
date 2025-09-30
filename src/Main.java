import com.atm.dao.CustomerDAO;
import com.atm.domain.Customer;
import com.atm.service.AccountService;

import com.atm.service.CustomerAdminService;

public class Main {
    public static void main(String[] args) {
        CustomerDAO dao = new CustomerDAO();
        AccountService accService = new AccountService(dao);
        Customer userid = dao.getCustomerById(1);

        String plainPin = "4124";
        String hashPin = CustomerAdminService.encodePin(plainPin);
        System.out.println(hashPin);
    }
}