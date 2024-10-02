package com.marcinseweryn.visualizer.model;

import com.marcinseweryn.visualizer.view.GraphNode;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.ListView;

public class GraphNodeQueue extends GraphNodeDeque {

    GraphNodeQueue(ListType listType, ListView<SimpleStringProperty> list) {
        super(listType, list);
    }

    @Override
    public void addGraphNode(GraphNode node) {
        super.enqueue(node);
    }

    @Override
    public GraphNode removeGraphNode() {
        return super.dequeue();
    }
}
