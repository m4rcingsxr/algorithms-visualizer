package com.marcinseweryn.visualizer;

import com.marcinseweryn.visualizer.view.GraphNode;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;

import static com.marcinseweryn.visualizer.ListType.CANDIDATE_NODES;
import static com.marcinseweryn.visualizer.ListType.VISITED;

public abstract class NodeVisualizer {

    // A list of nodes to be visualized
    ObservableList<SimpleStringProperty> visualizedNodes = FXCollections.observableArrayList();

    private ListType listType;

    protected NodeVisualizer(ListType listType, ListView<SimpleStringProperty>  candidateNodes, ListView<SimpleStringProperty>  visitedNodes) {
        // Run UI updates on the JavaFX application thread
        Platform.runLater(() -> {
            this.listType = listType;

            if (listType == CANDIDATE_NODES) {
                candidateNodes.setItems(visualizedNodes);
            } else if (listType == VISITED) {
                visitedNodes.setItems(visualizedNodes);
            }
        });
    }

    // Visualize adding a node
    void visualizeAdd(GraphNode node) {
        Platform.runLater(() -> {
            node.clearStyle();
            visualizedNodes.add(node.getSimpleString());
            if (listType == CANDIDATE_NODES) {
                node.getStyleClass().add("candidate-nodes");
            } else if (listType == VISITED) {
                node.getStyleClass().add("visited");
            }
        });
    }

    // Visualize removing a node
    void visualizeRemove(GraphNode node) {
        Platform.runLater(() -> {
            node.clearStyle();
            visualizedNodes.remove(node.getSimpleString());
        });
    }

    public abstract void addVertex(GraphNode node);

    public abstract GraphNode removeVertex();

    public abstract boolean containsNode(GraphNode node);

    public abstract boolean isEmpty();

}
