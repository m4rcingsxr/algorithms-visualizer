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

            pauseAtStep(5);
            visitedNodeList.addGraphNode(getCurrentNode());
            pauseAtStep(6);
            pauseAtStep(7);
            for (GraphNode neighbour : getCurrentNode().getNeighbours()) {
                setNeighborNode(neighbour);
                pauseAtStep(8);
                if (!visitedNodeList.containsGraphNode(getNeighborNode())) {
                    visitedNodeList.addGraphNode(getNeighborNode());
                    candidateNodeList.addGraphNode(getNeighborNode());
                    pauseAtStep(9);
                    getNeighborNode().setParentNode(getCurrentNode());
                    pauseAtStep(10);
                }
            }

            setNeighborNode(null);
        }
    }

    @Override
    public void setPseudocode() {
        this.pseudocode.addAll(
                "Q.enqueue(Start)",                       // Initialize the queue with Start
                "while (!Q.isEmpty())",                             // Loop until the queue is empty
                "   Current = Q.dequeue()",                         // Dequeue the front element
                "   if (Current == Destination)",                   // Check if Current is the destination
                "      tracePath() and exit",                       // Trace the path if found
                "   Visited.add(Current)",                          // Mark Current as visited
                "   for (N : neighbors(Current))",                  // Loop through neighbors of Current
                "      if (N not in Visited)",                      // Check if N is unvisited and not in queue
                "         Visited.add(N)",
                "         Q.enqueue(N)",                            // Enqueue N
                "         parent[N] = Current"                      // Set Current as the parent of N
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
