package com.marcinseweryn.visualizer.model.path;

import com.marcinseweryn.visualizer.model.GraphAlgorithm;
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
    public void executeAlgorithm() {
        this.candidateNodeList.addGraphNode(this.startNode.get());

        pauseAtStep(0);
        while (!candidateNodeList.isEmpty()) {
            pauseAtStep(1);
            setCurrentNode(this.candidateNodeList.removeGraphNode());
            pauseAtStep(2);
            pauseAtStep(3);

            if (getCurrentNode() == this.destinationNode.get()) {
                visitedNodeList.addGraphNode(getCurrentNode());

                reconstructPath(getCurrentNode());
                visualizePath();
                pauseAtStep(4);
                break;
            }

            visitedNodeList.addGraphNode(getCurrentNode());
            pauseAtStep(5);
            pauseAtStep(6);
            for (GraphNode neighbour : getCurrentNode().getNeighbours()) {
                setNeighborNode(neighbour);
                pauseAtStep(7);
                if (!visitedNodeList.containsGraphNode(getNeighborNode())) {
                    visitedNodeList.addGraphNode(getNeighborNode());
                    candidateNodeList.addGraphNode(getNeighborNode());
                    pauseAtStep(8);
                    getNeighborNode().setParentNode(getCurrentNode());
                    pauseAtStep(9);
                }
            }

            setNeighborNode(null);
        }
    }

    private void reconstructPath(GraphNode current) {
        for (GraphNode node = current; node != null; node = node.getParentVertex()) {
            addToPath(node);
        }

    }

    @Override
    public String toString() {
        return "Breadth First Search";
    }

}
