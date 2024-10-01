package com.marcinseweryn.visualizer;

import com.marcinseweryn.visualizer.view.GraphNode;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;

public abstract class NodeVisualizer {

    // A list of nodes to be visualized
    ObservableList<SimpleStringProperty> visualizedNodes = FXCollections.observableArrayList();

    public static final int TYPE_CANDIDATE_NODES = 0, TYPE_VISITED = 1;

    private int listType;

    protected NodeVisualizer(int listType, ListView candidateNodes, ListView visitedNodes) {
        // Run UI updates on the JavaFX application thread
        Platform.runLater(() -> {
            this.listType = listType;

            if (listType == TYPE_CANDIDATE_NODES) {
                candidateNodes.setItems(visualizedNodes);
            } else if (listType == TYPE_VISITED) {
                visitedNodes.setItems(visualizedNodes);
            }
        });
    }

    // Visualize adding a node
    void visualizeAdd(GraphNode node) {
        Platform.runLater(() -> {
            visualizedNodes.add(node.getSimpleString());
            node.getStyleClass().removeAll("start", "destination", "visited", "pending-nodes", "path");
            if (listType == TYPE_CANDIDATE_NODES) {
                node.getStyleClass().add("pending-nodes");
            } else if (listType == TYPE_VISITED) {
                node.getStyleClass().add("visited");
            }
        });
    }

    // Visualize removing a node
    void visualizeRemove(GraphNode node) {
        Platform.runLater(() -> {
            node.getStyleClass().removeAll("start", "destination", "visited", "pending-nodes", "path");
            visualizedNodes.remove(node.getSimpleString());
        });
    }

    public abstract void addVertex(GraphNode node);

    public abstract GraphNode removeVertex();

    public abstract boolean containsNode(GraphNode node);

    public abstract boolean isEmpty();

}
