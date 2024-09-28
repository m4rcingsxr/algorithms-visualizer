package com.marcinseweryn.visualizer.view;

import com.marcinseweryn.visualizer.Publisher;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class VertexSetup extends VBox {

    @FXML Button destinationBtn;
    @FXML Button startBtn;

    @FXML HBox vertexContainer;
    @FXML VBox edgeContainer;

    private final GraphNode vertex;
    private final Publisher publisher;

    private boolean initialized = false;

    public VertexSetup(GraphNode vertex, Publisher publisher) {
        this.vertex = vertex;
        this.publisher = publisher;

        // Load the FXML file in the constructor
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/VertexSetup.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        startBtn.setOnAction(e -> notifyStartButtonClicked());
        destinationBtn.setOnAction(e -> notifyDestinationButtonClicked());
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

            Region space = new Region();
            HBox.setHgrow(space, Priority.ALWAYS);

            HBox edge = new HBox();
            edge.getChildren().addAll(
                    new GraphNode(left),
                    createArrowDirectionChanger("<", e, left),
                    createWeightChanger(e),
                    createArrowDirectionChanger(">", e, right),
                    new GraphNode(right),
                    space,
                    createArrowRemoveBtn(e, this.vertex)
            );

            this.edgeContainer.getChildren().add(edge);
        }

        System.out.println("Updating selected vertex setup");
    }

    private Node createArrowRemoveBtn(Edge edge, GraphNode vertex) {
        Button btn = new Button("Remove");
        btn.setMaxWidth(10);
        btn.setOnAction(e -> {
            vertex.getEdges().remove(edge);
            edge.getNeighbour(vertex).getEdges().remove(edge);
            publisher.notify("removeEdge", edge);
            update();
        });

        return btn;
    }

    private void notifyStartButtonClicked() {
        publisher.notify("startClicked", vertex);
    }

    private void notifyDestinationButtonClicked() {
        publisher.notify("destinationClicked", vertex);
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

    private Spinner<Double> createWeightChanger(Edge edge) {
        // Create a spinner for double values with a range (min, max, step)
        Spinner<Double> spinner = new Spinner<>(0.0, 1000.0, edge.getWeight(), 0.1); // Adjust min, max, step as needed

        // Enable typing and number control through Spinner's text field
        spinner.setEditable(true);

        // Bind the Spinner's value to the edge's weightProperty
        spinner.getValueFactory().valueProperty().bindBidirectional(edge.weightProperty().asObject());

        return spinner;
    }

}
