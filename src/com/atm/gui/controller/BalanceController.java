package com.atm.gui.controller;

import com.atm.exception.InvalidAmountException;
import com.atm.gui.Navigator;
import com.atm.service.AccountServiceImpl;
import com.atm.session.SessionManagerImpl;
import com.atm.util.ControllerUtils;
import javafx.animation.PauseTransition;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.Duration;

import java.math.BigDecimal;
import java.util.Objects;

public class BalanceController {
    private AccountServiceImpl accountService;
    private SessionManagerImpl sessionManager;
    private Navigator navigator;

    public void setServices(SessionManagerImpl sessionManager, Navigator navigator, AccountServiceImpl accountService) {
        this.accountService = Objects.requireNonNull(accountService);
        this.sessionManager = Objects.requireNonNull(sessionManager);
        this.navigator = Objects.requireNonNull(navigator);
    }

    @FXML private Label balanceLabel;
    @FXML private Button backButton;
    @FXML private Button logoutButton;

    @FXML
    public void initialize() {
        accountService.
    }

    @FXML
    private void handleBackButton(){
        ControllerUtils.refresh(sessionManager);
        navigator.showCustomerMainView();
    }

    @FXML
    private void handleLogoutButton(){
        sessionManager.logout();
        navigator.showLoginView();
    }

}
