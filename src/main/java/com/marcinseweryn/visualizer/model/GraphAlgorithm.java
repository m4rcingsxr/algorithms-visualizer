package com.marcinseweryn.visualizer.model;

import com.marcinseweryn.visualizer.controller.GraphTabController;
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
    private VBox algorithmTab;

    // Visualizers for the candidate and visited nodes during traversal
//    protected GraphNodeVisualizer candidateNodeList;
//    protected GraphNodeVisualizer visitedNodeList;
//
    // Currently active and neighboring graph nodes during traversal
    private GraphNode currentNode;
    private GraphNode neighborNode;

    private AnchorPane algorithmSpace;

    /**
     * Used only for load algorithms to box list
     */
    protected GraphAlgorithm() {
        super(null);
    }

    /**
     * Constructor for GraphAlgorithm that initializes the visualizers and binds the start and destination nodes.
     *
     * @param candidateNoteListView  The ListView for visualizing candidate nodes.
     * @param visitedNodeList    The ListView for visualizing visited nodes.
     * @param startNode          The starting node for the traversal.
     * @param destinationNode    The destination node for the traversal.
     */
    protected GraphAlgorithm(
            VBox algorithmTab,
            ListView<String> pseudocodeList,
            SimpleObjectProperty<GraphNode> startNode,
            SimpleObjectProperty<GraphNode> destinationNode,
            AnchorPane algorithmSpace) {
        super(pseudocodeList);


        this.algorithmTab = algorithmTab;
//        this.candidateNodeList = new GraphNodeQueue(ListType.CANDIDATE_NODES, candidateNodeListView);
//        this.visitedNodeList = new GraphNodeStack(ListType.VISITED, visitedNodeListView);

//        this.candidateNodeListView = candidateNodeListView;

        this.startNode.bind(startNode);
        this.destinationNode.bind(destinationNode);

        this.algorithmSpace = algorithmSpace;
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
                if(node.getParentNode() != null) {
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
        // Remove the "current" style from the previous current node
        this.resetCurrentNodeStyle();

        // Add the "current" style to the new current node
        if (node != null) {
            node.pseudoClassStateChanged(GraphTabController.currentNodeStyle, true);
        }

        this.currentNode = node;
    }

    protected void resetCurrentNodeStyle() {
        if (this.currentNode != null) {
            this.currentNode.pseudoClassStateChanged(GraphTabController.currentNodeStyle, false);
        }
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
            this.neighborNode.pseudoClassStateChanged(GraphTabController.neighborNodeStyle, false);
        }

        // Add the "neighbor" style to the new neighbor node
        if (node != null) {
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

    protected List<GraphNode> getGraph() {
        return algorithmSpace.getChildren().stream().filter(GraphNode.class::isInstance).map(GraphNode.class::cast).toList();
    }

    protected GraphNodeVisualizer initializeGraphNodeVisualizer(String visualizerType, DataStructureType dsType) {
        VBox algorithmView = new VBox();
        Label label = new Label();
        label.setContentDisplay(ContentDisplay.RIGHT);
        label.getStyleClass().add("list-view-label");
        ListView<SimpleStringProperty> view = new ListView<>();
        algorithmView.getChildren().addAll(label, view);

        ListType listType;
        switch (visualizerType) {
            case "VISITED" -> {
                label.setText("Visited");
                listType = ListType.VISITED;
            }
            case "DISTANCE" -> {
                label.setText("Distance");
                listType = ListType.DISTANCE;
            }
            case "CANDIDATE" -> {
                label.setText("Candidate Nodes");
                listType = ListType.CANDIDATE_NODES;
            }
            default -> throw new RuntimeException("Not supported list view");
        }

        Platform.runLater(() -> {
            this.algorithmTab.getChildren().add(algorithmView);
        });

        GraphNodeVisualizer visualizer;
        switch (dsType) {
            case STACK -> visualizer = new GraphNodeStack(listType, view);
            case QUEUE -> visualizer = new GraphNodeQueue(listType, view);
            case PRIORITY_QUEUE -> visualizer = new GraphNodePriorityQueue(listType, view);
            default -> throw new RuntimeException("Not supported list visualizer");
        }

        return visualizer;
    }

}
