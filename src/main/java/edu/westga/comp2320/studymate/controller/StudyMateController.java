package edu.westga.comp2320.studymate.controller;

import edu.westga.comp2320.studymate.model.StudySession;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class StudyMateController {
    @FXML private TextField dayTextField;
    @FXML private Label dayErrorLabel;
    @FXML private TextField subjectTextField;
    @FXML private Label subjectErrorLabel;
    @FXML private TextField taskTextField;
    @FXML private ListView<StudySession> sessionListView;

    private ObservableList<StudySession> sessions;
    private StudySession currentlySelected;


    @FXML
    private void initialize() {
        this.sessions = FXCollections.observableArrayList();
        this.sessionListView.setItems(this.sessions);
        this.dayErrorLabel.setText("");
        this.subjectErrorLabel.setText("");
        this.sessionListView.getSelectionModel().selectedItemProperty().addListener(
                (_, _, newValue) -> {
                    this.currentlySelected = newValue;
                    if (newValue != null) {
                        this.dayTextField.setText(String.valueOf(newValue.dayOfWeek()));
                        this.subjectTextField.setText(newValue.subject());
                        this.taskTextField.setText(newValue.task() == null ? "" : newValue.task());
                        this.dayErrorLabel.setText("");
                        this.subjectErrorLabel.setText("");
                    }
                }
        );
        this.dayTextField.textProperty().addListener(new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue == null || newValue.isEmpty()) {
                    StudyMateController.this.dayErrorLabel.setText("");
                } else if (StudyMateController.this.isValidDay(newValue)) {
                    StudyMateController.this.dayErrorLabel.setText("");
                } else {
                    StudyMateController.this.dayErrorLabel.setText("must be M, T, W, R, or F");
                }
            }
        });    }
    @FXML
    private void handleAddButton() {
        this.dayErrorLabel.setText("");
        this.subjectErrorLabel.setText("");

        String dayInput = this.dayTextField.getText();
        String subjectInput = this.subjectTextField.getText();
        String taskInput = this.taskTextField.getText();

        boolean hasError = false;

        if (!this.isValidDay(dayInput)) {
            this.dayErrorLabel.setText("must be M, T, W, R, or F");
            hasError = true;
        }

        if (subjectInput == null || subjectInput.trim().isEmpty()) {
            this.subjectErrorLabel.setText("required");
            hasError = true;
        }

        if (hasError) {
            return;
        }

        char day = dayInput.trim().charAt(0);
        StudySession newSession;
        if (taskInput == null || taskInput.trim().isEmpty()) {
            newSession = new StudySession(day, subjectInput);
        } else {
            newSession = new StudySession(day, subjectInput, taskInput);
        }

        this.sessions.add(newSession);
        this.sessionListView.getSelectionModel().select(newSession);
    }

    private boolean isValidDay(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }
        String trimmed = input.trim();
        if (trimmed.length() != 1) {
            return false;
        }
        char day = Character.toUpperCase(trimmed.charAt(0));
        return day == 'M' || day == 'T' || day == 'W' || day == 'R' || day == 'F';
    }

    @FXML
    private void handleDeleteButton() {
        if (this.currentlySelected == null) {
            Alert warning = new Alert(Alert.AlertType.WARNING);
            warning.setTitle("No Selection");
            warning.setHeaderText(null);
            warning.setContentText("Please Select a Study Session to Delete.");
            warning.showAndWait();
            return;
        }

        this.sessions.remove(this.currentlySelected);
        this.dayTextField.setText("");
        this.subjectTextField.setText("");
        this.taskTextField.setText("");
        this.dayErrorLabel.setText("");
        this.subjectErrorLabel.setText("");
    }
}
