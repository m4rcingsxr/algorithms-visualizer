package com.marcinseweryn.visualizer;

import com.marcinseweryn.visualizer.view.GraphNode;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PathFindingController {

    private static final Logger logger = LogManager.getLogger(PathFindingController.class);

    // main view
    private final AnchorPane graphPane;

    // internal
    private GraphNode vertex1;

    public PathFindingController(AnchorPane graphPane) {
        this.graphPane = graphPane;
        logger.info("PathFindingController initialized with graphPane.");
    }

    void onGraphMousePressed(MouseEvent mouseEvent) {
        logger.debug("Mouse pressed at coordinates: [X: {}, Y: {}]", mouseEvent.getX(), mouseEvent.getY());

        if (mouseEvent.isPrimaryButtonDown()) {
            logger.debug("Creating a new GraphNode at coordinates: [X: {}, Y: {}]", mouseEvent.getX(), mouseEvent.getY());

            this.vertex1 = new GraphNode(mouseEvent.getX(), mouseEvent.getY());

            logger.debug("Adding new GraphNode with ID {} to the graphPane.", this.vertex1.getId());

            graphPane.getChildren().add(this.vertex1);
        }
    }

    void onGraphPaneMouseReleased(MouseEvent mouseEvent) {
        logger.debug("Mouse released at coordinates: [X: {}, Y: {}]", mouseEvent.getX(), mouseEvent.getY());
    }

    void onGraphPaneDragDetected(MouseEvent mouseEvent) {
        logger.debug("Drag detected at coordinates: [X: {}, Y: {}]", mouseEvent.getX(), mouseEvent.getY());
    }

    void onGraphPaneMouseDragged(MouseEvent mouseEvent) {
        logger.debug("Mouse dragged to coordinates: [X: {}, Y: {}]", mouseEvent.getX(), mouseEvent.getY());
    }

    // After a drag is detected, the system starts handling drag-and-drop events such as onDragOver and onDragDropped
    // triggered continuously while dragging an object over a target
    void onGraphPaneDragOver(DragEvent dragEvent) {
        logger.debug("Drag over at coordinates: [X: {}, Y: {}]", dragEvent.getX(), dragEvent.getY());
    }

    // dragged object is dropped onto a target
    void onGraphPaneDragDropped(DragEvent dragEvent) {
        logger.debug("Drag dropped at coordinates: [X: {}, Y: {}]", dragEvent.getX(), dragEvent.getY());
    }
}
