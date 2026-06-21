package com.atm.gui.controller;

import com.atm.gui.Navigator;
import com.atm.service.AccountServiceImpl;
import com.atm.service.AuthServiceImpl;
import com.atm.session.SessionManagerImpl;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class CustomerMainController {
    private AuthServiceImpl authService;
    private AccountServiceImpl accountService;
    private SessionManagerImpl sessionManager;
    private Navigator navigator;


    @FXML private Label welcomeLabel;
    @FXML private Button logoutButton;
    @FXML private Button withdrawButton;
    @FXML private Button depositButton;
    @FXML private Button balanceButton;

    public void setServices(AuthServiceImpl authService,
                            AccountServiceImpl accountService,
                            SessionManagerImpl sessionManager,
                            Navigator navigator) {

        this.authService = authService;
        this.accountService = accountService;
        this.sessionManager = sessionManager;
        this.navigator = navigator;

        // After dependencies are ready, refresh UI
        refresh();
    }

    @FXML
    public void initialize() {
        // Initialize UI-only defaults here
    }

    @FXML private void handleLogoutButton(){
        navigator.showLogoutTransactionView();
    }

    @FXML
    private void handleWithdraw() {
        sessionManager.requireActive();
        sessionManager.touch();
        navigator.showWithdrawView();
    }

    @FXML
    private void handleDeposit() {
        sessionManager.requireActive();
        sessionManager.touch();
        navigator.showDepositView();
    }

    @FXML
    private void handleBalance() {
        sessionManager.requireActive();
        sessionManager.touch();
        navigator.showBalanceView();
    }

    private void refresh() {
        sessionManager.requireActive();
        sessionManager.touch();
        String customerName = sessionManager.getActiveSession().getCustomerName();

        if (welcomeLabel != null) {
            welcomeLabel.setText("Welcome, " + customerName + ".");
        }
    }
}
