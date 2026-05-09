package com.atm.gui.controller;

import static com.atm.util.NameUtils.safeDigitTrim;
import com.atm.exception.InvalidCredentialsException;
import com.atm.exception.RecordNotFoundException;
import com.atm.gui.Navigator;
import com.atm.service.AuthServiceImpl;
import com.atm.session.SessionManagerImpl;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.Objects;


public class LoginController {
    private AuthServiceImpl authService;
    private SessionManagerImpl sessionManager;
    private Navigator navigator;

    public void setServices(AuthServiceImpl authService, SessionManagerImpl sessionManager, Navigator navigator) {
        this.authService = Objects.requireNonNull(authService);
        this.sessionManager = Objects.requireNonNull(sessionManager);
        this.navigator = Objects.requireNonNull(navigator);
    }
    @FXML private TextField cardNumberField;
    @FXML private PasswordField pinCodeField;
    @FXML private Label errorLabel;
    @FXML private Button submitButton;

    @FXML
    public void initialize() {
        setError(null);
        configureCardNumberFormatter();
        configurePinCodeFormatter();
    }

    @FXML private void handleLogin() {
        showError(null);

        String cardNumber = cardNumberField.getText() == null ? "" : safeDigitTrim(cardNumberField.getText());
        char[] pinCode = pinCodeField.getText() == null ? "".toCharArray() : safeDigitTrim(pinCodeField.getText()).toCharArray();

        try {
            authService.loginByCard(cardNumber, pinCode);
            sessionManager.touch();
            navigator.showCustomerMainView();
        }
        catch (RecordNotFoundException e) {
            showError("Invalid card. Please contact your card company.");
            pinCodeField.clear();
            cardNumberField.clear();
            cardNumberField.requestFocus();
        }
        catch (InvalidCredentialsException e) {
            showError(e.getMessage());
            cardNumberField.clear();
            cardNumberField.requestFocus();
            pinCodeField.clear();
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

    private void configureCardNumberFormatter (){
        TextFormatter<String> formatter = new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            String onlyDigitText = newText.replaceAll("\\D", "");

            if (onlyDigitText.length() > 16) {
                onlyDigitText = onlyDigitText.substring(0,16);
            }

            StringBuilder formatted = new StringBuilder();
            for (int i = 0; i < onlyDigitText.length(); i++){
                if ( i > 0 && i % 4 == 0) {
                    formatted.append("-");
                }
                formatted.append(onlyDigitText.charAt(i));
            }

            change.setText(formatted.toString());
            change.setRange(0, change.getControlText().length());
            change.setCaretPosition(formatted.length());
            change.setAnchor(formatted.length());

            return change;
        });

        cardNumberField.setTextFormatter(formatter);
    }

    private void configurePinCodeFormatter(){
        TextFormatter<String> formatter = new TextFormatter<>(change -> {
           String newCode = change.getControlNewText();
           String onlyDigitText = newCode.replaceAll("\\D", "");

            if (onlyDigitText.length() > 4) {
                onlyDigitText = onlyDigitText.substring(0,4);
            }

            change.setText(onlyDigitText);
            change.setRange(0, change.getControlText().length());
            change.setAnchor(onlyDigitText.length());
            change.setCaretPosition(onlyDigitText.length());

            return change;
        });

        pinCodeField.setTextFormatter(formatter);
    }

}
