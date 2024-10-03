package com.marcinseweryn.visualizer.view;

import com.marcinseweryn.visualizer.Publisher;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
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

    @FXML Spinner<Double> x;
    @FXML Spinner<Double> y;

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

        SpinnerValueFactory<Double> xValueFactory =
                new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, Double.MAX_VALUE, 0.0, 1); // min, max, initial value, step
        SpinnerValueFactory<Double> yValueFactory =
                new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, Double.MAX_VALUE, 0.0, 1); // min, max, initial value, step
        x.setValueFactory(xValueFactory);
        y.setValueFactory(yValueFactory);
        x.setEditable(true);
        y.setEditable(true);
        Bindings.bindBidirectional(x.getValueFactory().valueProperty(), vertex.layoutXProperty().asObject());
        Bindings.bindBidirectional(y.getValueFactory().valueProperty(), vertex.layoutYProperty().asObject());
    }


    public void update() {

        if(!initialized) {
            this.init();
            this.initialized = true;
        }

        edgeContainer.getChildren().clear();

        for (Edge e : vertex.getAllEdges()) {

            // check if current vertex layout is less than the neighbor (of same edge)
            boolean isCurrentVertexMoreToTheLeft = vertex.getLayoutX() <= e.getNeighbor(vertex).getLayoutX();
            GraphNode left = isCurrentVertexMoreToTheLeft ? vertex : e.getNeighbor(vertex);
            GraphNode right = !isCurrentVertexMoreToTheLeft ? vertex : e.getNeighbor(vertex);

            Region space = new Region();
            HBox.setHgrow(space, Priority.ALWAYS);

            HBox edge = new HBox(10);
            edge.setAlignment(Pos.CENTER);

            HBox hBox = new HBox();
            hBox.getChildren().addAll(
                    createArrowDirectionChanger("<", e, left),
                    createWeightChanger(e),
                    createArrowDirectionChanger(">", e, right)
            );

            edge.getChildren().addAll(
                    new GraphNode(left),
                    hBox,
                    new GraphNode(right),
                    space,
                    createArrowRemoveBtn(e, this.vertex)
            );

            this.edgeContainer.getChildren().add(edge);
        }
    }

    private Node createArrowRemoveBtn(Edge edge, GraphNode vertex) {
        Button btn = new Button("X");
        btn.getStyleClass().add("delete-edge-btn");
        btn.setOnAction(e -> {
            vertex.getAllEdges().remove(edge);
            edge.getNeighbor(vertex).getAllEdges().remove(edge);
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
        btn.getStyleClass().add("arrowhead-edge-btn");
        btn.setPrefWidth(50);
        btn.setText(edge.isArrowHeadVisible(node) ? text : "  ");

        btn.setOnAction(e -> {
            edge.setHeadAVisible(node, !edge.isArrowHeadVisible(node));
            btn.setText(edge.isArrowHeadVisible(node) ? text : "  ");
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
