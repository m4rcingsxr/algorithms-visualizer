package com.marcinseweryn.visualizer.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class VertexSetup extends VBox {

    @FXML HBox vertexContainer;
    @FXML VBox edgeContainer;

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

        edgeContainer.getChildren().clear();

        for (Edge e : vertex.getEdges()) {

            // check if current vertex layout is less than the neighbor (of same edge)
            boolean lewak = vertex.getLayoutX() <= e.getNeighbour(vertex).getLayoutX();
            GraphNode left = lewak ? vertex : e.getNeighbour(vertex);
            GraphNode right = !lewak ? vertex : e.getNeighbour(vertex);

            HBox edge = new HBox();
            edge.getChildren().addAll(
                    new GraphNode(left),
                    createArrowDirectionChanger("<", e, left),
                    createArrowDirectionChanger(">", e, right),
                    new GraphNode(right)
            );

            this.edgeContainer.getChildren().add(edge);
        }

        System.out.println("Updating selected vertex setup");
    }

    public void init() {
        vertexContainer.getChildren().add(new GraphNode(vertex));
    }

    private Button createArrowDirectionChanger(String text, Edge edge, GraphNode node) {
        Button btn = new Button();
        btn.setPrefWidth(50);
        btn.setText(edge.isHeadVisible(node) ? text : "");

        btn.setOnAction(e -> {
            edge.setHeadAVisible(node, !edge.isHeadVisible(node));
            btn.setText(edge.isHeadVisible(node) ? text : "");
        });

        return btn;
    }

}
