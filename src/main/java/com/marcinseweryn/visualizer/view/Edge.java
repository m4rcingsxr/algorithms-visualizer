package com.marcinseweryn.visualizer.view;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * The Edge class represents an edge between two GraphNodes.
 * It extends the PaneArrow class, which allows it to draw an arrow between two vertices.
 * It also allows visualization of weight and distance, which are displayed as labels along the edge.
 */
public class Edge extends PaneArrow {

    // The two nodes that this edge connects
    private final GraphNode nodeA;
    private final GraphNode nodeB;

    // The weight of the edge (for weighted graphs)
    private final DoubleProperty weight = new SimpleDoubleProperty();


    // Boolean properties to control the visibility of the weight and distance labels
    private final BooleanProperty weightVisible = new SimpleBooleanProperty(true);
    private final BooleanProperty distanceVisible = new SimpleBooleanProperty(false);

    /**
     * Constructs an Edge between two vertices.
     * Automatically binds the arrow's coordinates to the positions of the vertices.
     *
     * @param nodeA The first vertex (start of the edge).
     * @param nodeB The second vertex (end of the edge).
     */
    public Edge(GraphNode nodeA, GraphNode nodeB) {
        super(nodeA.getLayoutX(), nodeA.getLayoutY(), nodeB.getLayoutX(), nodeB.getLayoutY());

        this.nodeA = nodeA;
        this.nodeB = nodeB;

        // Bind the arrow's start and end points to the layout positions of the vertices
        this.x1.bind(nodeA.layoutXProperty());
        this.y1.bind(nodeA.layoutYProperty());
        this.x2.bind(nodeB.layoutXProperty());
        this.y2.bind(nodeB.layoutYProperty());

        // Add style class "weighted" to the main line if the weight is non-zero
        this.weight.addListener((obs, oldVal, newVal) -> {
            if (newVal.doubleValue() != 0) {
                mainLine.getStyleClass().add("weighted");
                headA.getStyleClass().add("weighted");
                headB.getStyleClass().add("weighted");
            } else {
                mainLine.getStyleClass().remove("weighted");
                headA.getStyleClass().remove("weighted");
                headB.getStyleClass().remove("weighted");
            }
        });

        // Label for displaying the distance between the vertices
        Label distanceLabel = new Label();
        distanceLabel.visibleProperty().bind(this.distanceVisible);

        // Label for displaying the weight of the edge
        Label weightLabel = new Label();
        weightLabel.visibleProperty().bind(this.weightVisible);

        // Bind the weight label to display the current weight value
        weightLabel.textProperty().bind(Bindings.concat("w:[", this.weight.asString(), "]"));

        // Distance is calculated as the Euclidean distance between the vertices
        IntegerProperty distance = new SimpleIntegerProperty();
        distance.bind(Bindings.createDoubleBinding(() ->
                                                           Math.sqrt(
                                                                   Math.pow(getY2() - getY1(), 2) +
                                                                           Math.pow(
                                                                                   getX2() - getX1(),
                                                                                   2
                                                                           )
                                                           ),
                                                   y2Property(), y1Property(), x2Property(),
                                                   x1Property()
                      )
        );
        distanceLabel.textProperty().bind(Bindings.concat("d:[", distance.asString(), "]"));

        // Add the weight and distance labels to the content of the PaneArrow
        addContent(new HBox(5, weightLabel, distanceLabel));
    }

    public GraphNode getNeighbour(GraphNode vertex) {
        return vertex == nodeA ? nodeB : nodeA;
    }

    public void setHeadAVisible(GraphNode vertex, boolean b) {
        if (vertex == nodeA) {
            setHeadAVisible(b);
        } else {
            setHeadBVisible(b);
        }
    }

    /**
     * Returns the neighboring vertex connected to the given vertex by this edge.
     * If vertex1 is passed in, it returns vertex2 and vice versa.
     *
     * @param vertex The vertex whose neighbor is needed.
     * @return The neighboring vertex connected by this edge.
     */
    public GraphNode getNeighbor(GraphNode vertex) {
        return vertex == nodeA ? nodeB : nodeA;
    }

    /**
     * Sets the visibility of the arrowhead at one end of the edge based on the given vertex.
     *
     * @param vertex The vertex at which to toggle the arrowhead visibility.
     * @param visible True to make the arrowhead visible, false to hide it.
     */
    public void setArrowHeadVisible(GraphNode vertex, boolean visible) {
        if (vertex == nodeA) {
            setHeadAVisible(visible);
        } else {
            setHeadBVisible(visible);
        }
    }

    /**
     * Checks if the arrowhead at the given vertex is visible.
     *
     * @param vertex The vertex at which to check the arrowhead visibility.
     * @return True if the arrowhead is visible, false otherwise.
     */
    public boolean isArrowHeadVisible(GraphNode vertex) {
        return vertex == this.nodeA ? isHeadAVisible() : isHeadBVisible();
    }

    public boolean isOppositeArrowHeadVisible(GraphNode vertex) {
        return vertex == this.nodeA ? isHeadBVisible() : isHeadAVisible();
    }

    /**
     * Gets the property for the weight of the edge, allowing for binding and updating.
     *
     * @return The DoubleProperty representing the weight of the edge.
     */
    public DoubleProperty weightProperty() {
        return this.weight;
    }

    /**
     * Sets the weight of the edge.
     *
     * @param weight The new weight value.
     */
    public void setWeight(double weight) {
        this.weight.set(weight);
    }

    /**
     * Gets the current weight of the edge.
     *
     * @return The current weight value.
     */
    public double getWeight() {
        return this.weight.getValue();
    }

    /**
     * Checks if the weight label is visible.
     *
     * @return True if the weight label is visible, false otherwise.
     */
    public boolean isWeightVisible() {
        return weightVisible.get();
    }

    /**
     * Gets the property for controlling the visibility of the weight label.
     *
     * @return The BooleanProperty for the visibility of the weight label.
     */
    public BooleanProperty weightVisibleProperty() {
        return weightVisible;
    }

    /**
     * Gets the property for controlling the visibility of the distance label.
     *
     * @return The BooleanProperty for the visibility of the distance label.
     */
    public BooleanProperty distanceVisibleProperty() {
        return distanceVisible;
    }

    public GraphNode getNodeA() {
        return this.nodeA;
    }

    public GraphNode getNodeB() {
        return this.nodeB;
    }

}

