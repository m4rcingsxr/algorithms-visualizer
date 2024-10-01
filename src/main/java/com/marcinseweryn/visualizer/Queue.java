package com.marcinseweryn.visualizer;

import com.marcinseweryn.visualizer.view.GraphNode;
import javafx.scene.control.ListView;

public class Queue extends Deque {

    Queue(int listType, ListView candidateNodes) {
        super(listType, candidateNodes);
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
