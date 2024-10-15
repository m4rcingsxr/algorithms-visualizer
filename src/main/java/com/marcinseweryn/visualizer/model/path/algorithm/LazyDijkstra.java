package com.marcinseweryn.visualizer.model.path.algorithm;

import com.marcinseweryn.visualizer.model.path.*;
import com.marcinseweryn.visualizer.view.GraphNode;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Implementation of Dijkstra's algorithm for graph traversal using lazy updates.
 * This class visualizes the process of finding the shortest path from a start node to a destination node.
 */
public class LazyDijkstra extends GraphAlgorithm {

    private static final Logger logger = LogManager.getLogger(LazyDijkstra.class);

    // List to store distances for each node in the graph
    private final DistanceList distance = new DistanceList();

    // Visualizers for candidate nodes, visited nodes, and distance list
    private final GraphNodeVisualizer visitedNodeList;
    private final GraphNodeVisualizer candidateNodeList;
    private final GraphNodeVisualizer distanceNodeList;

    /**
     * Constructor to initialize the LazyDijkstra algorithm with the necessary components.
     *
     * @param algorithmTab      The VBox for displaying the algorithm's visual elements.
     * @param pseudocodeList    The ListView for displaying the algorithm pseudocode.
     * @param startNode         The starting node for the algorithm.
     * @param destinationNode   The destination node for the algorithm.
     * @param algorithmSpace    The pane for visualizing the algorithm's execution.
     */
    public LazyDijkstra(VBox algorithmTab,
                        ListView<String> pseudocodeList,
                        SimpleObjectProperty<GraphNode> startNode,
                        SimpleObjectProperty<GraphNode> destinationNode,
                        AnchorPane algorithmSpace) {
        super(algorithmTab, pseudocodeList, startNode, destinationNode, algorithmSpace);

        // Initialize visualizers for different data structures
        candidateNodeList = initializeGraphNodeVisualizer(ViewType.CANDIDATE_NODES, DataStructureType.PRIORITY_QUEUE);
        visitedNodeList = initializeGraphNodeVisualizer(ViewType.VISITED, DataStructureType.STACK);
        distanceNodeList = initializeGraphNodeVisualizer(ViewType.DISTANCE, DataStructureType.LIST);

        logger.debug("LazyDijkstra algorithm initialized with start node: {} and destination node: {}", startNode, destinationNode);
    }

    /**
     * Executes the Dijkstra algorithm by calling the main method {@link #dijkstra()}.
     */
    @Override
    public void executeAlgorithm() {
        logger.info("Starting Lazy Dijkstra execution.");
        dijkstra();
    }

    /**
     * Main Dijkstra algorithm implementation with lazy updates.
     * The algorithm finds the shortest path from the start node to the destination node, visualizing the process.
     */
    public void dijkstra() {
        logger.debug("Initializing Dijkstra's algorithm.");

        getGraph().forEach(distanceNodeList::addNode);

        // Initialization steps
        pauseAtStep(1);  // Step: Initialize
        pauseAtStep(2);  // Step: Set distance to zero

        this.startNode.get().setDistance(0.0);
        pauseAtStep(3);  // Step: Add start node to the candidate list
        candidateNodeList.addNodeAndVisualize(this.startNode.get());
        this.distance.set(0, 0.0);
        distanceNodeList.addNode(this.startNode.get());

        // Main loop
        pauseAtStep(4);  // Step: Begin while-loop
        while (!candidateNodeList.isEmpty()) {
            pauseAtStep(5);  // Step: Remove node from candidate list
            setCurrentNode(candidateNodeList.removeNode());

            // Mark the node as visited
            pauseAtStep(6);  // Step: Mark node as visited
            visitedNodeList.addNodeAndVisualize(getCurrentNode());

            // Check if current node is the destination
            pauseAtStep(7);  // Step: Check if destination
            if (this.getCurrentNode().getId().equals(this.destinationNode.get().getId())) {
                pauseAtStep(8);  // Step: Destination reached
                shortestPath(this.distance.get(Integer.parseInt(this.getCurrentNode().getId())));
                return;
            }

            // Lazy delete check
            pauseAtStep(9);  // Step: Lazy delete check
            if (this.distance.get(Integer.parseInt(getCurrentNode().getId())) < getCurrentNode().getDistance()) {
                logger.debug("Skipping outdated node: {}", getCurrentNode());
                continue;  // Skip outdated nodes
            }

            // Explore neighbors
            pauseAtStep(10);  // Step: Explore neighbors
            for (GraphNode neighbor : getCurrentNode().getNeighbors()) {
                setNeighborNode(neighbor);

                pauseAtStep(11);  // Step: Skip if neighbor is already visited
                if (visitedNodeList.containsNode(getNeighborNode())) continue;

                // Calculate new distance
                pauseAtStep(12);  // Step: Calculate new distance
                double newDistance = getCurrentNode().getDistance() + getCurrentNode().getConnection(
                        getNeighborNode()).getWeight();

                // Relaxation step
                pauseAtStep(13);  // Step: Relaxation
                if (newDistance < this.distance.get(Integer.parseInt(getNeighborNode().getId()))) {

                    pauseAtStep(14);  // Step: Update distance
                    this.distance.set(Integer.parseInt(getNeighborNode().getId()), newDistance);
                    getNeighborNode().setParentNode(getCurrentNode());
                    getNeighborNode().setDistance(newDistance);

                    // Add neighbor to candidate list
                    pauseAtStep(15);
                    candidateNodeList.addNodeAndVisualize(getNeighborNode());
                }
                setNeighborNode(null);
            }
        }

        // If no path found, mark it as an infinite distance
        shortestPath(this.destinationNode.get().getDistance());
    }

    /**
     * Visualizes the shortest path once it has been found.
     *
     * @param distance The distance of the shortest path.
     */
    private void shortestPath(Double distance) {
        logger.info("Visualizing the shortest path.");
        pauseAtStep(20);


        // Step: Check if distance is infinite (no path found)
        pauseAtStep(21);
        if (Double.isInfinite(distance)) {
            logger.info("No path found to the destination.");
            return;
        }

        logger.info("Shortest path found with distance: {}", distance);

        // Step: Trace back the path from destination to start
        pauseAtStep(22);
        for (GraphNode node = destinationNode.get(); node != null; node = node.getParentNode()) {
            addToPath(node);
            pauseAtStep(23);  // Step: Add node to path
        }

        // Step: Visualize the complete path
        pauseAtStep(24);
        visualizePath();
        logger.info("Shortest path visualized.");
    }


    @Override
    public void setPseudocode() {
        this.pseudocode.addAll(
                "Dijkstra(start, end):",
                "\tinitialize distances with Double.POSITIVE_INFINITY",
                "\tset distance[start] = 0",
                "\tadd start node to the priority queue",

                "\twhile the priority queue is not empty do:",
                "\t\tnode = priority queue.poll()",
                "\t\tmark node as visited",

                "\t\tif node == end then:",
                "\t\t\treconstructPath() and return",

                "\t\tif distance[node] < node.value then:",
                "\t\t\tcontinue",

                "\t\tfor each neighbor of node do:",
                "\t\t\tif neighbor is visited then:",
                "\t\t\t\tcontinue",

                "\t\t\tcalculate newDistance = distance[node] + edge.weight",
                "\t\t\tif newDistance < distance[neighbor] then:",
                "\t\t\t\tupdate distance[neighbor]",
                "\t\t\t\tset parent of neighbor to node",
                "\t\t\t\tadd neighbor to the priority queue",

                "\nShortestPath(distance):",
                "\tif distance is infinite then:",
                "\t\treturn",
                "\tfor N = destination; N != null; N = N.parent:",
                "\t\tadd N to path",
                "\tvisualize path"
        );
    }

}

