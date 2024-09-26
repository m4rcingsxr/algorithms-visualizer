package com.marcinseweryn.visualizer.view;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Group;
import javafx.scene.shape.Polyline;

public class Arrow extends Group {

    private static final double ARROW_SCALE = 20;
    private static final double ARROW_HEAD_LENGTH = 10;
    private static final double ARROW_HEAD_ANGLE = Math.toRadians(20);

    private final Polyline mainLine = new Polyline();
    private final Polyline headA = new Polyline();
    private final Polyline headB = new Polyline();

    protected final SimpleDoubleProperty x1 = new SimpleDoubleProperty();
    protected final SimpleDoubleProperty x2 = new SimpleDoubleProperty();
    protected final SimpleDoubleProperty y1 = new SimpleDoubleProperty();
    protected final SimpleDoubleProperty y2 = new SimpleDoubleProperty();

    public Arrow(double x1, double y1, double x2, double y2) {
        this.x1.set(x1);
        this.x2.set(x2);
        this.y1.set(y1);
        this.y2.set(y2);

        initializeChangeListenerOnCoordinates();

        this.getChildren().addAll(mainLine, headA, headB);

        mainLine.getStyleClass().add("arrow-line");
    }

    private void initializeChangeListenerOnCoordinates() {
        SimpleDoubleProperty[] coordinates = new SimpleDoubleProperty[]{this.x1, this.x2, this.y1,
                                                                        this.y2};

        for (SimpleDoubleProperty coordinate : coordinates) {
            // Update the line whenever a coordinate changes
            coordinate.addListener((o, oldVal, newVal) -> updatePolyline());
        }

        updatePolyline();
    }

    private void updatePolyline() {
        // Get the scaled start and end points
        double[] start = scale(x1.get(), y1.get(), x2.get(), y2.get());
        double[] end = scale(x2.get(), y2.get(), x1.get(), y1.get());

        // Extract scaled coordinates
        double x1 = start[0];
        double y1 = start[1];
        double x2 = end[0];
        double y2 = end[1];

        // Update the main line
        mainLine.getPoints().setAll(x1, y1, x2, y2);

        // Arrow heads
        double[] arrowA = scaleArrowHead(x1, y1, x2, y2);
        double[] arrowB = scaleArrowHead(x2, y2, x1, y1);

        headA.getPoints().setAll(arrowA[0], arrowA[1], x1, y1, arrowA[2], arrowA[3]);
        headB.getPoints().setAll(arrowB[0], arrowB[1], x2, y2, arrowB[2], arrowB[3]);
    }

    private double[] scale(double x1, double y1, double x2, double y2) {
        // Calculate the angle
        double theta = Math.atan2(y2 - y1, x2 - x1);
        // Return scaled x and y
        return new double[]{
                x1 + Math.cos(theta) * Arrow.ARROW_SCALE,
                y1 + Math.sin(theta) * Arrow.ARROW_SCALE
        };
    }

    private double[] scaleArrowHead(double x1, double y1, double x2, double y2) {
        // Calculate the angle of the line (slope)
        double theta = Math.atan2(y2 - y1, x2 - x1);

        // Calculate the first point of the arrowhead
        double arrowX1 = x1 + Math.cos(theta + Arrow.ARROW_HEAD_ANGLE) * Arrow.ARROW_HEAD_LENGTH;
        double arrowY1 = y1 + Math.sin(theta + Arrow.ARROW_HEAD_ANGLE) * Arrow.ARROW_HEAD_LENGTH;

        // Calculate the second point of the arrowhead
        double arrowX2 = x1 + Math.cos(theta - Arrow.ARROW_HEAD_ANGLE) * Arrow.ARROW_HEAD_LENGTH;
        double arrowY2 = y1 + Math.sin(theta - Arrow.ARROW_HEAD_ANGLE) * Arrow.ARROW_HEAD_LENGTH;

        return new double[]{arrowX1, arrowY1, arrowX2, arrowY2};
    }

}
