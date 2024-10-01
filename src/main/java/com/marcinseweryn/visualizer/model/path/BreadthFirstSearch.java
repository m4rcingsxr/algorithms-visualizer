package com.marcinseweryn.visualizer.model.path;

import com.marcinseweryn.visualizer.GraphAlgorithm;
import com.marcinseweryn.visualizer.NodeVisualizer;
import com.marcinseweryn.visualizer.Publisher;
import com.marcinseweryn.visualizer.view.GraphNode;

import java.util.ArrayDeque;

public class BreadthFirstSearch extends GraphAlgorithm {

    public BreadthFirstSearch() {}

    protected BreadthFirstSearch(Publisher publisher) {
        super(publisher);
    }

    @Override
    public void resolve() {
        this.pendingNodes.addVertex(this.startVertex);

        step(1);
        while(!pendingNodes.isEmpty()) {
            step(2);
            setCurrent(this.pendingNodes.removeVertex());
            step(3);
            step(4);

            if(getCurrent() == this.destinationVertex) {
                visited.addVertex(getCurrent());

                reconstructPath(getCurrent());
                drawPath();
                step(5);
                break;
            }

            visited.addVertex(getCurrent());
            step(6);
            step(7);
            for (GraphNode neighbour : getCurrent().getNeighbours()) {
                setNeighbour(neighbour);
                step(8);
                if(!visited.containsNode(getNeighbour())) {
                    visited.addVertex(getNeighbour());
                    pendingNodes.addVertex(getNeighbour());
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
        for (GraphNode node = current; node != null ; node = node.getParentVertex()) {
            addToPath(node);
        }

    }
}
