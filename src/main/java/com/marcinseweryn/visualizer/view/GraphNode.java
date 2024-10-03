package com.marcinseweryn.visualizer.view;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Represents a node (vertex) in a graph. The node is visualized as a button and
 * can be connected to other nodes through edges. Each node has an ID, position,
 * and a parent node for traversal algorithms (like DFS, BFS).
 */
public class GraphNode extends Button {

    private static final Logger logger = LogManager.getLogger(GraphNode.class);

    // Static counter to generate unique IDs for each node
    private static int count = 0;

    // List of edges connecting this node to other nodes
    private final ObservableList<Edge> edges = FXCollections.observableArrayList();

    // The parent node in graph traversal algorithms
    private final SimpleObjectProperty<GraphNode> parent = new SimpleObjectProperty<>(null);

    // Holds information about the node (ID, parent)
    private final SimpleStringProperty info = new GraphNodeInfo();

    public GraphNode() {
        getStyleClass().add("vertex");
        setText("   ");
    }

    public GraphNode(String styleClass) {
        this();
        setText("   ");
        this.getStyleClass().add(styleClass);
    }

    /**
     * Creates a new GraphNode at the specified coordinates (x, y).
     *
     * @param x The x-coordinate for the node.
     * @param y The y-coordinate for the node.
     */
    public GraphNode(double x, double y) {
        this();
        // Set a unique ID for the node based on the counter
        this.setId(String.valueOf(count++));
        logger.debug("GraphNode created with ID {} at coordinates: [X: {}, Y: {}]", this.getId(), x, y);

        // Bind the text property to the node's ID so that the ID is displayed as the label
        this.textProperty().bind(this.idProperty());
        logger.debug("GraphNode ID {} bound to textProperty.", this.getId());

        // Center the node on the mouse click by adjusting translation properties
        this.translateXProperty().bind(widthProperty().divide(-2));
        this.translateYProperty().bind(heightProperty().divide(-2));
        logger.debug("GraphNode ID {} translation properties set (centered on mouse click).", this.getId());

        // Set the layout (position) of the node
        this.setLayoutX(x);
        this.setLayoutY(y);
        logger.debug("GraphNode ID {} positioned at coordinates: [X: {}, Y: {}]", this.getId(), x, y);

        // Bind the info property to reflect the node's parent
        info.bind(Bindings.concat(parent));
    }

    /**
     * Copy constructor to create a GraphNode based on an existing node.
     * Used for creating a visual copy of a node in a different context (e.g., Accordion).
     *
     * @param vertex The node to copy.
     */
    public GraphNode(GraphNode vertex) {
        textProperty().bind(vertex.textProperty());
        getStyleClass().setAll(vertex.getStyleClass());
    }

    public static void decrementCount() {
        count--;
    }

    public static void setCount(int i) {
        count = i;
    }

    /**
     * Adds an edge to the node, connecting it to another node.
     *
     * @param edge The edge to add.
     */
    public void addEdge(Edge edge) {
        this.edges.add(edge);
    }


    /**
     * Gets the list of edges connected to this node.
     *
     * @return A list of edges.
     */
    public List<Edge> getAllEdges() {
        return this.edges;
    }
    
    /**
     * Returns a list of neighbor nodes connected to this node via edges.
     *
     * @return A list of neighboring GraphNodes.
     */
    public List<GraphNode> getNeighbours() {
        return this.edges.stream().filter(e -> e.isOppositeArrowHeadVisible(this)).map(e -> e.getNeighbor(this)).toList();
    }

    /**
     * Removes an edge from the node.
     *
     * @param edge The edge to remove.
     */
    public void removeEdge(Edge edge) {
        this.edges.remove(edge);
    }

    /**
     * Returns the parent node in the traversal process (e.g., DFS or BFS).
     *
     * @return The parent node.
     */
    public GraphNode getParentNode() {
        return parent.getValue();
    }


    /**
     * Sets the parent node in the traversal process (e.g., DFS or BFS).
     *
     * @param parentVertex The new parent node.
     */
    public void setParentNode(GraphNode parentVertex) {
        this.parent.setValue(parentVertex);
    }

    /**
     * Finds the edge connecting this node to a given neighboring node.
     *
     * @param neighbour The neighboring node.
     * @return The edge connecting this node to the neighbor, or null if none exists.
     */
    public Edge getConnection(GraphNode neighbour) {
        return edges.stream().filter(edge -> edge.getNeighbor(this) == neighbour).findFirst().orElse(null);
    }


    /**
     * Returns the information property (info) for the node.
     *
     * @return The info property (SimpleStringProperty).
     */
    public SimpleStringProperty getInfo() {
        return info;
    }

    /**
     * Clears all applied styles from the node, resetting it to its base appearance.
     */
    public void clearStyle() {
        this.getStyleClass().removeAll("start", "destination", "visited", "candidate-nodes", "path");
    }

    public void setPrimaryClass(String clazz) {
        this.getStyleClass().removeAll("start", "destination", "visited", "candidate-nodes", "path");
        this.getStyleClass().add(clazz);
    }


    /**
     * Inner class that overrides SimpleStringProperty to provide a detailed
     * string representation of the GraphNode and bind it on other properties.
     */
    private class GraphNodeInfo extends SimpleStringProperty {

        @Override
        public String toString() {
            // Return the node's ID and the parent's ID (or null if no parent)
            return "ID: " + getId() + " | Parent: " +
                    (parent.getValue() != null ? parent.getValue().getId() : "null");
        }
    }
}
