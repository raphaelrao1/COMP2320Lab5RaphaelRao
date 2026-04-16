package edu.westga.comp2320.studymate.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class StudyMateController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}
