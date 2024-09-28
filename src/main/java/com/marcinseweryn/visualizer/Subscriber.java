package com.marcinseweryn.visualizer;

import com.marcinseweryn.visualizer.view.GraphNode;

public interface Subscriber {
    void update(String eventType, GraphNode vertex);
}