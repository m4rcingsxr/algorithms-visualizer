package com.marcinseweryn.visualizer.view;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GraphNode extends Button {

    private static final Logger logger = LogManager.getLogger(GraphNode.class);

    private static int count = 0;

    private final ObservableList<Edge> edges = FXCollections.observableArrayList();

    private SimpleObjectProperty<GraphNode> parent = new SimpleObjectProperty<>(null);

    SimpleStringProperty info = new SimpleStringProperty() {
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(
                    "ID: " + getId() +
                            " | Parent: " + parent.getValue()
            );

            return sb.toString();
        }
    };

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

        getStyleClass().add("vertex");

        info.bind(Bindings.concat(parent));
    }

    // used in accordion to show the styled vertex
    public GraphNode(GraphNode vertex) {

        // on change of original vertex - update this one
        textProperty().bind(vertex.textProperty());
        getStyleClass().setAll(vertex.getStyleClass());
    }

    public static void decrementCount() {
        count--;
    }

    public void addEdge(Edge temp) {
        this.edges.add(temp);
    }

    public List<Edge> getEdges() {
        return this.edges;
    }

    public List<GraphNode> getNeighbours() {
        return this.edges.stream().map(e -> e.getNeighbour(this)).toList();
    }

    public void removeEdge(Edge edge) {
        this.edges.remove(edge);
    }

    public GraphNode getParentVertex() {
        return parent.getValue();
    }

    public void setParentNode(GraphNode parentVertex) {
        this.parent.setValue(parentVertex);
    }

    public Edge getConnection(GraphNode neighbour) {
        for (Edge a : edges) {
            if (a.getNeighbour(this) == neighbour) {
                return a;
            }
        }
        return null;
    }

    public SimpleStringProperty getSimpleString() {
        /*
         *The simple string property will be used in listViews. This way the listview will be
         * updated
         * when the node's properties change.
         */
        return info;
    }

    public void clearStyle() {
        this.getStyleClass().removeAll("start", "destination", "visited", "candidate-nodes", "path");
    }
}
