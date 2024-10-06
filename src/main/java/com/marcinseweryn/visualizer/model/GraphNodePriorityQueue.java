package com.marcinseweryn.visualizer.model;

import com.marcinseweryn.visualizer.view.GraphNode;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.ListView;

import java.util.Comparator;
import java.util.PriorityQueue;

public class GraphNodePriorityQueue extends GraphNodeVisualizer {

    private final PriorityQueue<GraphNode> priorityQueue = new PriorityQueue<>(Comparator.comparing(GraphNode::getDistance));

    /**
     * Constructor to initialize the visualizer with a ListType and a ListView.
     * Ensures that the visualized nodes are updated on the JavaFX application thread.
     *
     * @param listType The type of the list (e.g., CANDIDATE_NODES, VISITED) to apply relevant styles.
     * @param list     The ListView in which the graph nodes will be displayed.
     */
    protected GraphNodePriorityQueue(ListType listType,
                                     ListView<SimpleStringProperty> list) {
        super(listType, list);
    }

    @Override
    public void addNode(GraphNode node) {
        this.priorityQueue.add(node);
    }

    @Override
    public GraphNode removeNode() {
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
