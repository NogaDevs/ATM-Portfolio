package com.atm.util;

import com.atm.gui.controller.OperationState;
import com.atm.session.SessionManagerImpl;
import javafx.beans.property.ObjectProperty;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

import java.text.NumberFormat;
import java.util.Locale;

public class ControllerUtils {

    private ControllerUtils(){}

    public static void configureInputFieldFormatter(TextField inputField, ObjectProperty<OperationState> state){
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
        state.set(OperationState.INPUT);
        inputField.setTextFormatter(formatter);
    }

    public static void refresh(SessionManagerImpl sessionManager) {
        sessionManager.touch();
        sessionManager.requireActive();
    }
}
