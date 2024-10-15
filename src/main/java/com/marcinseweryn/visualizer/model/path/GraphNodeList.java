package com.marcinseweryn.visualizer.model.path;

import com.marcinseweryn.visualizer.view.GraphNode;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.ListView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing a list of graph nodes.
 * Provides basic operations for managing a list of graph nodes.
 */
public class GraphNodeList extends GraphNodeVisualizer {

    private static final Logger logger = LogManager.getLogger(GraphNodeList.class);

    // List to store graph nodes
    private final List<GraphNode> graphNodeList = new ArrayList<>();

    /**
     * Constructor to initialize the list visualizer with a ListType and a ListView.
     *
     * @param listType The type of the list (e.g., CANDIDATE_NODES, VISITED) to apply relevant styles.
     * @param list     The ListView in which the graph nodes will be displayed.
     */
    protected GraphNodeList(ViewType listType, ListView<SimpleStringProperty> list) {
        super(listType, list);
        logger.debug("GraphNodeList initialized with list type: {}", listType);
    }

    @Override
    public void addNode(GraphNode node) {
        logger.debug("Adding node to list: {}", node);
        super.addNodeInfoToList(node);
        graphNodeList.add(node);
    }

    @Override
    public GraphNode removeNode() {
        logger.error("Remove operation not supported for GraphNodeList.");
        throw new UnsupportedOperationException("Method not supported for GraphNodeList data structure");
    }

    @Override
    public boolean containsNode(GraphNode node) {
        return graphNodeList.contains(node);
    }

    @Override
    public boolean isEmpty() {
        return graphNodeList.isEmpty();
    }

    /**
     * Retrieves a graph node from the list.
     *
     * @param node The node to retrieve.
     * @return The graph node if present in the list.
     */
    public GraphNode get(GraphNode node) {
        logger.debug("Retrieving node from list: {}", node);
        return graphNodeList.get(graphNodeList.indexOf(node));
    }
}
