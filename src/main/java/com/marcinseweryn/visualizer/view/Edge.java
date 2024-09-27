package com.marcinseweryn.visualizer.view;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class Edge extends PaneArrow {

    private final GraphNode vertex1;
    private final GraphNode vertex2;

    private final DoubleProperty weight = new SimpleDoubleProperty();

    private final Label weightLabel = new Label();

    public Edge(GraphNode vertex1, GraphNode vertex2) {
        super(vertex1.getLayoutX(), vertex1.getLayoutY(), vertex2.getLayoutX(), vertex2.getLayoutY());

        this.vertex1 = vertex1;
        this.vertex2 = vertex2;

        this.x1.bind(vertex1.layoutXProperty());
        this.y1.bind(vertex1.layoutYProperty());
        this.x2.bind(vertex2.layoutXProperty());
        this.y2.bind(vertex2.layoutYProperty());

        this.weight.addListener((obs, oldVal, newVal) -> {
           if (newVal.doubleValue() != 0) {
               mainLine.getStyleClass().add("weighted");
           } else {
               mainLine.getStyleClass().remove("weighted");
           }
        });

        this.weightLabel.visibleProperty().bind(this.weight.isNotEqualTo(0));
        this.weightLabel.textProperty().bind(Bindings.concat("w:[", this.weight.asString(), "]"));

        addContent(new HBox(5, weightLabel));
    }

    public GraphNode getNeighbour(GraphNode vertex) {
        return vertex == vertex1 ? vertex2 : vertex1;
    }

    public void setHeadAVisible(GraphNode vertex, boolean b) {
        if(vertex == vertex1) {
            setHeadAVisible(b);
        } else {
            setHeadBVisible(b);
        }
    }

    public boolean isHeadVisible(GraphNode vertex) {
        return vertex == this.vertex1 ? isHeadAVisible() : isHeadBVisible();
    }

    public DoubleProperty weightProperty() {
        return this.weight;
    }

    public void setWeight(double weight) {
        this.weight.set(weight);
    }

    public double getWeight() {
        return this.weight.getValue();
    }
}
