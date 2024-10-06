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
                              ListView<String> pseudocodeList,
                              SimpleObjectProperty<GraphNode> startNode,
                              SimpleObjectProperty<GraphNode> destinationNode) {
        super(candidateNodes, visitedNodes, pseudocodeList, startNode, destinationNode);
    }

    @Override
    public void executeAlgorithm() {
        pauseAtStep(0);

        this.candidateNodeList.addNodeAndVisualize(this.startNode.get());

        pauseAtStep(1);

        this.visitedNodeList.addNode(this.startNode.get());

        while (!candidateNodeList.isEmpty()) {
            pauseAtStep(2);

            setCurrentNode(this.candidateNodeList.removeNode());

            visitedNodeList.addNodeAndApplyStyle(getCurrentNode());

            pauseAtStep(3);
            pauseAtStep(4);

            if (getCurrentNode() == this.destinationNode.get()) {
                resetCurrentNodeStyle();
                reconstructPath(getCurrentNode());
                visualizePath();
                pauseAtStep(5);
                break;
            }

            pauseAtStep(6);
            for (GraphNode neighbour : getCurrentNode().getNeighbors()) {
                setNeighborNode(neighbour);
                pauseAtStep(7);
                if (!visitedNodeList.containsNode(getNeighborNode())) {

                    visitedNodeList.addNode(getNeighborNode());

                    pauseAtStep(8);

                    this.candidateNodeList.addNodeAndVisualize(getNeighborNode());

                    getNeighborNode().setParentNode(getCurrentNode());
                    pauseAtStep(9);
                }
            }

            setNeighborNode(null);
        }
    }

    @Override
    public void setPseudocode() {
        this.pseudocode.addAll(
                "queue.enqueue(start)",                       // Initialize the queue with Start
                "Visited.add(Start)",
                "while (!queue.isEmpty())",                             // Loop until the queue is empty
                "   current = queue.dequeue()",                         // Dequeue the front element
                "   if (current == destination)",                   // Check if Current is the destination
                "      reconstructPath() and return",                       // Trace the path if found
                "   for (N : neighbors(current))",                  // Loop through neighbors of Current
                "      if (N not in visited)",                      // Check if N is unvisited and not in queue
                "         Visited.add(N)",
                "         Q.enqueue(N)",                            // Enqueue N
                "         parent[N] = current"                      // Set Current as the parent of N
        );
    }


    private void reconstructPath(GraphNode current) {
        for (GraphNode node = current; node != null; node = node.getParentNode()) {
            addToPath(node);
        }

    }

    @Override
    public String toString() {
        return "Breadth First Search";
    }

}
