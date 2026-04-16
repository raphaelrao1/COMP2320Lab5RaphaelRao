module edu.westga.comp2320.studymate {
    requires javafx.controls;
    requires javafx.fxml;

    opens edu.westga.comp2320.studymate to javafx.fxml;
    exports edu.westga.comp2320.studymate;
}