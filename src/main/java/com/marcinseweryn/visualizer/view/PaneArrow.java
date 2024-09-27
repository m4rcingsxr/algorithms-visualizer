package com.marcinseweryn.visualizer.view;

import javafx.scene.Node;
import javafx.scene.layout.Pane;

public class PaneArrow extends Arrow {

    private final Pane content = new Pane();

    public PaneArrow(double x1, double y1, double x2, double y2) {
        super(x1, y1, x2, y2);

        getChildren().addAll(content);

        content.layoutXProperty().bind(x2Property().add(x1Property()).divide(2).subtract(content.widthProperty().divide(2)));
        content.layoutYProperty().bind(y2Property().add(y1Property()).divide(2).subtract(content.heightProperty().divide(2)));
    }

    public void addContent(Node content) {
        this.content.getChildren().setAll(content);
    }

}
