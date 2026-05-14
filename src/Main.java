import com.atm.dao.AuthDAOImpl;
import com.atm.dao.CustomerDAOImpl;
import com.atm.domain.Customer;
import com.atm.dto.AuthRecord;
import com.atm.dto.CustomerCreateRequest;
import com.atm.dto.CustomerUpdateRequest;
import com.atm.gui.Gui;
import com.atm.service.AccountServiceImpl;
import com.atm.service.CustomerAdminServiceImpl;
import com.atm.session.SessionManagerImpl;
import javafx.application.Application;

import java.math.BigDecimal;


public class Main {
    public static void main(String[] args) throws InterruptedException {
//        SessionManagerImpl mockSession = new SessionManagerImpl();
//        CustomerDAOImpl dao = new CustomerDAOImpl();
//        AccountServiceImpl accService = new AccountServiceImpl(mockSession);
//        CustomerAdminServiceImpl customerAdminServiceImpl = new CustomerAdminServiceImpl();
//        AuthDAOImpl authdao = new AuthDAOImpl();
//        char[] pinCode = {'1', '2', '3', '4'};
//        CustomerCreateRequest newUser = new CustomerCreateRequest("Dumb Dumb", "1234567891011121", pinCode, "dumb@dumb.com", BigDecimal.valueOf(25000));
//        int newUserId = customerAdminServiceImpl.registerCustomer(newUser);
//        boolean isCorrect = CustomerAdminServiceImpl.validatePin("1989", "$2a$12$wTgfQSzMTdiNOHajLmT6XulrXDPo6BDoMPgm1mp8Dfh3urutD3Ixe");
//        System.out.println(isCorrect);

//        AuthRecord readUser = authdao.findCustomerByCardNumber(("31211101987654321"));
//        Customer user = dao.read(readUser.getCustomerId());
//        System.out.println(user.getEmail());
//        System.out.println(user.getName());
//        CustomerUpdateRequest updateUserRequest = new CustomerUpdateRequest(user.getId(), "Taki Sayuri", "2424242424242424", "1234".toCharArray(), "taki_sayuri@dummy.com");
//        customerAdminServiceImpl.updateCustomer(updateUserRequest);
//        AuthRecord readUser2 = authdao.findCustomerByCardNumber(("2424242424242424"));
//        Customer user2 = dao.read(readUser2.getCustomerId());
//        System.out.println(user2.getEmail());
//        System.out.println(user2.getName());

//        System.out.println(customerAdminServiceImpl.deleteCustomer(newUserId));

//        AuthRecord record = authdao.findCustomerById(3);

//        mockSession.login(3, Role.CUSTOMER);
//
//        System.out.println("ID: " + mockSession.getActiveSession().getCustomerId() + "\nRole: " + mockSession.getActiveSession().getRole() + "\nLogin time: " + mockSession.getActiveSession().getLoginTime() + "\nLast update: " + mockSession.getActiveSession().getLastUpdate() + "\nLogout time: " + mockSession.getActiveSession().getTimeout());
//        if (mockSession.getActiveSession().getTimeout().isBefore(LocalDateTime.now())) {
//            System.out.println("Session timed out!");
//        } else {
//            System.out.println("Not timed out");
//        }
//        Thread.sleep(3000);
//        mockSession.touch();
//        System.out.println("ID: " + mockSession.getActiveSession().getCustomerId() + "\nRole: " + mockSession.getActiveSession().getRole() + "\nLogin time: " + mockSession.getActiveSession().getLoginTime() + "\nLast update: " + mockSession.getActiveSession().getLastUpdate() + "\nLogout time: " + mockSession.getActiveSession().getTimeout());

        Application.launch(Gui.class, args);

    }
}