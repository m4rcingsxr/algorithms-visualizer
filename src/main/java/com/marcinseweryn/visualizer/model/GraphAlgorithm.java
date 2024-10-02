package com.marcinseweryn.visualizer.model;

import com.marcinseweryn.visualizer.controller.PathFindingController;
import com.marcinseweryn.visualizer.view.GraphNode;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.ListView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class representing a graph traversal algorithm.
 * It manages the traversal between nodes, tracks the current and neighboring vertices,
 * and handles path visualization.
 */
public abstract class GraphAlgorithm extends Algorithm {

    public static final Logger logger = LogManager.getLogger(GraphAlgorithm.class);

    // Starting and destination nodes for the graph traversal
    protected final SimpleObjectProperty<GraphNode> startNode = new SimpleObjectProperty<>();
    protected final SimpleObjectProperty<GraphNode> destinationNode = new SimpleObjectProperty<>();

    // List to store the nodes forming the path
    private final List<GraphNode> path = new ArrayList<>();

    // Visualizers for the candidate and visited nodes during traversal
    protected GraphNodeVisualizer candidateNodeList;
    protected GraphNodeVisualizer visitedNodeList;

    // Currently active and neighboring graph nodes during traversal
    private GraphNode currentNode;
    private GraphNode neighborNode;

    /**
     * Used only for load algorithms to box list
     */
    protected GraphAlgorithm() {
        super();
    }

    /**
     * Constructor for GraphAlgorithm that initializes the visualizers and binds the start and destination nodes.
     *
     * @param candidateNodeList  The ListView for visualizing candidate nodes.
     * @param visitedNodeList    The ListView for visualizing visited nodes.
     * @param startNode          The starting node for the traversal.
     * @param destinationNode    The destination node for the traversal.
     */
    protected GraphAlgorithm(
            ListView<SimpleStringProperty> candidateNodeList,
            ListView<SimpleStringProperty> visitedNodeList,
            SimpleObjectProperty<GraphNode> startNode,
            SimpleObjectProperty<GraphNode> destinationNode) {
        this.candidateNodeList = new GraphNodeQueue(ListType.CANDIDATE_NODES, candidateNodeList);
        this.visitedNodeList = new GraphNodeStack(ListType.VISITED, visitedNodeList);

        this.startNode.bind(startNode);
        this.destinationNode.bind(destinationNode);
    }

    /**
     * Adds a graph node to the path.
     *
     * @param node The GraphNode to be added to the path.
     */
    protected void addToPath(GraphNode node) {
        path.add(node);
    }

    /**
     * Draws the path by applying a visual style to each node in the path.
     */
    protected void visualizePath() {
        for (GraphNode node : path) {
            Platform.runLater(() -> {
                node.clearStyle();  // Clear any existing styles
                node.getStyleClass().add("path");  // Apply the "path" style to the node
            });
        }
    }

    /**
     * Sets the current active node during traversal and updates its visual style.
     *
     * @param node The GraphNode to be set as the current node.
     */
    protected void setCurrentNode(GraphNode node) {
        // Remove the "current" style from the previous current node
        if (this.currentNode != null) {
            this.currentNode.pseudoClassStateChanged(PathFindingController.currentNodeStyle, false);
        }

        // Add the "current" style to the new current node
        if (node != null) {
            node.pseudoClassStateChanged(PathFindingController.currentNodeStyle, true);
        }

        this.currentNode = node;
    }

    /**
     * Gets the current active node during traversal.
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
        // Remove the "neighbor" style from the previous neighbor node
        if (this.neighborNode != null) {
            this.neighborNode.pseudoClassStateChanged(PathFindingController.neighborNodeStyle, false);
        }

        // Add the "neighbor" style to the new neighbor node
        if (node != null) {
            node.pseudoClassStateChanged(PathFindingController.neighborNodeStyle, true);
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

}
