package com.marcinseweryn.visualizer;

import com.marcinseweryn.visualizer.view.GraphNode;

import java.util.ArrayDeque;

public abstract class Deque extends NodeVisualizer {

    private final ArrayDeque<GraphNode> pendingNodes = new ArrayDeque<>();

    Deque( int listType) {
        super(listType);
    }

    void push(GraphNode node) {
        pendingNodes.push(node);
        super.visualizeAdd(node);
    }

    GraphNode pop() {
        GraphNode node = pendingNodes.pop();
        super.visualizeAdd(node);
        return node;
    }

    void enqueue(GraphNode node) {
        pendingNodes.add(node);
        super.visualizeAdd(node);
    }

    GraphNode dequeue() {
        GraphNode node = pendingNodes.remove();
        super.visualizeRemove(node);
        return node;
    }

    public boolean containsNode(GraphNode node) {
        return pendingNodes.contains(node);
    }

    public boolean isEmpty() {
        return pendingNodes.isEmpty();
    }

}
