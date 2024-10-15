package com.marcinseweryn.visualizer.model.path;

import com.marcinseweryn.visualizer.view.GraphNode;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.ListView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class representing a graph node queue.
 * This class extends {@link GraphNodeDeque} and provides functionality for adding and removing nodes in a queue.
 */
public class GraphNodeQueue extends GraphNodeDeque {

    private static final Logger logger = LogManager.getLogger(GraphNodeQueue.class);

    /**
     * Constructor to initialize the queue visualizer with a ListType and a ListView.
     *
     * @param listType The type of the list (e.g., CANDIDATE_NODES, VISITED) to apply relevant styles.
     * @param list     The ListView in which the graph nodes will be displayed.
     */
    public GraphNodeQueue(ViewType listType, ListView<SimpleStringProperty> list) {
        super(listType, list);
        logger.debug("GraphNodeQueue initialized with list type: {}", listType);
    }

    @Override
    public void addNode(GraphNode node) {
        logger.debug("Adding node to queue: {}", node);
        super.enqueue(node);
    }

    @Override
    public GraphNode removeNode() {
        logger.debug("Removing node from queue.");
        return super.dequeue();
    }
}
