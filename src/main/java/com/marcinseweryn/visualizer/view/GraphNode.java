package com.marcinseweryn.visualizer.view;

import javafx.scene.control.Button;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GraphNode extends Button {

    Logger logger = LogManager.getLogger(GraphNode.class);

    private static int count = 0;

    // Constructor to create a new GraphNode at specified (x, y) coordinates
    public GraphNode(double x, double y) {
        this.setId(String.valueOf(count++));
        logger.debug("GraphNode created with ID {} at coordinates: [X: {}, Y: {}]", this.getId(), x, y);

        // Bind the text property of the node to its ID, so the ID is displayed on the node
        this.textProperty().bind(this.idProperty());
        logger.debug("GraphNode ID {} bound to textProperty.", this.getId());

        /*
         * Adjust the translation so that the mouse cursor is centered on the node:
         * - The X and Y properties of the node are adjusted by half of the node's width and height.
         * - This ensures that the node appears centered where the mouse was clicked.
         */
        this.translateXProperty().bind(widthProperty().divide(-2));
        this.translateYProperty().bind(heightProperty().divide(-2));
        logger.debug("GraphNode ID {} translation properties set (centered on mouse click).", this.getId());

        // Set the initial position of the node at the given (x, y) coordinates and log the action
        this.setLayoutX(x);
        this.setLayoutY(y);
        logger.debug("GraphNode ID {} positioned at coordinates: [X: {}, Y: {}]", this.getId(), x, y);
    }



}
