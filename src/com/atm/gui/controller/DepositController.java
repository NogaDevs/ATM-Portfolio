package com.atm.gui.controller;

import com.atm.exception.InvalidAmountException;
import com.atm.gui.Navigator;
import com.atm.service.AccountServiceImpl;
import com.atm.session.SessionManagerImpl;
import javafx.animation.PauseTransition;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import com.atm.util.ControllerUtils;
import javafx.util.Duration;

import java.math.BigDecimal;
import java.util.Objects;

public class DepositController {
    private AccountServiceImpl accountService;
    private SessionManagerImpl sessionManager;
    private Navigator navigator;
    private final ObjectProperty<OperationState> state = new SimpleObjectProperty<>(OperationState.IDLE);
    private final PauseTransition clickLock = new PauseTransition(Duration.seconds(5));

    public void setServices(SessionManagerImpl sessionManager, Navigator navigator, AccountServiceImpl accountService) {
        this.accountService = Objects.requireNonNull(accountService);
        this.sessionManager = Objects.requireNonNull(sessionManager);
        this.navigator = Objects.requireNonNull(navigator);
    }

    @FXML private TextField inputField;
    @FXML private Label messageLabel;
    @FXML private Label errorLabel;
    @FXML private Button backButton;
    @FXML private Button logoutButton;
    @FXML private Button actionButton;

    @FXML
    public void initialize() {
        setError(null);
        ControllerUtils.configureInputFieldFormatter(inputField, state);

        actionButton.setOnAction(event -> {
            if (actionButton.isDisable()) return;

            setError(null);

            actionButton.setDisable(true);
            inputField.setDisable(true);
            actionButton.setText("Processing...");

            PauseTransition wait = new PauseTransition(Duration.seconds(2));
            wait.setOnFinished(e -> handleActionButton());
            wait.play();
            //TODO: Make this a function named beginTransactionFlow in utils and use it in deposit and withrdaw. Try to fix redo transaction when failing.
        });
    }

    @FXML
    private void handleActionButton(){
        ControllerUtils.refresh(sessionManager);

        String onlyDigitString = inputField.getText().replaceAll("\\D", "");
        if (onlyDigitString.isEmpty()) {
            state.set(OperationState.ERROR);
            showError("Invalid input. Please enter a valid amount.");
            return;
        }

        BigDecimal depositAmount = BigDecimal.valueOf(Long.parseLong(onlyDigitString) / 100.0);

        try {
            BigDecimal result = accountService.deposit(sessionManager.getActiveSession().getCustomerId(), depositAmount);
            messageLabel.setText("Transaction finished successfully.\nYour new balance is: " + result + "€");
            state.set(OperationState.SUCCESS);
        }
        catch (NumberFormatException e) {
            state.set(OperationState.ERROR);
            showError("Deposit field cannot be empty.");
        }
        catch (InvalidAmountException e) {
            state.set(OperationState.ERROR);
            showError("Invalid amount. Please enter a valid positive number.");
        }
        catch (Exception e) {
            state.set(OperationState.ERROR);
            showError("An unexpected error occurred. Please try again later.");
        }

        if (state.get() == OperationState.SUCCESS) {
            inputField.setVisible(false);
            actionButton.setText("Next transaction");
            actionButton.setOnAction(event -> handleBackButton());
            actionButton.setDisable(false);
        } else {
            inputField.setVisible(true);
            inputField.setDisable(false);
            actionButton.setDisable(false);
            actionButton.setText("Deposit");
            actionButton.setOnAction(event -> handleActionButton());
        }
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

    private void setError(String message) {
        boolean hasError = message != null && !message.isBlank();
        errorLabel.setText(hasError ? message : "");
        errorLabel.setVisible(hasError);
        errorLabel.setManaged(hasError);
    }

    private void showError(String msg){
        errorLabel.setText(msg == null ? "": msg);
        boolean hasError = msg != null && !msg.isBlank();
        errorLabel.setVisible(hasError);
        errorLabel.setManaged(hasError);
    }
}
