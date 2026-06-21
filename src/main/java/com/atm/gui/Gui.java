package com.atm.gui;
import com.atm.service.AccountServiceImpl;
import com.atm.service.AuthServiceImpl;
import com.atm.service.CustomerAdminServiceImpl;
import com.atm.session.SessionManagerImpl;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.io.IOException;

public class Gui extends Application {

    @Override
    public void start(Stage loginPanel) throws IOException {
        SessionManagerImpl sessionManager = new SessionManagerImpl();
        AccountServiceImpl accountService = new AccountServiceImpl(sessionManager);
        CustomerAdminServiceImpl customerAdminService = new CustomerAdminServiceImpl();
        AuthServiceImpl authService = new AuthServiceImpl(sessionManager);


        Navigator navigator = new Navigator(loginPanel, authService, accountService, sessionManager);

        navigator.showLoginView();
    }

}
