package com.marcinseweryn.visualizer.view;

import javafx.scene.Node;
import javafx.scene.layout.Pane;

/**
 * This class represents an arrow with a content pane positioned at the midpoint of the arrow.
 * It extends the functionality of the base `Arrow` class by adding the ability to place a `Pane`
 * that holds custom content
 */
public class PaneArrow extends Arrow {

    private final Pane content = new Pane();

    /**
     * Constructor for creating a `PaneArrow` with given start and end coordinates.
     * The content pane is automatically positioned at the midpoint between the start (x1, y1)
     * and end (x2, y2) points of the arrow.
     *
     * @param x1 The starting x-coordinate of the arrow.
     * @param y1 The starting y-coordinate of the arrow.
     * @param x2 The ending x-coordinate of the arrow.
     * @param y2 The ending y-coordinate of the arrow.
     */
    public PaneArrow(double x1, double y1, double x2, double y2) {
        super(x1, y1, x2, y2);

        // Add the content pane to the arrow group
        getChildren().addAll(content);

        // Bind the layout of the content pane to the midpoint of the arrow
        content.layoutXProperty().bind(
                x2Property().add(x1Property()).divide(2).subtract(content.widthProperty().divide(2))
        );
        content.layoutYProperty().bind(
                y2Property().add(y1Property()).divide(2).subtract(content.heightProperty().divide(2))
        );
    }

    /**
     * Adds custom content (a Node) to the content pane, replacing any existing content.
     * The content will be displayed at the midpoint of the arrow.
     *
     * @param content The content (a JavaFX Node) to add to the pane.
     */
    public void addContent(Node content) {
        this.content.getChildren().setAll(content);
    }

}
