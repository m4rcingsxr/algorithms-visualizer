package com.marcinseweryn.visualizer.model.path.algorithm;

import com.marcinseweryn.visualizer.model.path.DataStructureType;
import com.marcinseweryn.visualizer.model.path.GraphAlgorithm;
import com.marcinseweryn.visualizer.model.path.GraphNodeVisualizer;
import com.marcinseweryn.visualizer.model.path.ViewType;
import com.marcinseweryn.visualizer.view.GraphNode;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Implementation of Breadth-First Search (BFS) algorithm for graph traversal.
 * It explores nodes level by level using a queue and marks visited nodes to avoid revisits.
 * This class visualizes the process of finding the shortest path in an unweighted graph.
 */
public class BreadthFirstSearch extends GraphAlgorithm {

    private static final Logger logger = LogManager.getLogger(BreadthFirstSearch.class);

    // Visualizers for candidate (queue) and visited nodes
    private final GraphNodeVisualizer candidateNodeList;
    private final GraphNodeVisualizer visitedNodeList;

    /**
     * Constructor for initializing the BFS algorithm with required components.
     *
     * @param algorithmTab      The VBox for displaying the algorithm's visual elements.
     * @param pseudocodeList    The ListView for displaying the algorithm pseudocode.
     * @param startNode         The starting node for the algorithm.
     * @param destinationNode   The destination node for the algorithm.
     * @param algorithmSpace    The pane for visualizing the algorithm's execution.
     */
    public BreadthFirstSearch(
            VBox algorithmTab,
            ListView<String> pseudocodeList,
            SimpleObjectProperty<GraphNode> startNode,
            SimpleObjectProperty<GraphNode> destinationNode,
            AnchorPane algorithmSpace) {
        super(algorithmTab, pseudocodeList, startNode, destinationNode, algorithmSpace);

        // Initialize visualizers for candidate and visited nodes
        candidateNodeList = initializeGraphNodeVisualizer(ViewType.CANDIDATE_NODES, DataStructureType.QUEUE);
        visitedNodeList = initializeGraphNodeVisualizer(ViewType.VISITED, DataStructureType.LIST);

        logger.debug("BreadthFirstSearch algorithm initialized with startNode: {} and destinationNode: {}", startNode, destinationNode);
    }

    /**
     * Executes the Breadth-First Search algorithm.
     */
    @Override
    public void executeAlgorithm() {
        logger.info("Starting Breadth-First Search execution.");

        // Add the start node to the candidate list and visualize
        pauseAtStep(0);  // Initialize
        logger.debug("Adding start node to the candidate list: {}", startNode.get());
        pauseAtStep(1);  // Pause after adding the start node
        candidateNodeList.addNodeAndVisualize(this.startNode.get());

        // Mark the start node as visited
        pauseAtStep(2);
        visitedNodeList.addNode(this.startNode.get());

        // Main loop: Continue until the candidate list is empty
        pauseAtStep(3);
        while (!candidateNodeList.isEmpty()) {
            // Remove the current node from the queue
            pauseAtStep(4);
            setCurrentNode(candidateNodeList.removeNode());
            logger.debug("Processing current node: {}", getCurrentNode());

            // Apply visual style to the current node
            pauseAtStep(5);
            visitedNodeList.addNodeAndApplyStyle(getCurrentNode());

            // Check if the current node is the destination
            if (getCurrentNode() == this.destinationNode.get()) {
                logger.info("Destination node found: {}", getCurrentNode());
                resetCurrentNodeStyle();

                pauseAtStep(6);  // Visualize the shortest path
                reconstructPath(getCurrentNode());
                break;
            }

            // Explore all unvisited neighbors
            pauseAtStep(7);  // Begin neighbor exploration
            for (GraphNode neighbor : getCurrentNode().getNeighbors()) {
                setNeighborNode(neighbor);
                pauseAtStep(8);  // Check if the neighbor has been visited

                if (!visitedNodeList.containsNode(getNeighborNode())) {
                    logger.debug("Visiting neighbor: {}", getNeighborNode());

                    // Mark the neighbor as visited
                    visitedNodeList.addNode(getNeighborNode());
                    pauseAtStep(9);

                    // Add neighbor to the candidate list and visualize
                    candidateNodeList.addNodeAndVisualize(getNeighborNode());
                    pauseAtStep(10);  // Pause after adding to the candidate list

                    // Set the parent of the neighbor
                    getNeighborNode().setParentNode(getCurrentNode());
                    logger.debug("Set parent of {} to {}", getNeighborNode(), getCurrentNode());
                }
            }

            // Reset the neighbor node after processing
            setNeighborNode(null);
        }
    }

    /**
     * Reconstructs the path from the destination node back to the start node.
     *
     * @param current The current node (destination) to begin the path reconstruction.
     */
    private void reconstructPath(GraphNode current) {
        logger.debug("Reconstructing the path from destination to start.");
        pauseAtStep(13);
        for (GraphNode node = current; node != null; node = node.getParentNode()) {
            pauseAtStep(14);
            addToPath(node);
            pauseAtStep(15);
            visualizePath();
            logger.debug("Added node to path: {}", node);
        }
    }

    /**
     * Sets the pseudocode for the BFS algorithm to be displayed in the UI.
     */
    @Override
    public void setPseudocode() {
        logger.debug("Setting pseudocode for Breadth-First Search algorithm.");
        this.pseudocode.addAll(
                "BFS(start, destination):",
                "\tqueue.enqueue(start)",                  // Enqueue the start node
                "\tvisited.add(start)",                    // Mark start as visited

                "\twhile the queue is not empty do:",
                "\t\tcurrent = queue.dequeue()",           // Dequeue the front node
                "\t\tif current == destination then:",
                "\t\t\treconstructPath() and return",      // Reconstruct the path if destination is found

                "\t\tfor each neighbor of current do:",
                "\t\t\tif neighbor is unvisited then:",
                "\t\t\t\tvisited.add(neighbor)",          // Mark neighbor as visited
                "\t\t\t\tqueue.enqueue(neighbor)",        // Enqueue the neighbor
                "\t\t\t\tparent[neighbor] = current",     // Set current as parent of neighbor

                "\nShortestPath():",
                "\tfor N = destination; N != null; N = N.parent:",
                "\t\tadd N to path",                      // Add each node to the path
                "\tvisualize path"                        // Visualize the constructed path
        );
    }
}
