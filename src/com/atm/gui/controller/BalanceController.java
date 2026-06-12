package com.atm.gui.controller;

import com.atm.gui.Navigator;
import com.atm.service.AccountServiceImpl;
import com.atm.session.SessionManagerImpl;
import com.atm.util.ControllerUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.math.BigDecimal;
import java.util.Objects;

public class BalanceController {
    private AccountServiceImpl accountService;
    private SessionManagerImpl sessionManager;
    private Navigator navigator;


    public void setServices(SessionManagerImpl sessionManager,
                            Navigator navigator,
                            AccountServiceImpl accountService) {
        this.accountService = Objects.requireNonNull(accountService);
        this.sessionManager = Objects.requireNonNull(sessionManager);
        this.navigator = Objects.requireNonNull(navigator);

        showBalance();
    }

    @FXML private Label balanceLabel;
    @FXML private Button backButton;
    @FXML private Button logoutButton;
    @FXML private Button depositButton;
    @FXML private Button withdrawButton;

    @FXML
    public void initialize() {
    }

    private void showBalance() {
        BigDecimal balance = new BigDecimal(String.valueOf(accountService.balance(sessionManager.getActiveSession().getCustomerId())));
        balanceLabel.setText(balance + "€");
    }

    @FXML
    private void handleBackButton(){
        ControllerUtils.refresh(sessionManager);
        navigator.showCustomerMainView();
    }

    @FXML
    private void handleWithdraw(){
        ControllerUtils.refresh(sessionManager);
        navigator.showWithdrawView();
    }

    @FXML
    private void handleDeposit(){
        ControllerUtils.refresh(sessionManager);
        navigator.showDepositView();
    }

    @FXML
    private void handleLogoutButton(){
        navigator.showLogoutTransactionView();
    }

}
