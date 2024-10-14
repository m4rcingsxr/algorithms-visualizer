package com.marcinseweryn.visualizer.model.path;

import com.marcinseweryn.visualizer.model.DataStructureType;
import com.marcinseweryn.visualizer.model.GraphAlgorithm;
import com.marcinseweryn.visualizer.model.GraphNodeVisualizer;
import com.marcinseweryn.visualizer.view.GraphNode;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class DepthFirstSearch extends GraphAlgorithm {

    private GraphNodeVisualizer candidateNodeList;
    private GraphNodeVisualizer visitedNodeList;

    public DepthFirstSearch() {
        super();
    }

    public DepthFirstSearch(VBox algorithmTab,
                            ListView<String> pseudocodeList, SimpleObjectProperty<GraphNode> startNode,
                            SimpleObjectProperty<GraphNode> destinationNode, AnchorPane algorithmSpace) {
        super(algorithmTab, pseudocodeList, startNode, destinationNode, algorithmSpace);
        candidateNodeList = initializeGraphNodeVisualizer("CANDIDATE", DataStructureType.STACK);
        visitedNodeList = initializeGraphNodeVisualizer("VISITED", DataStructureType.STACK);
    }

    @Override
    public String toString() {
        return "Depth First Search";
    }

    @Override
    public void executeAlgorithm() {
        pauseAtStep(0);
        candidateNodeList.addNodeAndVisualize(this.startNode.get());

        pauseAtStep(1);
        while (!candidateNodeList.isEmpty()) {
            pauseAtStep(2);
            setCurrentNode(candidateNodeList.removeNode());

            pauseAtStep(3);
            if(getCurrentNode() == this.destinationNode.get()) {
                reconstructPath(getCurrentNode());
                visualizePath();
                pauseAtStep(4);
                break;
            }

            pauseAtStep(5);
            if(!visitedNodeList.containsNode(getCurrentNode())) {
                pauseAtStep(6);
                visitedNodeList.addNodeAndVisualize(getCurrentNode());

                pauseAtStep(7);
                for (GraphNode neighbour : getCurrentNode().getNeighbors()) {
                    setNeighborNode(neighbour);
                    pauseAtStep(8);
                    if(!visitedNodeList.containsNode(neighbour)) {
                        pauseAtStep(9);
                        candidateNodeList.addNodeAndVisualize(neighbour);
                        pauseAtStep(10);
                        getNeighborNode().setParentNode(getCurrentNode());
                    }
                    setNeighborNode(null);
                }

            }
        }

    }

    @Override
    public void setPseudocode() {
        this.pseudocode.setAll(
               "stack.push(start)",
               "while (!stack.isEmpty())",
               "    current = stack.pop()",
               "    if (current == destination)",
               "        reconstructPath() and return",
               "    if (current not in visited)",
               "        visited.add(current)",
               "            for (N : neighbors(current))",
               "                if (N not in visited)",
               "                    stack.push(N)",
               "                    parent[N] = current"
        );
    }

    // Method to trace the path from destination to start
    private void reconstructPath(GraphNode current) {
        for (GraphNode node = current; node != null; node = node.getParentNode()) {
            addToPath(node); // Add each node in the path to be visualized
        }
    }
}
