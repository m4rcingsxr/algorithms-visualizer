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
 * Implementation of Depth-First Search (DFS) algorithm for graph traversal.
 * It explores nodes by diving deeper along each branch before backtracking.
 * This class visualizes the process of traversing the graph in an unweighted graph.
 */
public class DepthFirstSearch extends GraphAlgorithm {

    private static final Logger logger = LogManager.getLogger(DepthFirstSearch.class);

    // Visualizers for candidate (stack) and visited nodes
    private final GraphNodeVisualizer candidateNodeList;
    private final GraphNodeVisualizer visitedNodeList;

    /**
     * Constructor for initializing the DFS algorithm with required components.
     *
     * @param algorithmTab      The VBox for displaying the algorithm's visual elements.
     * @param pseudocodeList    The ListView for displaying the algorithm pseudocode.
     * @param startNode         The starting node for the algorithm.
     * @param destinationNode   The destination node for the algorithm.
     * @param algorithmSpace    The pane for visualizing the algorithm's execution.
     */
    public DepthFirstSearch(VBox algorithmTab,
                            ListView<String> pseudocodeList,
                            SimpleObjectProperty<GraphNode> startNode,
                            SimpleObjectProperty<GraphNode> destinationNode,
                            AnchorPane algorithmSpace) {
        super(algorithmTab, pseudocodeList, startNode, destinationNode, algorithmSpace);

        // Initialize visualizers for candidate (stack) and visited nodes
        candidateNodeList = initializeGraphNodeVisualizer(ViewType.CANDIDATE_NODES, DataStructureType.STACK);
        visitedNodeList = initializeGraphNodeVisualizer(ViewType.VISITED, DataStructureType.STACK);

        logger.debug("DepthFirstSearch algorithm initialized with startNode: {} and destinationNode: {}", startNode, destinationNode);
    }

    @Override
    public String toString() {
        return "Depth First Search";
    }

    /**
     * Executes the Depth-First Search algorithm.
     * It explores the graph by diving deep into one branch and backtracking when necessary.
     */
    @Override
    public void executeAlgorithm() {
        logger.info("Starting Depth-First Search execution.");

        // Add the start node to the candidate list (stack) and visualize
        pauseAtStep(0);  // Initialize
        logger.debug("Adding start node to the candidate stack: {}", startNode.get());
        pauseAtStep(1);  // Pause after adding the start node
        candidateNodeList.addNodeAndVisualize(this.startNode.get());

        // Main loop: Continue until the candidate list (stack) is empty
        pauseAtStep(2);
        while (!candidateNodeList.isEmpty()) {
            pauseAtStep(3);  // Step: Remove the top node from the stack
            setCurrentNode(candidateNodeList.removeNode());
            logger.debug("Processing current node: {}", getCurrentNode());

            // Check if the current node is the destination
            pauseAtStep(4);
            if (getCurrentNode() == this.destinationNode.get()) {
                logger.info("Destination node found: {}", getCurrentNode());
                pauseAtStep(5);  // Visualize the shortest path
                reconstructPath(getCurrentNode());
                break;
            }

            // Explore all unvisited neighbors
            pauseAtStep(6);
            if (!visitedNodeList.containsNode(getCurrentNode())) {
                logger.debug("Visiting current node: {}", getCurrentNode());
                pauseAtStep(7);
                visitedNodeList.addNodeAndVisualize(getCurrentNode());

                pauseAtStep(8);
                for (GraphNode neighbor : getCurrentNode().getNeighbors()) {
                    setNeighborNode(neighbor);

                    pauseAtStep(9);  // Check if the neighbor has been visited
                    if (!visitedNodeList.containsNode(getNeighborNode())) {
                        logger.debug("Visiting neighbor: {}", getNeighborNode());

                        pauseAtStep(10);
                        // Add the neighbor to the candidate stack and visualize
                        candidateNodeList.addNodeAndVisualize(getNeighborNode());
                        pauseAtStep(11);  // Pause after adding to the candidate stack

                        // Set the parent of the neighbor
                        getNeighborNode().setParentNode(getCurrentNode());
                        logger.debug("Set parent of {} to {}", getNeighborNode(), getCurrentNode());
                    }
                    setNeighborNode(null);  // Reset neighbor node after processing
                }
            }
        }
    }

    /**
     * Reconstructs the path from the destination node back to the start node.
     *
     * @param current The current node (destination) to begin path reconstruction.
     */
    private void reconstructPath(GraphNode current) {
        logger.debug("Reconstructing the path from destination to start.");
        pauseAtStep(13);
        for (GraphNode node = current; node != null; node = node.getParentNode()) {
            pauseAtStep(14);
            addToPath(node);  // Add each node in the path to be visualized
            logger.debug("Added node to path: {}", node);
        }
        pauseAtStep(15);
        visualizePath();

    }

    /**
     * Sets the pseudocode for the DFS algorithm to be displayed in the UI.
     */
    @Override
    public void setPseudocode() {
        logger.debug("Setting pseudocode for Depth-First Search algorithm.");
        this.pseudocode.setAll(
                "DFS(start, destination):",
                "\tstack.push(start)",                       // Push start node onto the stack
                "\twhile the stack is not empty do:",
                "\t\tcurrent = stack.pop()",                // Pop the top node from the stack
                "\t\tif current == destination then:",
                "\t\t\tShortestPath() and return",         // Reconstruct the path if destination is found

                "\t\tif current is unvisited then:",
                "\t\t\tvisited.add(current)",               // Mark the current node as visited

                "\t\t\tfor each neighbor of current do:",
                "\t\t\t\tif neighbor is unvisited then:",
                "\t\t\t\t\tstack.push(neighbor)",          // Push unvisited neighbors onto the stack
                "\t\t\t\t\tparent[neighbor] = current",     // Set current as parent of neighbor

                "\nShortestPath():",
                "\tfor N = destination; N != null; N = N.parent:",
                "\t\tadd N to path",                        // Add each node in the path
                "\tvisualize path"                          // Visualize the constructed path
        );
    }
}
