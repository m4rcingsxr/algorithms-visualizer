package com.marcinseweryn.visualizer.model.path;

import com.marcinseweryn.visualizer.view.GraphNode;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Abstract base class for managing the visualization of graph nodes in a ListView.
 * This class provides methods for adding, removing, and styling graph nodes while ensuring that
 * updates occur on the JavaFX application thread for thread safety.
 *
 * The concrete implementations of this class should define how nodes are added, removed,
 * and managed in the underlying structure. This class applies the Template Method design pattern.
 */
public abstract class GraphNodeVisualizer {

    private static final Logger logger = LogManager.getLogger(GraphNodeVisualizer.class);

    // Observable list representing the graph nodes being visualized in the ListView.
    protected final ObservableList<SimpleStringProperty> visualizedNodes = FXCollections.observableArrayList();

    // The type of the list (CANDIDATE_NODES, VISITED, etc.) to apply relevant styles
    private final ViewType listType;

    /**
     * Constructor to initialize the visualizer with a ViewType and a ListView for node visualization.
     * The nodes are set on the ListView using the JavaFX application thread.
     *
     * @param listType The type of the list (e.g., CANDIDATE_NODES, VISITED) to apply relevant styles.
     * @param list     The ListView in which the graph nodes will be displayed.
     */
    protected GraphNodeVisualizer(ViewType listType, ListView<SimpleStringProperty> list) {
        this.listType = listType;
        logger.debug("Initializing GraphNodeVisualizer with list type: {}", listType);

        // Ensure UI updates occur on the JavaFX application thread
        Platform.runLater(() -> list.setItems(visualizedNodes));
    }

    /**
     * Adds the given graph node's information to the visualized list.
     * This method ensures the update happens on the JavaFX application thread.
     *
     * @param node The GraphNode whose information is to be added to the visualized list.
     */
    public void addNodeInfoToList(GraphNode node) {
        Platform.runLater(() -> {
            if (listType == ViewType.DISTANCE) {
                logger.debug("Adding distance info for node: {}", node);
                visualizedNodes.add(node.getDistanceInfo());
            } else {
                logger.debug("Adding general info for node: {}", node);
                visualizedNodes.add(node.getGeneralInfo());
            }
        });
    }

    /**
     * Applies a specific visual style to the given graph node based on the ListType.
     * Clears any existing styles and adds the corresponding style classes.
     *
     * @param node The GraphNode to which the visual style will be applied.
     */
    public void applyVisualStyleOnNode(GraphNode node) {
        Platform.runLater(() -> {
            logger.debug("Applying visual style for node: {} with list type: {}", node, listType);
            node.clearStyle();

            switch (listType) {
                case CANDIDATE_NODES:
                    node.getStyleClass().add("candidate-nodes");
                    break;
                case VISITED:
                    node.getStyleClass().add("visited");
                    break;
                case NEGATIVE_CYCLE:
                    node.getStyleClass().add("negative-cycle");
                    break;
                default:
                    logger.warn("Unknown list type: {}", listType);
                    break;
            }
        });
    }

    /**
     * Removes the given graph node's information from the visualized list and clears its style.
     * This method ensures the update happens on the JavaFX application thread.
     *
     * @param node The GraphNode whose information and style are to be removed from the list.
     */
    public void removeNodeFromListAndClearStyle(GraphNode node) {
        Platform.runLater(() -> {
            logger.debug("Removing node info and clearing style for node: {}", node);
            node.clearStyle();
            visualizedNodes.remove(node.getGeneralInfo());
        });
    }

    /**
     * Adds a graph node to the visualized list and applies the corresponding visual style.
     * This method combines adding the node, applying its visual style, and displaying its information in one step.
     *
     * @param node The GraphNode to be added, visualized, and styled.
     */
    public void addNodeAndVisualize(GraphNode node) {
        logger.debug("Adding and visualizing node: {}", node);
        addNode(node);
        addNodeInfoToList(node);
        applyVisualStyleOnNode(node);
    }

    /**
     * Adds the given graph node's information to the visualized list and applies the visual style.
     * This is a convenience method to combine both actions.
     *
     * @param node The GraphNode to be added and styled in the visualized list.
     */
    public void addNodeAndApplyStyle(GraphNode node) {
        logger.debug("Adding node info and applying style for node: {}", node);
        addNodeInfoToList(node);
        applyVisualStyleOnNode(node);
    }

    // ----- Abstract methods to be implemented by subclasses -----

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
