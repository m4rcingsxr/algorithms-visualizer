package com.marcinseweryn.visualizer.model.path;

import com.marcinseweryn.visualizer.view.GraphNode;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.ListView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayDeque;

/**
 * Abstract class representing a graph node deque.
 * Provides functionality to manage a deque of graph nodes with operations such as push, pop, enqueue, and dequeue.
 */
public abstract class GraphNodeDeque extends GraphNodeVisualizer {

    private static final Logger logger = LogManager.getLogger(GraphNodeDeque.class);

    // Internal deque to store pending graph nodes
    private final ArrayDeque<GraphNode> pendingNodes = new ArrayDeque<>();

    /**
     * Constructor to initialize the deque visualizer with a ListType and a ListView for visualization.
     *
     * @param listType The type of the list (e.g., CANDIDATE_NODES, VISITED) to apply relevant styles.
     * @param list     The ListView in which the graph nodes will be displayed.
     */
    protected GraphNodeDeque(ViewType listType, ListView<SimpleStringProperty> list) {
        super(listType, list);
        logger.debug("GraphNodeDeque initialized with list type: {}", listType);
    }

    /**
     * Pushes a graph node onto the deque.
     *
     * @param node The GraphNode to push.
     */
    public void push(GraphNode node) {
        logger.debug("Pushing node onto deque: {}", node);
        pendingNodes.push(node);
    }

    /**
     * Pops a graph node from the deque.
     *
     * @return The GraphNode that was popped.
     */
    public GraphNode pop() {
        logger.debug("Popping node from deque.");
        return pendingNodes.pop();
    }

    /**
     * Enqueues a graph node into the deque.
     *
     * @param node The GraphNode to enqueue.
     */
    public void enqueue(GraphNode node) {
        logger.debug("Enqueuing node into deque: {}", node);
        pendingNodes.add(node);
    }

    /**
     * Dequeues a graph node from the deque and removes its style.
     *
     * @return The GraphNode that was dequeued.
     */
    public GraphNode dequeue() {
        GraphNode node = pendingNodes.remove();
        logger.debug("Dequeuing node and removing style: {}", node);
        super.removeNodeFromListAndClearStyle(node);
        return node;
    }

    @Override
    public boolean containsNode(GraphNode node) {
        return pendingNodes.contains(node);
    }

    @Override
    public boolean isEmpty() {
        return pendingNodes.isEmpty();
    }
}
