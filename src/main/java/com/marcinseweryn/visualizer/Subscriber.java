package com.marcinseweryn.visualizer;

import com.marcinseweryn.visualizer.view.GraphNode;
import javafx.scene.Node;

public interface Subscriber {
    void update(String eventType, Node vertex);
}