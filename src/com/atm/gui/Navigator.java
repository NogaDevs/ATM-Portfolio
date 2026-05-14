package com.atm.gui;

import com.atm.gui.controller.CustomerMainController;
import com.atm.gui.controller.DepositController;
import com.atm.gui.controller.LoginController;
import com.atm.gui.controller.WithdrawController;
import com.atm.service.AccountServiceImpl;
import com.atm.service.AuthServiceImpl;
import com.atm.session.SessionManagerImpl;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.util.Objects;

public class Navigator {
    private final Stage stage;
    private final Scene scene;
    private final AuthServiceImpl authService;
    private final AccountServiceImpl accountService;
    private final SessionManagerImpl sessionManager;

    public Navigator(Stage stage,
                     AuthServiceImpl authService,
                     AccountServiceImpl accountService,
                     SessionManagerImpl sessionManager){
        this.stage = Objects.requireNonNull(stage);
        this.scene = new Scene(new javafx.scene.layout.StackPane());
        this.stage.setScene(scene);

        this.authService = Objects.requireNonNull(authService);
        this.accountService = Objects.requireNonNull(accountService);
        this.sessionManager = Objects.requireNonNull(sessionManager);
    }

    public void showLoginView() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/atm/gui/view/loginView.fxml"));
        try {
            Parent root = loader.load();
            scene.setRoot(root);

            LoginController controller = loader.getController();
            controller.setServices(authService, sessionManager, this);

            stage.show();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load loginView.fxml", e);
        }
    }

    public void showCustomerMainView() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/atm/gui/view/customerMainView.fxml"));
        try {
            Parent root = loader.load();
            scene.setRoot(root);

            CustomerMainController controller = loader.getController();
            controller.setServices(authService,accountService, sessionManager, this);

            stage.show();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load customerMainView.fxml", e);
        }
    }

    public void showWithdrawView() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/atm/gui/view/withdrawView.fxml"));
        try {
            Parent root = loader.load();
            scene.setRoot(root);

            WithdrawController controller = loader.getController();
            controller.setServices(sessionManager, this, accountService);

            stage.show();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load withdrawView.fxml", e);
        }
    }

    public void showDepositView() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/atm/gui/view/depositView.fxml"));
        try {
            Parent root = loader.load();
            scene.setRoot(root);

            DepositController controller = loader.getController();
            controller.setServices(sessionManager, this, accountService);

            stage.show();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load depositView.fxml", e);
        }
    }

}
