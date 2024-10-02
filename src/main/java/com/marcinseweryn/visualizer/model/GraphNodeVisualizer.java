package com.marcinseweryn.visualizer.model;

import com.marcinseweryn.visualizer.view.GraphNode;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;

/**
 * Abstract class for managing the visualization of graph nodes.
 * This class provides a mechanism to add and remove nodes from a ListView's
 * and ensures that updates happen on the JavaFX application thread.
 */
public abstract class GraphNodeVisualizer {

    // Observable list representing the nodes being visualized in the ListView.
    protected ObservableList<SimpleStringProperty> visualizedNodes = FXCollections.observableArrayList();

    // Type of the list (CANDIDATE_NODES or VISITED) to apply specific styles
    private final ListType listType;

    protected GraphNodeVisualizer(ListType listType, ListView<SimpleStringProperty>  list) {
        this.listType = listType;

        // Ensure UI updates occur on the JavaFX application thread
        Platform.runLater(() -> list.setItems(visualizedNodes));
    }

    /**
     * Adds a graph node to the visualized list and updates its style based on the list type.
     *
     * @param node The GraphNode to be added.
     */
    void visualizeAdd(GraphNode node) {
        Platform.runLater(() -> {
            node.clearStyle();  // Clear any existing styles
            visualizedNodes.add(node.getSimpleString());

            if (listType == ListType.CANDIDATE_NODES) {
                node.getStyleClass().add("candidate-nodes");
            } else if (listType == ListType.VISITED) {
                node.getStyleClass().add("visited");
            }
        });
    }

    /**
     * Removes a graph node from the visualized list.
     *
     * @param node The GraphNode to be removed.
     */
    void visualizeRemove(GraphNode node) {
        Platform.runLater(() -> {
            node.clearStyle();
            visualizedNodes.remove(node.getSimpleString());
        });
    }

    /**
     * Abstract method for adding a graph node to the manager.
     *
     * @param node The GraphNode to be added.
     */
    public abstract void addGraphNode(GraphNode node);

    /**
     * Abstract method for removing a graph node from the manager.
     *
     * @return The GraphNode that was removed.
     */
    public abstract GraphNode removeGraphNode();

    /**
     * Checks whether the visualized list contains a specific graph node.
     *
     * @param node The GraphNode to check for.
     * @return true if the list contains the node, false otherwise.
     */
    public abstract boolean containsGraphNode(GraphNode node);

    /**
     * Checks whether the visualized node list is empty.
     *
     * @return true if the list is empty, false otherwise.
     */
    public abstract boolean isEmpty();

}
