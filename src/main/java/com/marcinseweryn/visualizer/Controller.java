package com.marcinseweryn.visualizer;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TabPane;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Controller {

    private static final Logger logger = LogManager.getLogger(Controller.class);

    // ===========================================
    // ================== GRAPH ==================
    // ===========================================
    @FXML TabPane algorithmTab;
    @FXML TabPane graphTab;

    // ===========================================
    // ================== COMMON =================
    // ===========================================
    @FXML ChoiceBox<String> algorithmList;

    // ===========================================
    // ============= ALGORITHM SPACE =============
    // ===========================================
    @FXML AnchorPane graphPane;
    @FXML AnchorPane arrayPane;

    @FXML VBox centerVBox;

    private PathFindingController findController;
    private SortingController sortController;

    @FXML
    public void initialize() {
        logger.info("Initializing Controller...");

        // Initialize controllers for path finding and sorting
        this.findController = new PathFindingController(this.graphPane);
        this.sortController = new SortingController(this.arrayPane);

        // Log once for both controllers initialization (if needed)
        logger.debug("PathFindingController and SortingController initialized.");

        arrayPane.setVisible(true);
        graphPane.setVisible(true);
        centerVBox.getChildren().remove(arrayPane);
        logger.debug("Graph space view selected.");

        // Show correct algorithm space based on selected tab
        algorithmTab.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            String tabName = newTab.getText();
            logger.info("{} tab selected.", tabName);

            // Manage panes visibility based on the tab selected
            if (tabName.equals("Path Finding")) {
                centerVBox.getChildren().remove(arrayPane);
                if (!centerVBox.getChildren().contains(graphPane)) {
                    centerVBox.getChildren().add(graphPane);
                }
            } else if (tabName.equals("Sorting")) {
                centerVBox.getChildren().remove(graphPane);
                if (!centerVBox.getChildren().contains(arrayPane)) {
                    centerVBox.getChildren().add(arrayPane);
                }
            } else {
                logger.warn("Unknown tab selected: {}", tabName);
            }
        });

        logger.info("Controller initialization completed.");
    }


    @FXML
    private void onGraphPaneMousePressed(MouseEvent mouseEvent) {
        this.findController.onGraphMousePressed(mouseEvent);
    }

    @FXML
    private void onGraphPaneDragDetected(MouseEvent mouseEvent) {
        this.findController.onGraphPaneDragDetected(mouseEvent);
    }

    @FXML
    private void onGraphPaneMouseDragged(MouseEvent mouseEvent) {
        this.findController.onGraphPaneMouseDragged(mouseEvent);
    }

    @FXML
    private void onGraphPaneMouseReleased(MouseEvent mouseEvent) {
        this.findController.onGraphPaneMouseReleased(mouseEvent);
    }

    @FXML
    private void onGraphPaneDragOver(DragEvent dragEvent) {
        this.findController.onGraphPaneDragOver(dragEvent);
    }

    @FXML
    private void onGraphPaneDragDropped(DragEvent dragEvent) {
        this.findController.onGraphPaneDragDropped(dragEvent);
    }
}
