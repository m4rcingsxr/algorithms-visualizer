package com.marcinseweryn.visualizer.model.path;

import com.marcinseweryn.visualizer.view.GraphNode;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.ListView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Class representing a priority queue for graph nodes.
 * Nodes are prioritized based on their distance property using a {@link PriorityQueue}.
 */
public class GraphNodePriorityQueue extends GraphNodeVisualizer {

    private static final Logger logger = LogManager.getLogger(GraphNodePriorityQueue.class);

    // Priority queue based on node distance
    private final PriorityQueue<GraphNode> priorityQueue = new PriorityQueue<>(Comparator.comparing(GraphNode::getDistance));

    /**
     * Constructor to initialize the priority queue visualizer with a ListType and a ListView.
     *
     * @param listType The type of the list (e.g., CANDIDATE_NODES, VISITED) to apply relevant styles.
     * @param list     The ListView in which the graph nodes will be displayed.
     */
    protected GraphNodePriorityQueue(ViewType listType, ListView<SimpleStringProperty> list) {
        super(listType, list);
        logger.debug("GraphNodePriorityQueue initialized with list type: {}", listType);
    }

    @Override
    public void addNode(GraphNode node) {
        logger.debug("Adding node to priority queue: {}", node);
        this.priorityQueue.add(node);
    }

    @Override
    public GraphNode removeNode() {
        logger.debug("Removing node from priority queue.");
        return this.priorityQueue.remove();
    }

    @Override
    public boolean containsNode(GraphNode node) {
        return this.priorityQueue.contains(node);
    }

    @Override
    public boolean isEmpty() {
        return this.priorityQueue.isEmpty();
    }
}
