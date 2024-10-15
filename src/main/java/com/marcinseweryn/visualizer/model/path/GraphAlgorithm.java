package com.marcinseweryn.visualizer.model.path;

import com.marcinseweryn.visualizer.controller.GraphTabController;
import com.marcinseweryn.visualizer.model.Algorithm;
import com.marcinseweryn.visualizer.view.Edge;
import com.marcinseweryn.visualizer.view.GraphNode;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class representing a graph traversal algorithm.
 * This class is responsible for managing traversal between nodes, visualizing paths,
 * and tracking the current and neighboring nodes. It also handles algorithm-specific
 * operations such as path drawing and node state management.
 *
 * This class is an extension of the {@link Algorithm} class.
 */
public abstract class GraphAlgorithm extends Algorithm {

    private static final Logger logger = LogManager.getLogger(GraphAlgorithm.class);

    // Starting and destination nodes for the graph traversal
    protected final SimpleObjectProperty<GraphNode> startNode = new SimpleObjectProperty<>();
    protected final SimpleObjectProperty<GraphNode> destinationNode = new SimpleObjectProperty<>();

    // List to store the nodes that form the path
    private final List<GraphNode> path = new ArrayList<>();

    // Reference to the graphical elements in the algorithm tab
    private final VBox algorithmTab;
    private final AnchorPane algorithmSpace;

    // Currently active and neighboring nodes during traversal
    private GraphNode currentNode;
    private GraphNode neighborNode;

    /**
     * Constructor for initializing the GraphAlgorithm class with necessary visual elements and bindings.
     *
     * @param algorithmTab      The VBox that contains the algorithm view components.
     * @param pseudocodeList    The ListView to display pseudocode steps.
     * @param startNode         The starting node for the traversal.
     * @param destinationNode   The destination node for the traversal.
     * @param algorithmSpace    The pane where the algorithm visualization takes place.
     */
    protected GraphAlgorithm(
            VBox algorithmTab,
            ListView<String> pseudocodeList,
            SimpleObjectProperty<GraphNode> startNode,
            SimpleObjectProperty<GraphNode> destinationNode,
            AnchorPane algorithmSpace) {
        super(pseudocodeList);
        this.algorithmTab = algorithmTab;
        this.startNode.bind(startNode);
        this.destinationNode.bind(destinationNode);
        this.algorithmSpace = algorithmSpace;
        logger.debug("GraphAlgorithm initialized with startNode: {} and destinationNode: {}", startNode, destinationNode);
    }

    /**
     * Adds a node to the current path being traversed.
     *
     * @param node The GraphNode to be added to the path.
     */
    protected void addToPath(GraphNode node) {
        path.add(node);
        logger.debug("Added node to path: {}", node);
    }

    /**
     * Visualizes the path by updating the style of each node and connection to indicate it belongs to the path.
     */
    protected void visualizePath() {
        logger.debug("Visualizing path with {} nodes.", path.size());
        for (GraphNode node : path) {
            Platform.runLater(() -> {
                node.clearStyle();  // Clear any existing styles
                node.getStyleClass().add("path");  // Apply the "path" style to the node
                if (node.getParentNode() != null) {
                    Edge connection = node.getConnection(node.getParentNode());
                    connection.setStyleClass("path");
                }
            });
        }
    }

    /**
     * Sets the current active node during traversal and updates its visual style.
     *
     * @param node The GraphNode to be set as the current node.
     */
    protected void setCurrentNode(GraphNode node) {
        resetCurrentNodeStyle();  // Reset style for the previously active node

        if (node != null) {
            logger.debug("Setting current node to {}", node);
            node.pseudoClassStateChanged(GraphTabController.currentNodeStyle, true);
        }

        this.currentNode = node;
    }

    /**
     * Resets the visual style of the current active node.
     */
    protected void resetCurrentNodeStyle() {
        if (currentNode != null) {
            logger.debug("Resetting style for current node: {}", currentNode);
            currentNode.pseudoClassStateChanged(GraphTabController.currentNodeStyle, false);
        }
    }

    /**
     * Gets the current active node.
     *
     * @return The current GraphNode.
     */
    protected GraphNode getCurrentNode() {
        return currentNode;
    }

    /**
     * Sets the neighboring node during traversal and updates its visual style.
     *
     * @param node The GraphNode to be set as the neighboring node.
     */
    protected void setNeighborNode(GraphNode node) {
        if (neighborNode != null) {
            logger.debug("Resetting style for previous neighbor node: {}", neighborNode);
            neighborNode.pseudoClassStateChanged(GraphTabController.neighborNodeStyle, false);
        }

        if (node != null) {
            logger.debug("Setting neighbor node to {}", node);
            node.pseudoClassStateChanged(GraphTabController.neighborNodeStyle, true);
        }

        this.neighborNode = node;
    }

    /**
     * Gets the neighboring node during traversal.
     *
     * @return The neighboring GraphNode.
     */
    protected GraphNode getNeighborNode() {
        return neighborNode;
    }


    /**
     * Retrieves all the GraphNode elements currently present in the algorithm space.
     *
     * @return A list of GraphNodes in the algorithm space.
     */
    protected List<GraphNode> getGraph() {
        return algorithmSpace.getChildren().stream()
                .filter(GraphNode.class::isInstance)
                .map(GraphNode.class::cast)
                .toList();
    }

    /**
     * Initializes the GraphNode visualizer for different data structure types (e.g., stack, queue).
     *
     * @param viewType       The type of visualizer to initialize.
     * @param dsType         The type of data structure to represent (STACK, QUEUE, etc.).
     * @return The initialized GraphNodeVisualizer.
     */
    protected GraphNodeVisualizer initializeGraphNodeVisualizer(ViewType viewType, DataStructureType dsType) {
        VBox algorithmView = new VBox();
        Label label = new Label();
        label.setContentDisplay(ContentDisplay.RIGHT);
        label.getStyleClass().add("list-view-label");
        ListView<SimpleStringProperty> view = new ListView<>();
        algorithmView.getChildren().addAll(label, view);

        switch (viewType) {
            case VISITED -> label.setText("Visited");
            case DISTANCE -> label.setText("Distance");
            case CANDIDATE_NODES -> label.setText("Candidate Nodes");
            default -> throw new RuntimeException("Unsupported visualizer type: " + viewType);
        }

        Platform.runLater(() -> this.algorithmTab.getChildren().add(algorithmView));

        return switch (dsType) {
            case STACK -> new GraphNodeStack(viewType, view);
            case QUEUE -> new GraphNodeQueue(viewType, view);
            case PRIORITY_QUEUE -> new GraphNodePriorityQueue(viewType, view);
            case LIST -> new GraphNodeList(viewType, view);
            default -> throw new RuntimeException("Unsupported data structure type: " + dsType);
        };
    }

}
