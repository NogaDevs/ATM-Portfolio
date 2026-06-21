package com.atm.gui.controller;

import com.atm.gui.Navigator;
import com.atm.session.SessionManagerImpl;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.util.Objects;

public class logoutTransitionController {
    private SessionManagerImpl sessionManager;
    private Navigator navigator;


    public void setServices(SessionManagerImpl sessionManager,
                            Navigator navigator) {
        this.sessionManager = Objects.requireNonNull(sessionManager);
        this.navigator = Objects.requireNonNull(navigator);

        handleLogout();
    }

    @FXML private Label messageLabel;

    @FXML
    public void initialize() {
    }

    private void handleLogout() {
        Timeline logoutTime = new Timeline(new KeyFrame(
                Duration.seconds(3),
                event -> sessionManager.logout()
        ));
        logoutTime.play();
        logoutTime.setOnFinished(event -> navigator.showLoginView());
    }
}


