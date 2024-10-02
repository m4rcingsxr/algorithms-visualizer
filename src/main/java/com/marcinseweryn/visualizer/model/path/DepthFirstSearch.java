package com.marcinseweryn.visualizer.model.path;

import com.marcinseweryn.visualizer.model.GraphAlgorithm;
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
                            ListView<String> pseudocodeList,
                            SimpleObjectProperty<GraphNode> startNode,
                            SimpleObjectProperty<GraphNode> destinationNode) {
        super(candidateNodes, visitedNodes, pseudocodeList, startNode, destinationNode);
    }

    @Override
    public String toString() {
        return "Depth First Search";
    }

    @Override
    public void executeAlgorithm() {

    }

    @Override
    public void setPseudocode() {

    }
}
