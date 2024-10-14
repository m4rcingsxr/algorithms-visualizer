package com.marcinseweryn.visualizer.view;

import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

public class SortingRectangles extends HBox {

    private final List<Integer> unsortedList;
    private final Integer max;
    private final Integer min;
    private final double containerHeight;
    private final double containerWidth;

    public SortingRectangles(List<Integer> unsortedList, Integer max, Integer min, double containerHeight, double containerWidth) {
        this.unsortedList = unsortedList;
        this.max = max;
        this.min = min;
        this.containerHeight = containerHeight / 1.5;
        this.containerWidth = containerWidth;

        initializeSortingRectangles();
    }

    private void initializeSortingRectangles() {
        double calculatedWidth = containerWidth / unsortedList.size();
        double rectangleWidth = Math.min(calculatedWidth, 20);

        for (Integer i : unsortedList) {
            double scaledHeight = getScaledHeight(i);
            VBox rectangle = new VBox();
            rectangle.setPrefWidth(rectangleWidth); // Set the calculated width
            rectangle.setMaxHeight(scaledHeight);
            rectangle.setPrefHeight(scaledHeight);
            rectangle.getStyleClass().add("sorting-rectangle");

            getChildren().add(rectangle);
        }

        setAlignment(Pos.TOP_CENTER);
    }

    private double getScaledHeight(Integer value) {
        // If max equals min, all values are the same, so return a fixed height
        if (max.equals(min)) {
            return containerHeight / 2;
        }

        // Scale the height of the rectangle based on the value, ensuring that the minimum value gets a proportionally small height
        double scaledHeight = ((double) (value - min) / (max - min)) * containerHeight;

        // To ensure that no rectangle has a height of zero, introduce a small offset
        double minHeightOffset = 0.1 * containerHeight;
        return Math.max(scaledHeight + minHeightOffset, minHeightOffset); // Ensure each rectangle has a non-zero height
    }
}
