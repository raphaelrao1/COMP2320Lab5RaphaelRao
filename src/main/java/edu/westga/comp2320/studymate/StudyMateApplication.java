package edu.westga.comp2320.studymate;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class StudyMateApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(StudyMateApplication.class.getResource("studymate-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 350);
        stage.setTitle("Study Organizer by Raphael Rao");
        stage.setScene(scene);
        stage.show();
    }
}
