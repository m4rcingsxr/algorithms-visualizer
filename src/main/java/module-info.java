module com.marcinseweryn {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.logging.log4j;

    exports com.marcinseweryn.visualizer;
    exports com.marcinseweryn.visualizer.controller;
    exports com.marcinseweryn.visualizer.model;
    exports com.marcinseweryn.visualizer.model.path;
    exports com.marcinseweryn.visualizer.view;

    opens com.marcinseweryn.visualizer to javafx.fxml;
    opens com.marcinseweryn.visualizer.controller to javafx.fxml;
    opens com.marcinseweryn.visualizer.model to javafx.fxml;
    opens com.marcinseweryn.visualizer.model.path to javafx.fxml;
    opens com.marcinseweryn.visualizer.view to javafx.fxml;
}
