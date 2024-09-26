package com.marcinseweryn.visualizer.view;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Group;
import javafx.scene.shape.Polyline;

public class Arrow extends Group {

    private final Polyline mainLine = new Polyline();

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

        this.getChildren().add(mainLine);
    }

    private void initializeChangeListenerOnCoordinates() {
        SimpleDoubleProperty[] coordinates = new SimpleDoubleProperty[]{this.x1, this.x2, this.y1, this.y2};

        for (SimpleDoubleProperty coordinate : coordinates) {
            // Update the line whenever a coordinate changes
            coordinate.addListener((o, oldVal, newVal) -> updatePolyline());
        }

        updatePolyline();
    }

    private void updatePolyline() {
        mainLine.getPoints().setAll(
                x1.get(), y1.get(),
                x2.get(), y2.get()
        );
    }

}
