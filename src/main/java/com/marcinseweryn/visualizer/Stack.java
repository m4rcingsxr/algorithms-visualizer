package com.marcinseweryn.visualizer;

import com.marcinseweryn.visualizer.view.GraphNode;

public class Stack extends Deque {

    public Stack( int listType) {
        super( listType);
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
