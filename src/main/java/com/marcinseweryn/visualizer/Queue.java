package com.marcinseweryn.visualizer;

import com.marcinseweryn.visualizer.view.GraphNode;

public class Queue extends Deque {

    Queue(int listType) {
        super(listType);
    }

    @Override
    public void addVertex(GraphNode node) {
        super.enqueue(node);
    }

    @Override
    public GraphNode removeVertex() {
        return super.dequeue();
    }
}
