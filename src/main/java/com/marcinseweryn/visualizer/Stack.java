package com.marcinseweryn.visualizer;

import com.marcinseweryn.visualizer.view.GraphNode;
import javafx.scene.control.ListView;

public class Stack extends Deque {

    public Stack(ListType listType, ListView candidateNodes, ListView visitedNodes) {
        super(listType, candidateNodes, visitedNodes);
    }

    @Override
    public void addVertex(GraphNode node) {
        super.push(node);
    }

    @Override
    public GraphNode removeVertex() {
        return super.pop();
    }
}
