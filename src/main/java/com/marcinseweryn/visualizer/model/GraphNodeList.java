package com.marcinseweryn.visualizer.model;

import com.marcinseweryn.visualizer.view.GraphNode;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.ListView;

import java.util.ArrayList;
import java.util.List;

public class GraphNodeList extends GraphNodeVisualizer {

    private final List<GraphNode> graphNodeList = new ArrayList<>();

    /**
     * Constructor to initialize the visualizer with a ListType and a ListView.
     * Ensures that the visualized nodes are updated on the JavaFX application thread.
     *
     * @param listType The type of the list (e.g., CANDIDATE_NODES, VISITED) to apply relevant styles.
     * @param list     The ListView in which the graph nodes will be displayed.
     */
    protected GraphNodeList(ListType listType,
                            ListView<SimpleStringProperty> list) {
        super(listType, list);
    }

    @Override
    public void addNode(GraphNode node) {
        super.addNodeInfoToList(node);
        graphNodeList.add(node);
    }

    @Override
    public GraphNode removeNode() {
        throw new RuntimeException("Method not supported for GraphNodeList data structure");
    }

    @Override
    public boolean containsNode(GraphNode node) {
        return graphNodeList.contains(node);
    }

    @Override
    public boolean isEmpty() {
        return graphNodeList.isEmpty();
    }

    public GraphNode get(GraphNode node) {
        return graphNodeList.get(graphNodeList.indexOf(node));
    }
}
