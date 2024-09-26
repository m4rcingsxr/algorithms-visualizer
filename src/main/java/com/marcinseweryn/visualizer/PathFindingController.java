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
    private GraphNode vertex2;

    private boolean deleteNode;

    public PathFindingController(AnchorPane graphPane) {
        this.graphPane = graphPane;
        logger.info("PathFindingController initialized with graphPane.");
    }

    void onGraphMousePressed(MouseEvent mouseEvent) {
        logger.debug("Mouse pressed at coordinates: [X: {}, Y: {}]", mouseEvent.getX(), mouseEvent.getY());

        if (mouseEvent.isPrimaryButtonDown()) {
            this.vertex1 = createAndAddVertex(mouseEvent.getX(), mouseEvent.getY());
        }
    }

    void onGraphPaneMouseReleased(MouseEvent mouseEvent) {
        logger.debug("Mouse released at coordinates: [X: {}, Y: {}]", mouseEvent.getX(), mouseEvent.getY());
        this.vertex2 = null;
    }

    void onGraphPaneDragDetected(MouseEvent mouseEvent) {
        logger.debug("Drag detected at coordinates: [X: {}, Y: {}]", mouseEvent.getX(), mouseEvent.getY());
        if (mouseEvent.isPrimaryButtonDown()) {

            // here's position is translated to the cursor (initialization step)
            this.vertex2 = createAndAddVertex(mouseEvent.getX(), mouseEvent.getY());
        }
    }

    void onGraphPaneMouseDragged(MouseEvent mouseEvent) {
        logger.debug("Mouse dragged to coordinates: [X: {}, Y: {}]", mouseEvent.getX(), mouseEvent.getY());

        if(this.vertex2 != null) {
            this.vertex2.setLayoutX(mouseEvent.getX());
            this.vertex2.setLayoutY(mouseEvent.getY());
        }
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

    private GraphNode createAndAddVertex(double x, double y) {
        logger.debug("Creating a new GraphNode at coordinates: [X: {}, Y: {}]", x, y);

        GraphNode graphNode = new GraphNode(x, y);

        graphNode.setOnMousePressed(this::handleVertexPressed);
        graphNode.setOnMouseReleased(e -> handleVertexReleased(e, graphNode));
        graphNode.setOnDragDetected(e -> handleVertexDragDetected(e, graphNode));
        graphNode.setOnMouseDragged(e -> handleVertexDragged(e, graphNode));

        logger.debug("Adding new GraphNode with ID {} to the graphPane.", graphNode.getId());

        graphPane.getChildren().add(graphNode);

        return graphNode;
    }

    private void handleVertexPressed(MouseEvent mouseEvent) {
        if (mouseEvent.isSecondaryButtonDown()) {
            deleteNode = true;
        }
    }


    private void handleVertexReleased(MouseEvent mouseEvent, GraphNode node) {
        if(this.deleteNode) {
            deleteNode(node);
            deleteNode = false;
        }
        this.vertex1 = null;
        this.vertex2 = null;
    }

    private void deleteNode(GraphNode node) {
        this.graphPane.getChildren().remove(node);
    }

    private void handleVertexDragged(MouseEvent e, GraphNode graphNode) {
        if(this.vertex2 != null) {

            // relative from graphNode
            this.vertex2.setLayoutX(graphNode.getLayoutX() + e.getX() + graphNode.getTranslateX());
            this.vertex2.setLayoutY(graphNode.getLayoutY() + e.getY() + graphNode.getTranslateY());
        }
    }

    private void handleVertexDragDetected(MouseEvent mouseEvent, GraphNode graphNode) {
        if(mouseEvent.isPrimaryButtonDown()) {
            this.vertex1 = graphNode;
            this.vertex2 = createAndAddVertex(graphNode.getLayoutX() + mouseEvent.getX() + graphNode.getTranslateX() ,
                                              graphNode.getLayoutY() +  mouseEvent.getY() + graphNode.getTranslateX());
        }
    }

}
