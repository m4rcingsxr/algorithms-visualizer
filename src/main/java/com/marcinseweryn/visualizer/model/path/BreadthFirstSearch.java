package com.marcinseweryn.visualizer.model.path;

import com.marcinseweryn.visualizer.GraphAlgorithm;
import com.marcinseweryn.visualizer.view.GraphNode;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.ListView;

public class BreadthFirstSearch extends GraphAlgorithm {

    public BreadthFirstSearch() {
        super();
    }

    public BreadthFirstSearch(ListView<SimpleStringProperty> candidateNodes,
                              ListView<SimpleStringProperty> visitedNodes,
                              SimpleObjectProperty<GraphNode> startNode,
                              SimpleObjectProperty<GraphNode> destinationNode) {
        super(candidateNodes, visitedNodes, startNode, destinationNode);
    }

    @Override
    public void resolve() {
        this.candidateNodes.addVertex(this.startNode.get());

        step(1);
        while (!candidateNodes.isEmpty()) {
            step(2);
            setCurrent(this.candidateNodes.removeVertex());
            step(3);
            step(4);

            if (getCurrent() == this.destinationNode.get()) {
                visitedNodes.addVertex(getCurrent());

                reconstructPath(getCurrent());
                drawPath();
                step(5);
                break;
            }

            visitedNodes.addVertex(getCurrent());
            step(6);
            step(7);
            for (GraphNode neighbour : getCurrent().getNeighbours()) {
                setNeighbour(neighbour);
                step(8);
                if (!visitedNodes.containsNode(getNeighbour())) {
                    visitedNodes.addVertex(getNeighbour());
                    candidateNodes.addVertex(getNeighbour());
                    step(9);
                    getNeighbour().setParentNode(getCurrent());
                    step(10);
                }
            }
            setNeighbour(null);
        }
    }

    @Override
    public String toString() {
        return "Breadth First Search";
    }

    private void reconstructPath(GraphNode current) {
        for (GraphNode node = current; node != null; node = node.getParentVertex()) {
            addToPath(node);
        }

    }
}
