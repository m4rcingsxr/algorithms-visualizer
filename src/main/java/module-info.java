module com.marcinseweryn {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.logging.log4j;

    // Exports the package for other modules to use
    exports com.marcinseweryn.visualizer;

    // Opens the package to javafx.fxml for reflection
    opens com.marcinseweryn.visualizer to javafx.fxml;
    opens com.marcinseweryn.visualizer.view to javafx.fxml;
    exports com.marcinseweryn.visualizer.controller;
    opens com.marcinseweryn.visualizer.controller to javafx.fxml;
    exports com.marcinseweryn.visualizer.model.path;
    opens com.marcinseweryn.visualizer.model.path to javafx.fxml;
    exports com.marcinseweryn.visualizer.model;
    opens com.marcinseweryn.visualizer.model to javafx.fxml;
}
