package com.marcinseweryn.visualizer;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TabPane;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

public class Controller {

    @FXML public TabPane algorithmTab;
    @FXML public TabPane graphTab;
    @FXML public ChoiceBox<String> algorithmList;
    @FXML public AnchorPane algorithmSpace;

    public void onAlgorithmSpaceMousePressed(MouseEvent mouseEvent) {
    }

    public void onAlgorithmSpaceDragDetected(MouseEvent mouseEvent) {
    }

    public void onAlgorithmSpaceMouseDragged(MouseEvent mouseEvent) {
    }

    public void onAlgorithmSpaceMouseReleased(MouseEvent mouseEvent) {
    }

    public void onAlgorithmSpaceDragOver(DragEvent dragEvent) {
    }

    public void onAlgorithmSpaceDragDropped(DragEvent dragEvent) {
    }
}
