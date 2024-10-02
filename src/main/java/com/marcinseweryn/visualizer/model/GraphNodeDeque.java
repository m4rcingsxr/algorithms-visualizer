package com.marcinseweryn.visualizer.model;

import com.marcinseweryn.visualizer.view.GraphNode;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.ListView;

import java.util.ArrayDeque;

public abstract class GraphNodeDeque extends GraphNodeVisualizer {

    private final ArrayDeque<GraphNode> pendingNodes = new ArrayDeque<>();

    GraphNodeDeque(ListType listType, ListView<SimpleStringProperty> list) {
        super(listType, list);
    }

    void push(GraphNode node, boolean applyStyle) {
        pendingNodes.push(node);
        super.visualizeAdd(node, applyStyle);
    }

    GraphNode pop() {
        GraphNode node = pendingNodes.pop();
        super.visualizeAdd(node, true);
        return node;
    }

    void enqueue(GraphNode node, boolean applyStyle) {
        pendingNodes.add(node);
        super.visualizeAdd(node, applyStyle);
    }

    GraphNode dequeue() {
        GraphNode node = pendingNodes.remove();
        super.visualizeRemove(node);
        return node;
    }

    public boolean containsGraphNode(GraphNode node) {
        return pendingNodes.contains(node);
    }

    public boolean isEmpty() {
        return pendingNodes.isEmpty();
    }

}
