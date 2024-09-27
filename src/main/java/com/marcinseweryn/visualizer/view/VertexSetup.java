package com.marcinseweryn.visualizer.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class VertexSetup extends VBox {

    @FXML HBox vertexContainer;

    private final GraphNode vertex;

    private boolean initialized = false;

    public VertexSetup(GraphNode vertex) {
        this.vertex = vertex;
        // Load the FXML file in the constructor
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/VertexSetup.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void update() {

        if(!initialized) {
            this.init();
            this.initialized = true;
        }

        System.out.println("Updating selected vertex setup");
    }

    public void init() {
        vertexContainer.getChildren().add(new GraphNode(vertex));
    }
}
