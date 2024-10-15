package com.marcinseweryn.visualizer.model.path.algorithm;

import com.marcinseweryn.visualizer.model.path.*;
import com.marcinseweryn.visualizer.view.Edge;
import com.marcinseweryn.visualizer.view.GraphNode;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Implementation of Bellman-Ford algorithm for finding shortest paths in a weighted graph.
 * This class visualizes the process of detecting shortest paths and potential negative cycles.
 */
public class BellmanFord extends GraphAlgorithm {

    private static final Logger logger = LogManager.getLogger(BellmanFord.class);

    // Visualizer for the distance list during the algorithm's execution
    private final GraphNodeVisualizer distanceNodeList;

    /**
     * Constructor for initializing the Bellman-Ford algorithm with required components.
     *
     * @param algorithmTab    The VBox for displaying the algorithm's visual elements.
     * @param pseudocodeList  The ListView for displaying the algorithm pseudocode.
     * @param startNode       The starting node for the algorithm.
     * @param destinationNode The destination node for the algorithm.
     * @param algorithmSpace  The pane for visualizing the algorithm's execution.
     */
    public BellmanFord(VBox algorithmTab,
                       ListView<String> pseudocodeList,
                       SimpleObjectProperty<GraphNode> startNode,
                       SimpleObjectProperty<GraphNode> destinationNode,
                       AnchorPane algorithmSpace) {
        super(algorithmTab, pseudocodeList, startNode, destinationNode, algorithmSpace);
        this.distanceNodeList = initializeGraphNodeVisualizer(ViewType.DISTANCE, DataStructureType.LIST);
        logger.debug("BellmanFord algorithm initialized with startNode: {} and destinationNode: {}", startNode,
                     destinationNode
        );
    }

    /**
     * Executes the Bellman-Ford algorithm.
     * The algorithm finds the shortest paths and detects negative weight cycles.
     */
    @Override
    public void executeAlgorithm() {
        logger.info("Starting Bellman-Ford execution.");

        // Initialize distances and set the distance for the start node
        pauseAtStep(0);  // Initialize
        int start = Integer.parseInt(startNode.get().getId());

        pauseAtStep(1);
        List<GraphNode> graph = getGraph();
        graph.forEach(distanceNodeList::addNode);

        int N = graph.size();
        DistanceList distanceList = new DistanceList();

        pauseAtStep(2);  // Step after initializing distances
        startNode.get().setDistance(0.0);
        distanceList.set(start, 0.0);

        logger.debug("Set start node distance to 0. Start node: {}", startNode.get());


        // Main loop: Relaxation step (N-1 times)
        pauseAtStep(3);
        for (int i = 0; i < N - 1; i++) {
            logger.debug("Relaxation iteration {}", i + 1);

            pauseAtStep(4);
            for (GraphNode graphNode : graph) {
                int from = Integer.parseInt(graphNode.getId());
                setCurrentNode(graphNode);

                pauseAtStep(5);  // Process current node
                for (GraphNode neighbor : graphNode.getNeighbors()) {
                    setNeighborNode(neighbor);
                    Edge edge = graphNode.getConnection(neighbor);
                    int to = Integer.parseInt(neighbor.getId());

                    pauseAtStep(6);  // Check edge weight
                    if (distanceList.get(from) + edge.getWeight() < distanceList.get(to)) {
                        pauseAtStep(7);  // Relaxation step: Update distance
                        distanceList.set(to, distanceList.get(from) + edge.getWeight());
                        getNeighborNode().setDistance(distanceList.get(from) + edge.getWeight());
                        logger.debug("Updated distance of node {}: {}", neighbor.getId(), distanceList.get(to));
                    }

                    setNeighborNode(null);  // Reset neighbor after processing
                }
            }
        }

        // Detect negative weight cycles
        pauseAtStep(9);  // Step before checking for negative cycles
        logger.debug("Checking for negative weight cycles.");
        for (int i = 0; i < N - 1; i++) {
            pauseAtStep(10);
            for (GraphNode graphNode : graph) {
                int from = Integer.parseInt(graphNode.getId());

                pauseAtStep(11);
                for (GraphNode neighbor : graphNode.getNeighbors()) {
                    Edge edge = graphNode.getConnection(neighbor);
                    int to = Integer.parseInt(neighbor.getId());

                    pauseAtStep(12);
                    if (distanceList.get(from) + edge.getWeight() < distanceList.get(to)) {
                        pauseAtStep(13);
                        distanceList.set(to, Double.NEGATIVE_INFINITY);
                        neighbor.setDistance(Double.NEGATIVE_INFINITY);

                        neighbor.clearStyle();
                        neighbor.setPrimaryClass("negative-cycle");

                        logger.warn("Detected negative cycle at node: {}", neighbor.getId());
                    }
                }
            }
        }

        logger.info("Bellman-Ford execution completed. Final distances: {}", distanceList);
    }

    /**
     * Sets the pseudocode for the Bellman-Ford algorithm to be displayed in the UI.
     */
    @Override
    public void setPseudocode() {
        logger.debug("Setting pseudocode for Bellman-Ford algorithm.");
        this.pseudocode.addAll(
                "Bellman-Ford(start):",
                "\tinitialize distances with Double.POSITIVE_INFINITY",
                "\tset distance[start] = 0",

                "\tfor i = 0 to N - 1 do:",                  // Loop N-1 times for relaxation
                "\t\tfor each node in graph do:",
                "\t\t\tfor each edge of node do:",
                "\t\t\t\tif distance[edge.from] + edge.weight < distance[edge.to] then:",
                "\t\t\t\t\tupdate distance[edge.to]",

                "\nDetectNegativeCycles():",
                "\tfor i = 0 to N - 1 do:",
                "\t\tfor each node in graph do:",
                "\t\t\tfor each edge of node do:",
                "\t\t\t\tif distance[edge.from] + edge.weight < distance[edge.to] then:",
                "\t\t\t\t\tmark distance[edge.to] as Double.NEGATIVE_INFINITY"
        );
    }
}
