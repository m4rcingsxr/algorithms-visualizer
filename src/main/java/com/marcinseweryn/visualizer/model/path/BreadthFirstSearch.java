package com.marcinseweryn.visualizer.model.path;

import com.marcinseweryn.visualizer.GraphAlgorithm;
import com.marcinseweryn.visualizer.Publisher;
import com.marcinseweryn.visualizer.view.GraphNode;

public class BreadthFirstSearch extends GraphAlgorithm {

    public BreadthFirstSearch() {}

    protected BreadthFirstSearch(Publisher publisher) {
        super(publisher);
    }

    @Override
    public void resolve() {
        this.candidateNodes.addVertex(this.startVertex);

        step(1);
        while(!candidateNodes.isEmpty()) {
            step(2);
            setCurrent(this.candidateNodes.removeVertex());
            step(3);
            step(4);

            if(getCurrent() == this.destinationVertex) {
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
                if(!visitedNodes.containsNode(getNeighbour())) {
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
        for (GraphNode node = current; node != null ; node = node.getParentVertex()) {
            addToPath(node);
        }

    }
}
