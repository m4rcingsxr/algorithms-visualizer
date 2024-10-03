package com.marcinseweryn.visualizer.view;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Group;
import javafx.scene.shape.Polyline;

/**
 * This class represents an arrow composed of a main line and two arrowheads.
 * It dynamically updates the arrow's position and appearance based on the coordinates (x1, y1) and (x2, y2),
 * which represent the start and end points of the arrow. The arrowhead size and angle are calculated based
 * on constants defined for scaling and geometry.
 */
public class Arrow extends Group {

    private static final double ARROW_SCALE = 20;
    private static final double ARROW_HEAD_LENGTH = 10;
    private static final double ARROW_HEAD_ANGLE = Math.toRadians(20);

    protected final Polyline mainLine = new Polyline();
    protected final Polyline headA = new Polyline();
    protected final Polyline headB = new Polyline();

    protected final SimpleDoubleProperty x1 = new SimpleDoubleProperty();
    protected final SimpleDoubleProperty x2 = new SimpleDoubleProperty();
    protected final SimpleDoubleProperty y1 = new SimpleDoubleProperty();
    protected final SimpleDoubleProperty y2 = new SimpleDoubleProperty();

    /**
     * Constructor for the Arrow class. Initializes the arrow's start and end points
     * and sets up listeners to automatically update the arrow when coordinates change.
     *
     * @param x1 The starting x-coordinate of the arrow
     * @param y1 The starting y-coordinate of the arrow
     * @param x2 The ending x-coordinate of the arrow
     * @param y2 The ending y-coordinate of the arrow
     */
    public Arrow(double x1, double y1, double x2, double y2) {
        this.x1.set(x1);
        this.x2.set(x2);
        this.y1.set(y1);
        this.y2.set(y2);

        // Sets up listeners on coordinates to update arrow dynamically
        initializeChangeListenerOnCoordinates();

        // Adds the arrow parts to the Group
        this.getChildren().addAll(mainLine, headA, headB);

        mainLine.getStyleClass().add("arrow-line");
    }

    /**
     * Initializes listeners on the coordinates of the arrow. Whenever a coordinate changes,
     * the arrow is recalculated and redrawn.
     */
    private void initializeChangeListenerOnCoordinates() {
        SimpleDoubleProperty[] coordinates = new SimpleDoubleProperty[]{this.x1, this.x2, this.y1, this.y2};

        // Attach listeners to each coordinate property
        for (SimpleDoubleProperty coordinate : coordinates) {
            coordinate.addListener((o, oldVal, newVal) -> updatePolyline());
        }

        // Initial update to draw the arrow
        updatePolyline();
    }

    /**
     * Updates the arrow's main line and arrowhead positions whenever the coordinates change.
     */
    private void updatePolyline() {
        double[] start = scale(x1.get(), y1.get(), x2.get(), y2.get());
        double[] end = scale(x2.get(), y2.get(), x1.get(), y1.get());

        // Extract scaled coordinates for the arrow's shaft
        double x1 = start[0];
        double y1 = start[1];
        double x2 = end[0];
        double y2 = end[1];

        // Update the main line of the arrow
        mainLine.getPoints().setAll(x1, y1, x2, y2);

        // Calculate the points for the two arrowheads
        double[] arrowA = scaleArrowHead(x1, y1, x2, y2);
        double[] arrowB = scaleArrowHead(x2, y2, x1, y1);

        // Set the positions of the arrowheads
        headA.getPoints().setAll(arrowA[0], arrowA[1], x1, y1, arrowA[2], arrowA[3]);
        headB.getPoints().setAll(arrowB[0], arrowB[1], x2, y2, arrowB[2], arrowB[3]);
    }

    /**
     * Scales the coordinates for the start and end of the arrow shaft.
     *
     * @param x1 Starting x-coordinate.
     * @param y1 Starting y-coordinate.
     * @param x2 Ending x-coordinate.
     * @param y2 Ending y-coordinate.
     * @return A double array containing the new (scaled) start or end coordinates [x, y].
     */
    private double[] scale(double x1, double y1, double x2, double y2) {
        // Calculate the angle (theta) between the two points (x1, y1) and (x2, y2)
        double theta = Math.atan2(y2 - y1, x2 - x1);

        return new double[]{
                x1 + Math.cos(theta) * Arrow.ARROW_SCALE,
                y1 + Math.sin(theta) * Arrow.ARROW_SCALE
        };
    }

    /**
     * Calculates the points for the arrowhead
     *
     * @param x1 Starting x-coordinate of the arrow.
     * @param y1 Starting y-coordinate of the arrow.
     * @param x2 Ending x-coordinate of the arrow.
     * @param y2 Ending y-coordinate of the arrow.
     * @return A double array containing the coordinates [x1, y1, x2, y2] for the arrowhead.
     */
    private double[] scaleArrowHead(double x1, double y1, double x2, double y2) {
        // Calculate the angle (theta) between the start and end of the arrow (shaft direction)
        double theta = Math.atan2(y2 - y1, x2 - x1);

        // 1. Rotate the angle by +ARROW_HEAD_ANGLE to get one side of the arrowhead.
        double arrowX1 = x1 + Math.cos(theta + Arrow.ARROW_HEAD_ANGLE) * Arrow.ARROW_HEAD_LENGTH;
        double arrowY1 = y1 + Math.sin(theta + Arrow.ARROW_HEAD_ANGLE) * Arrow.ARROW_HEAD_LENGTH;

        // 2. Rotate the angle by -ARROW_HEAD_ANGLE to get the other side of the arrowhead.
        double arrowX2 = x1 + Math.cos(theta - Arrow.ARROW_HEAD_ANGLE) * Arrow.ARROW_HEAD_LENGTH;
        double arrowY2 = y1 + Math.sin(theta - Arrow.ARROW_HEAD_ANGLE) * Arrow.ARROW_HEAD_LENGTH;

        return new double[]{arrowX1, arrowY1, arrowX2, arrowY2};
    }

    // Getters and setters for visibility of the arrowheads
    public boolean isHeadAVisible() {
        return this.headA.isVisible();
    }

    public boolean isHeadBVisible() {
        return this.headB.isVisible();
    }

    public void setHeadAVisible(boolean headAVisible) {
        this.headA.setVisible(headAVisible);
    }

    public void setHeadBVisible(boolean headBVisible) {
        this.headB.setVisible(headBVisible);
    }

    // Getters and property methods for the coordinates
    public double getX1() {
        return x1.get();
    }

    public SimpleDoubleProperty x1Property() {
        return x1;
    }

    public double getX2() {
        return x2.get();
    }

    public SimpleDoubleProperty x2Property() {
        return x2;
    }

    public double getY1() {
        return y1.get();
    }

    public SimpleDoubleProperty y1Property() {
        return y1;
    }

    public double getY2() {
        return y2.get();
    }

    public SimpleDoubleProperty y2Property() {
        return y2;
    }

    public void setStyleClass(String clazz) {
        mainLine.getStyleClass().add(clazz);
        headA.getStyleClass().add(clazz);
        headB.getStyleClass().add(clazz);
    }
}
