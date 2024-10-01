package com.marcinseweryn.visualizer;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.AnchorPane;

public class SortingController {

    private final AnchorPane arrayPane;

    public SortingController(AnchorPane arrayPane) {
        this.arrayPane = arrayPane;
    }

    public void runAlgorithm(SimpleObjectProperty<Thread> resolveThread, GraphAlgorithm algorithm) {
    }
}
