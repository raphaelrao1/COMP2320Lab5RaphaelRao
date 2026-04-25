package edu.westga.comp2320.studymate.controller;

import edu.westga.comp2320.studymate.model.StudySession;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.List;

public class StudyMateController {
    @FXML private ToggleGroup dayToggleGroup;
    @FXML private RadioButton mondayRadio;
    @FXML private RadioButton tuesdayRadio;
    @FXML private RadioButton wednesdayRadio;
    @FXML private RadioButton thursdayRadio;
    @FXML private RadioButton fridayRadio;

    @FXML private CheckBox englCheckBox;
    @FXML private CheckBox histCheckBox;
    @FXML private CheckBox mathCheckBox;
    @FXML private CheckBox compCheckBox;

    @FXML private Label subjectErrorLabel;
    @FXML private TextField taskTextField;
    @FXML private ListView<StudySession> sessionListView;

    private ObservableList<StudySession> sessions;
    private StudySession currentlySelected;

    private List<RadioButton> dayRadioButtons;
    private List<CheckBox> subjectCheckBoxes;


    @FXML
    private void initialize() {
        this.sessions = FXCollections.observableArrayList();
        this.sessionListView.setItems(this.sessions);
        this.subjectErrorLabel.setText("");

        this.dayRadioButtons = List.of(
                this.mondayRadio,
                this.tuesdayRadio,
                this.wednesdayRadio,
                this.thursdayRadio,
                this.fridayRadio
        );

        this.subjectCheckBoxes = List.of(
                this.englCheckBox,
                this.histCheckBox,
                this.mathCheckBox,
                this.compCheckBox
        );

        this.mondayRadio.setSelected(true);
        Platform.runLater(() -> this.mondayRadio.requestFocus());

        this.sessionListView.getSelectionModel().selectedItemProperty().addListener(
                (_, _, newValue) -> {
                    this.currentlySelected = newValue;
                    if (newValue != null) {
                        this.selectDayRadio(newValue.dayOfWeek());
                        this.selectSubjectCheckBox(newValue.subject());
                        this.taskTextField.setText(newValue.task() == null ? "" : newValue.task());
                        this.subjectErrorLabel.setText("");
                    } else {
                        this.clearSubjectCheckBoxes();
                    }
                }
        );
    }

    private void selectDayRadio(char day) {
        for (RadioButton radio : this.dayRadioButtons) {
            if (radio.getText().charAt(0) == day) {
                radio.setSelected(true);
                return;
            }
        }
    }


    private void selectSubjectCheckBox(String subject) {
        for (CheckBox box : this.subjectCheckBoxes) {
            box.setSelected(box.getText().equals(subject));
        }
    }

    private void clearSubjectCheckBoxes() {
        for (CheckBox box : this.subjectCheckBoxes) {
            box.setSelected(false);
        }
    }

    @FXML
    private void handleAddButton() {
        this.subjectErrorLabel.setText("");

        // A radio button is always selected, so this cast is always safe.
        RadioButton selectedRadio = (RadioButton) this.dayToggleGroup.getSelectedToggle();
        char day = selectedRadio.getText().charAt(0);

        // Collect labels of all checked subject boxes.
        List<String> selectedSubjects = new ArrayList<>();
        for (CheckBox box : this.subjectCheckBoxes) {
            if (box.isSelected()) {
                selectedSubjects.add(box.getText());
            }
        }

        // Validation: at least one subject must be checked.
        if (selectedSubjects.isEmpty()) {
            this.subjectErrorLabel.setText("select at least one subject");
            return;
        }

        String taskInput = this.taskTextField.getText();
        String task = (taskInput == null || taskInput.trim().isEmpty()) ? null : taskInput.trim();

        // Add one StudySession per checked subject; all share the same day and task.
        StudySession lastAdded = null;
        for (String subject : selectedSubjects) {
            StudySession newSession = (task == null)
                    ? new StudySession(day, subject)
                    : new StudySession(day, subject, task);
            this.sessions.add(newSession);
            lastAdded = newSession;
        }

        // Spec: after adding, one of the newly added sessions is selected.
        this.sessionListView.getSelectionModel().select(lastAdded);
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
        this.taskTextField.setText("");
        this.clearSubjectCheckBoxes();
        this.subjectErrorLabel.setText("");
        this.mondayRadio.setSelected(true);
    }
}
