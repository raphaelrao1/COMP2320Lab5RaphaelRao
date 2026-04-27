package edu.westga.comp2320.studymate.controller;

import edu.westga.comp2320.studymate.model.StudySession;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

        RadioButton selectedRadio = (RadioButton) this.dayToggleGroup.getSelectedToggle();
        char day = selectedRadio.getText().charAt(0);

        List<String> selectedSubjects = new ArrayList<>();
        for (CheckBox box : this.subjectCheckBoxes) {
            if (box.isSelected()) {
                selectedSubjects.add(box.getText());
            }
        }

        if (selectedSubjects.isEmpty()) {
            this.subjectErrorLabel.setText("select at least one subject");
            return;
        }

        String taskInput = this.taskTextField.getText();
        String task = (taskInput == null || taskInput.trim().isEmpty()) ? null : taskInput.trim();

        StudySession lastAdded = null;
        for (String subject : selectedSubjects) {
            StudySession newSession = (task == null)
                    ? new StudySession(day, subject)
                    : new StudySession(day, subject, task);
            this.sessions.add(newSession);
            lastAdded = newSession;
        }

        this.sessionListView.getSelectionModel().select(lastAdded);
    }

    @FXML
    private void handleSave() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Study Sessions");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        fileChooser.setInitialFileName("study_sessions.txt");

        Stage stage = (Stage) this.sessionListView.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);
        if (file == null) {
            return;
        }

        this.writeSessionsToFile(file);
    }


    private void writeSessionsToFile(File file) {
        Map<Character, List<StudySession>> byDay = new LinkedHashMap<>();
        byDay.put('M', new ArrayList<>());
        byDay.put('T', new ArrayList<>());
        byDay.put('W', new ArrayList<>());
        byDay.put('R', new ArrayList<>());
        byDay.put('F', new ArrayList<>());

        for (StudySession session : this.sessions) {
            byDay.get(session.dayOfWeek()).add(session);
        }

        try (PrintWriter writer = new PrintWriter(file)) {
            boolean firstBlock = true;
            for (Map.Entry<Character, List<StudySession>> entry : byDay.entrySet()) {
                List<StudySession> daySessions = entry.getValue();
                if (daySessions.isEmpty()) {
                    continue;
                }
                if (!firstBlock) {
                    writer.println();
                }
                writer.println(dayName(entry.getKey()));
                for (StudySession session : daySessions) {
                    if (session.task() == null) {
                        writer.println(session.subject());
                    } else {
                        writer.println(session.subject() + " - " + session.task());
                    }
                }
                firstBlock = false;
            }
        } catch (IOException ex) {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Save Failed");
            error.setHeaderText(null);
            error.setContentText("Could not save file: " + ex.getMessage());
            error.showAndWait();
        }
    }

    private static String dayName(char day) {
        return switch (day) {
            case 'M' -> "Monday";
            case 'T' -> "Tuesday";
            case 'W' -> "Wednesday";
            case 'R' -> "Thursday";
            case 'F' -> "Friday";
            default -> "";
        };
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
