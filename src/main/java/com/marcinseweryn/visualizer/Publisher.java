package com.marcinseweryn.visualizer;

import com.marcinseweryn.visualizer.view.GraphNode;
import javafx.scene.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Publisher {
    private final Map<String, List<Subscriber>> subscribers = new HashMap<>();

    // Subscribe a subscriber to a specific event
    public void subscribe(String eventType, Subscriber subscriber) {
        this.subscribers.putIfAbsent(eventType, new ArrayList<>());
        this.subscribers.get(eventType).add(subscriber);
    }

    // Unsubscribe a subscriber from a specific event
    public void unsubscribe(String eventType, Subscriber subscriber) {
        List<Subscriber> subscribersOfType = this.subscribers.get(eventType);
        if (subscribersOfType != null) {
            subscribersOfType.remove(subscriber);
        }
    }

    // Notify all subscribers about a specific event
    public void notify(String eventType, Node vertex) {
        List<Subscriber> subscribersOfType = this.subscribers.get(eventType);
        if (subscribersOfType != null) {
            for (Subscriber subscriber : subscribersOfType) {
                subscriber.update(eventType, vertex);
            }
        }
    }
}
