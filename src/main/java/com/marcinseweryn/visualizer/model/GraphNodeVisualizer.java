package com.marcinseweryn.visualizer.model;

import com.marcinseweryn.visualizer.view.GraphNode;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;

/**
 * Abstract class responsible for managing the visualization of graph nodes in a ListView.
 * This class provides mechanisms to add, remove, and style nodes while ensuring updates occur
 * on the JavaFX application thread for thread safety.
 */
public abstract class GraphNodeVisualizer {

    // Observable list representing the graph nodes being visualized in the ListView.
    protected ObservableList<SimpleStringProperty> visualizedNodes = FXCollections.observableArrayList();

    // Type of the list (CANDIDATE_NODES or VISITED) to apply specific styles
    private final ListType listType;

    /**
     * Constructor to initialize the visualizer with a ListType and a ListView.
     * Ensures that the visualized nodes are updated on the JavaFX application thread.
     *
     * @param listType The type of the list (e.g., CANDIDATE_NODES, VISITED) to apply relevant styles.
     * @param list     The ListView in which the graph nodes will be displayed.
     */
    protected GraphNodeVisualizer(ListType listType, ListView<SimpleStringProperty> list) {
        this.listType = listType;

        // Ensure UI updates occur on the JavaFX application thread
        Platform.runLater(() -> list.setItems(visualizedNodes));
    }

    /**
     * Adds the given graph node's information to the visualized list.
     *
     * @param node The GraphNode whose information is to be added to the visualized list.
     */
    public void addNodeInfoToList(GraphNode node) {
        if(listType.equals(ListType.DISTANCE)) {
            Platform.runLater(() -> visualizedNodes.add(node.getDistanceInfo()));
        } else {
            Platform.runLater(() -> visualizedNodes.add(node.getGeneralInfo()));
        }
    }

    /**
     * Applies a specific visual style to the given graph node based on the ListType.
     * Clears any existing styles and adds either 'candidate-nodes' or 'visited' style classes.
     *
     * @param node The GraphNode to which the visual style will be applied.
     */
    public void applyVisualStyleOnNode(GraphNode node) {
        Platform.runLater(() -> {
            node.clearStyle();  // Clear any existing styles
            if (listType == ListType.CANDIDATE_NODES) {
                node.getStyleClass().add("candidate-nodes");
            } else if (listType == ListType.VISITED) {
                node.getStyleClass().add("visited");
            }
        });
    }

    /**
     * Removes the given graph node's information from the visualized list and clears its style.
     *
     * @param node The GraphNode whose information and style are to be removed from the list.
     */
    public void removeNodeFromListAndClearStyle(GraphNode node) {
        Platform.runLater(() -> {
            node.clearStyle();
            visualizedNodes.remove(node.getGeneralInfo());
        });
    }

    /**
     * Adds a graph node to the visualized list and applies the corresponding visual style.
     * This method combines adding the node, applying its visual style, and displaying its information in one step.
     *
     * @param node The GraphNode to be added, visualized, and displayed.
     */
    public void addNodeAndVisualize(GraphNode node) {
        addNode(node);
        addNodeInfoToList(node);
        applyVisualStyleOnNode(node);
    }

    /**
     * Adds the given graph node's information to the visualized list and applies the visual style.
     * This method is a convenience method for combining these two actions.
     *
     * @param node The GraphNode to be added and styled in the visualized list.
     */
    public void addNodeAndApplyStyle(GraphNode node) {
        addNodeInfoToList(node);      // Add the node's info to the visualized list
        applyVisualStyleOnNode(node); // Apply the corresponding visual style
    }

    /**
     * Abstract method to add a graph node to the underlying manager or structure.
     * The implementation is provided by subclasses.
     *
     * @param node The GraphNode to be added.
     */
    public abstract void addNode(GraphNode node);


    /**
     * Abstract method to remove a graph node from the underlying manager or structure.
     * The implementation is provided by subclasses.
     *
     * @return The GraphNode that was removed.
     */
    public abstract GraphNode removeNode();

    /**
     * Checks whether the underlying list or structure contains the given graph node.
     *
     * @param node The GraphNode to check for in the list.
     * @return true if the list contains the node, false otherwise.
     */
    public abstract boolean containsNode(GraphNode node);

    /**
     * Checks whether the underlying visualized list is empty.
     *
     * @return true if the visualized list is empty, false otherwise.
     */
    public abstract boolean isEmpty();

}
