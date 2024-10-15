package com.marcinseweryn.visualizer.model.path;

import com.marcinseweryn.visualizer.view.GraphNode;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.ListView;

public class GraphNodeStack extends GraphNodeDeque {

    public GraphNodeStack(ViewType listType, ListView<SimpleStringProperty> list) {
        super(listType, list);
    }

    @Override
    public void addNode(GraphNode node) {
        super.push(node);
    }

    @Override
    public GraphNode removeNode() {
        return super.pop();
    }
}
