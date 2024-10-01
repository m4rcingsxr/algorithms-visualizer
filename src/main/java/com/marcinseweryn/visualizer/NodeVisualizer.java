package com.marcinseweryn.visualizer;

import com.marcinseweryn.visualizer.view.GraphNode;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public abstract class NodeVisualizer {

    // A list of nodes to be visualized
    ObservableList<SimpleStringProperty> visualizedNodes = FXCollections.observableArrayList();

    public static final int TYPE_PENDING_NODES = 0, TYPE_VISITED = 1;

    private int listType;

    protected NodeVisualizer( int listType) {
        // Run UI updates on the JavaFX application thread
        Platform.runLater(() -> {
            this.listType = listType;

//            if (listType == TYPE_PENDING_NODES) {
//                controller.frontierListView.setItems(visualizedNodes);
//            } else if (listType == TYPE_VISITED) {
//                controller.visitedListView.setItems(visualizedNodes);
//            }
        });
    }

    // Visualize adding a node
    void visualizeAdd(GraphNode node) {
        Platform.runLater(() -> {
//            visualizedNodes.add(node.getSimpleString());
            node.getStyleClass().removeAll("start", "destination", "visited", "pending-nodes", "path");
            if (listType == TYPE_PENDING_NODES) {
                node.getStyleClass().add("pending-nodes");
            } else if (listType == TYPE_VISITED) {
                node.getStyleClass().add("visited");
            }
        });
    }

    // Visualize removing a node
    void visualizeRemove(GraphNode node) {
        Platform.runLater(() -> {
//            visualizedNodes.remove(node.getSimpleString());
//            controller.applyNodeStyle((VisNode) node, Controller.STYLE_DEFAULT);
        });
    }

    public abstract void addVertex(GraphNode node);

    public abstract GraphNode removeVertex();

    public abstract boolean containsNode(GraphNode node);

    public abstract boolean isEmpty();

}
