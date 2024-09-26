module com.marcinseweryn {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.logging.log4j;

    // Exports the package for other modules to use
    exports com.marcinseweryn.visualizer;

    // Opens the package to javafx.fxml for reflection
    opens com.marcinseweryn.visualizer to javafx.fxml;
}
