import com.atm.dao.CustomerDAO;
import com.atm.domain.Customer;
import com.atm.dto.CustomerCreateRequest;
import com.atm.dto.CustomerUpdateRequest;
import com.atm.service.AccountService;
import com.atm.service.CustomerAdminService;

import java.math.BigDecimal;


public class Main {
    public static void main(String[] args) throws InterruptedException {
        CustomerDAO dao = new CustomerDAO();
        AccountService accService = new AccountService();
        CustomerAdminService customerAdminService = new CustomerAdminService();

        CustomerCreateRequest newUser = new CustomerCreateRequest("DummY tHe Dumbest", "12345678910111213", "1989", "dumb@dummy.com", BigDecimal.valueOf(25000));
        int newUserId = customerAdminService.registerCustomer(newUser);
        boolean isCorrect = CustomerAdminService.validatePin("1989", "$2a$12$wTgfQSzMTdiNOHajLmT6XulrXDPo6BDoMPgm1mp8Dfh3urutD3Ixe");
        System.out.println(isCorrect);

//        Customer readUser = dao.read(dao.getIdByCardNumber("12345678910111213"));
//        System.out.println(readUser.getName());
//        System.out.println(readUser.getEmail());
//        CustomerUpdateRequest updateUserRequest = new CustomerUpdateRequest(readUser.getId(), "Dumby the Dumbest", "31211101987654321", "1999", "dumb@dummy.com");
//        customerAdminService.updateCustomer(updateUserRequest);

        System.out.println(customerAdminService.deleteCustomer(newUserId));
    }
}