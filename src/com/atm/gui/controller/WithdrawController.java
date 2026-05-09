package com.atm.gui.controller;

import com.atm.exception.InvalidAmountException;
import com.atm.gui.Navigator;
import com.atm.service.AccountServiceImpl;
import com.atm.service.AuthServiceImpl;
import com.atm.session.SessionManagerImpl;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import jdk.dynalink.Operation;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

public class WithdrawController {
    // TODO: Do we need auth?
    private AuthServiceImpl authService;
    private AccountServiceImpl accountService;
    private SessionManagerImpl sessionManager;
    private Navigator navigator;
    private OperationState state;

    public void setServices(AuthServiceImpl authService, SessionManagerImpl sessionManager, Navigator navigator, AccountServiceImpl accountService) {
        this.authService = Objects.requireNonNull(authService);
        this.accountService = Objects.requireNonNull(accountService);
        this.sessionManager = Objects.requireNonNull(sessionManager);
        this.navigator = Objects.requireNonNull(navigator);
        this.state = OperationState.IDLE;

    }
    @FXML
    private TextField inputField;
    @FXML private Label messageLabel;
    @FXML private Label errorLabel;
    @FXML private Button backButton;
    @FXML private Button logoutButton;
    //TODO: actionButton. Hacer la funcion para withdraw
    @FXML private Button actionButton;

    @FXML
    public void initialize() {
        setError(null);
        configureInputField();
    }

    private void configureInputField (){
        TextFormatter<String> formatter = new TextFormatter<>(change -> {
            String onlyDigitString = change.getControlNewText().replaceAll("\\D", "");

            if (onlyDigitString.length() > 16) {
                onlyDigitString = onlyDigitString.substring(0,16);
            }

            if (!onlyDigitString.isEmpty()) {

                // Fix for backspace. Forces another backspace because of Euro character.
                double numericInput;
                if (!change.isDeleted()) {
                    numericInput = Long.parseLong(onlyDigitString) / 100.0;
                }
                else {
                    numericInput = Long.parseLong(onlyDigitString.substring(0, onlyDigitString.length() - 1)) / 100.0;
                }

                NumberFormat localeFormatter = NumberFormat.getCurrencyInstance(Locale.GERMANY);
                String formatted = localeFormatter.format(numericInput);
                change.setText(formatted);
                change.setRange(0, change.getControlText().length());
                change.setCaretPosition(formatted.length());
                change.setAnchor(formatted.length());
            }
            else {
                change.setText("");
                change.setRange(0, change.getControlText().length());
            }

            return change;
        });
        this.state = OperationState.INPUT;
        inputField.setTextFormatter(formatter);
    }

    private void configureActionButton(){
        this.state = OperationState.PROCESSING;

        String onlyDigitString = inputField.getText().replaceAll("\\D", "");
        BigDecimal withdrawAmount = BigDecimal.valueOf(Long.parseLong(onlyDigitString) / 100.0);

        if (withdrawAmount.longValue() <= 0){
            showError("Amount can't be 0 or less than 0.");
        }
        else {
            sessionManager.requireActive();
            sessionManager.touch();
            accountService.withdraw(sessionManager.getActiveSession().getCustomerId(), withdrawAmount);
            //TODO: Continuar con esta funcion. Aun esta sin probar.
        }
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
