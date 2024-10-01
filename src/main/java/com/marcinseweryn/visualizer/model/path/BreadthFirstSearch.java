package com.marcinseweryn.visualizer.model.path;

import com.marcinseweryn.visualizer.GraphAlgorithm;
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
        ArrayDeque<GraphNode> queue = new ArrayDeque<>();
        boolean[] visited = new boolean[100]; // temp
        queue.add(this.startVertex);

        step(1);
        while(!queue.isEmpty()) {
            step(2);
            GraphNode current = queue.remove();
            step(3);
            step(4);

            if(current == this.destinationVertex) {
                visited[Integer.parseInt(current.getText())] = true;

                reconstructPath(current);
                drawPath();
                step(5);
                break;
            }

            visited[Integer.parseInt(current.getText())] = true;
            step(6);
            step(7);
            for (GraphNode neighbour : current.getNeighbours()) {
                step(8);
                if(!visited[Integer.parseInt(neighbour.getText())]) {
                    visited[Integer.parseInt(neighbour.getText())] = true;
                    queue.add(neighbour);
                    step(9);
                    neighbour.setParentNode(current);
                    step(10);
                }
            }
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
