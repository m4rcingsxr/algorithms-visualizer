package com.marcinseweryn.visualizer.model.path;

import com.marcinseweryn.visualizer.GraphAlgorithm;
import com.marcinseweryn.visualizer.Publisher;
import com.marcinseweryn.visualizer.view.GraphNode;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.ListView;

public class DepthFirstSearch extends GraphAlgorithm {

    public DepthFirstSearch() {
        super();
    }

    public DepthFirstSearch(ListView<SimpleStringProperty> candidateNodes,
                            ListView<SimpleStringProperty> visitedNodes,
                            SimpleObjectProperty<GraphNode> startNode,
                            SimpleObjectProperty<GraphNode> destinationNode) {
        super(candidateNodes, visitedNodes, startNode, destinationNode);
    }

    @Override
    public String toString() {
        return "Depth First Search";
    }

    @Override
    public void resolve() {

    }
}
