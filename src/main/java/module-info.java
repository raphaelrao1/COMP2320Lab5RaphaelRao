module edu.westga.comp2320.studymate {
    requires javafx.controls;
    requires javafx.fxml;

    opens edu.westga.comp2320.studymate to javafx.fxml;
    exports edu.westga.comp2320.studymate;
    exports edu.westga.comp2320.studymate.controller;
    opens edu.westga.comp2320.studymate.controller to javafx.fxml;
}