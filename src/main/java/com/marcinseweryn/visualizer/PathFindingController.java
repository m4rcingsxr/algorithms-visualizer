package com.marcinseweryn.visualizer;

import com.marcinseweryn.visualizer.view.Edge;
import com.marcinseweryn.visualizer.view.GraphNode;
import com.marcinseweryn.visualizer.view.VertexSetup;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PathFindingController implements Subscriber {

    private static final Logger logger = LogManager.getLogger(PathFindingController.class);

    // main view
    private final AnchorPane graphPane;
    private final Accordion vertexList;
    private final ToggleButton toggleWeight;
    private final ToggleButton toggleDistance;

    // internal
    private final Publisher publisher;

    private GraphNode vertex1;
    private GraphNode vertex2;

    private boolean deleteNode;
    private GraphNode tempVertex;
    private Edge edge;

    private final SimpleObjectProperty<GraphNode> startVertex = new SimpleObjectProperty<>(null);
    private final SimpleObjectProperty<GraphNode> destinationVertex = new SimpleObjectProperty<>(
            null);

    public PathFindingController(AnchorPane graphPane, Accordion vertexList,
                                 ToggleButton toggleWeight, ToggleButton toggleDistance) {
        this.graphPane = graphPane;
        this.vertexList = vertexList;
        this.toggleWeight = toggleWeight;
        this.toggleDistance = toggleDistance;

        this.publisher = new Publisher();

        this.publisher.subscribe("startClicked", this);
        this.publisher.subscribe("destinationClicked", this);
        this.publisher.subscribe("removeEdge", this);

        logger.info("PathFindingController initialized with graphPane.");
    }

    void onGraphMousePressed(MouseEvent mouseEvent) {
        logger.debug("Mouse pressed at coordinates: [X: {}, Y: {}]", mouseEvent.getX(),
                     mouseEvent.getY()
        );

        if (mouseEvent.isPrimaryButtonDown()) {
            this.vertex1 = createAndAddVertex(mouseEvent.getX(), mouseEvent.getY());
        }
    }

    void onGraphPaneMouseReleased(MouseEvent mouseEvent) {
        logger.debug("Mouse released at coordinates: [X: {}, Y: {}]", mouseEvent.getX(),
                     mouseEvent.getY()
        );

        if (this.tempVertex != null) {
            this.vertex1.removeEdge(this.edge);
            this.graphPane.getChildren().removeAll(this.vertex2, this.edge);

            // remove additional candidate as new vertex from accordion
            this.removeVertexFromAccordion(this.vertex2);

            GraphNode.decrementCount();
            this.vertex2 = tempVertex;

            // no need to check if already exist because we cannot connect it
            // multiple times from graph pane mouse released
            this.edge = createAndAddEdge(this.vertex1, this.vertex2);
        }

        if (this.vertex2 != null) {
            this.vertex2.getStyleClass().remove("drag");
            this.edge.getStyleClass().remove("drag");
            this.vertex2 = null;
        }
    }

    void onGraphPaneDragDetected(MouseEvent mouseEvent) {
        logger.debug("Drag detected at coordinates: [X: {}, Y: {}]", mouseEvent.getX(),
                     mouseEvent.getY()
        );
        if (mouseEvent.isPrimaryButtonDown()) {

            // here's position is translated to the cursor (initialization step)
            this.vertex2 = createAndAddVertex(mouseEvent.getX(), mouseEvent.getY());

            this.vertex2.startFullDrag();

            this.vertex2.toBack();

            // required to hold it - mouse release must remove this edge if vertex entered other one
            // that's different from vertex1 and vertex2 -  create network from vertex1
            // to already existing vertex`
            this.edge = createAndAddEdge(this.vertex1, this.vertex2);

            this.vertex2.getStyleClass().add("drag");
            this.edge.getStyleClass().add("drag");
        }
    }

    private Edge createAndAddEdge(GraphNode vertex1, GraphNode vertex2) {
        Edge temp = new Edge(this.vertex1, this.vertex2);
        temp.weightVisibleProperty().bind(toggleWeight.selectedProperty());
        temp.distanceVisibleProperty().bind(toggleDistance.selectedProperty());

        vertex1.addEdge(temp);
        vertex2.addEdge(temp);

        this.graphPane.getChildren().add(temp);
        return temp;
    }

    void onGraphPaneMouseDragged(MouseEvent mouseEvent) {
        logger.debug("Mouse dragged to coordinates: [X: {}, Y: {}]", mouseEvent.getX(),
                     mouseEvent.getY()
        );

        if (this.vertex2 != null) {
            this.vertex2.setLayoutX(mouseEvent.getX());
            this.vertex2.setLayoutY(mouseEvent.getY());
        }
    }

    // After a drag is detected, the system starts handling drag-and-drop events such as
    // onDragOver and onDragDropped
    // triggered continuously while dragging an object over a target
    void onGraphPaneDragOver(DragEvent dragEvent) {
        logger.debug("Drag over at coordinates: [X: {}, Y: {}]", dragEvent.getX(),
                     dragEvent.getY()
        );
    }

    // dragged object is dropped onto a target
    void onGraphPaneDragDropped(DragEvent dragEvent) {
        logger.debug("Drag dropped at coordinates: [X: {}, Y: {}]", dragEvent.getX(),
                     dragEvent.getY()
        );
    }

    private GraphNode createAndAddVertex(double x, double y) {
        logger.debug("Creating a new GraphNode at coordinates: [X: {}, Y: {}]", x, y);

        GraphNode graphNode = new GraphNode(x, y);

        graphNode.setOnMousePressed(this::handleVertexPressed);
        graphNode.setOnMouseReleased(e -> handleVertexReleased(e, graphNode));
        graphNode.setOnDragDetected(e -> handleVertexDragDetected(e, graphNode));
        graphNode.setOnMouseDragged(e -> handleVertexDragged(e, graphNode));
        graphNode.setOnMouseDragEntered(e -> nodeMouseDragEntered(e, graphNode));
        graphNode.setOnMouseDragExited(e -> nodeMouseDragExited(e, graphNode));

        logger.debug("Adding new GraphNode with ID {} to the graphPane.", graphNode.getId());

        this.addVertexToAccordion(graphNode);
        graphPane.getChildren().add(graphNode);

        return graphNode;
    }

    private void handleVertexPressed(MouseEvent mouseEvent) {
        if (mouseEvent.isSecondaryButtonDown()) {
            deleteNode = true;
        }
    }

    private void handleVertexReleased(MouseEvent mouseEvent, GraphNode node) {
        if (this.tempVertex != null) {
            this.vertex1.removeEdge(edge);
            this.graphPane.getChildren().removeAll(vertex2, edge);

            removeVertexFromAccordion(this.vertex2);

            GraphNode.decrementCount();
            this.vertex2 = this.tempVertex;

            // do not allow to create same edge multiple times between 2 nodes
            if (!vertex1.getNeighbours().contains(vertex2)) {
                this.edge = createAndAddEdge(vertex1, vertex2);
            }
        }

        if (vertex2 != null) {
            vertex2.getStyleClass().remove("drag");
        }

        for (Edge e : node.getEdges()) {
            e.getStyleClass().remove("drag");
        }

        if (this.deleteNode) {
            deleteNode(node);
            deleteNode = false;
            removeVertexFromAccordion(node);
        }

        this.vertex1 = null;
        this.vertex2 = null;
        this.edge = null;
        this.tempVertex = null;
    }

    private void deleteNode(GraphNode node) {
        for (Edge edge : node.getEdges()) {
            edge.getNeighbour(node).getEdges().remove(edge);
            this.graphPane.getChildren().remove(edge);
        }
        this.graphPane.getChildren().remove(node);
    }

    private void handleVertexDragged(MouseEvent e, GraphNode graphNode) {
        if (this.vertex2 != null) {

            // relative from graphNode
            this.vertex2.setLayoutX(graphNode.getLayoutX() + e.getX() + graphNode.getTranslateX());
            this.vertex2.setLayoutY(graphNode.getLayoutY() + e.getY() + graphNode.getTranslateY());
        }
    }

    private void handleVertexDragDetected(MouseEvent mouseEvent, GraphNode graphNode) {
        if (mouseEvent.isPrimaryButtonDown()) {
            graphNode.startFullDrag();
            this.vertex1 = graphNode;
            this.vertex2 = createAndAddVertex(
                    graphNode.getLayoutX() + mouseEvent.getX() + graphNode.getTranslateX(),
                    graphNode.getLayoutY() + mouseEvent.getY() + graphNode.getTranslateX()
            );
            this.edge = createAndAddEdge(vertex1, vertex2);

            // whenever move this vertex on other put it on back
            // might stay on top of other elements even when it is supposed to be hidden
            this.vertex2.toBack();

            vertex2.getStyleClass().add("drag");
            edge.getStyleClass().add("drag");
        }

        if (mouseEvent.isSecondaryButtonDown()) {
            this.deleteNode = false; // do not delete on release

            graphNode.getStyleClass().add("drag");
            for (Edge edge : graphNode.getEdges()) {
                edge.toFront();
                edge.getStyleClass().add("drag");
            }

            graphNode.toFront();

            // set currently dragged vertex
            this.vertex2 = graphNode;
        }
    }

    private void nodeMouseDragEntered(MouseDragEvent e, GraphNode graphNode) {
        if (graphNode != vertex1 && graphNode != vertex2) {
            graphNode.getStyleClass().add("drag");
            this.tempVertex = graphNode;
            this.vertex2.setVisible(false);
        }
    }

    private void nodeMouseDragExited(MouseDragEvent e, GraphNode graphNode) {
        if (vertex2 != null) {
            vertex2.setVisible(true);
        }

        if (tempVertex != null) {
            graphNode.getStyleClass().remove("drag");
            tempVertex = null;
        }
    }

    private void addVertexToAccordion(GraphNode graphNode) {
        VertexSetup vertexSetup = new VertexSetup(graphNode, this.publisher);
        TitledPane titledPane = new TitledPane(graphNode.getId(), vertexSetup);
        titledPane.setMaxWidth(Double.MAX_VALUE);
        this.vertexList.getPanes().add(titledPane);

        titledPane.expandedProperty().addListener(
                (ChangeListener<? super Boolean>) (obs, wasExpanded, expandedNow) -> {
                    if (expandedNow) {
                        vertexSetup.update();
                    }
                }
        );


    }

    private void removeVertexFromAccordion(GraphNode graphNode) {
        vertexList.getPanes().stream()
                .filter(tp -> tp.getText().equals(graphNode.getId()))
                .findFirst()
                .ifPresent(vertexList.getPanes()::remove);
    }

    @Override
    public void update(String eventType, Node node) {
        if (eventType.equals("startClicked")) {
            logger.info("Start button clicked for node: {}", node.getId());
            startVertex.set((GraphNode) node);
        } else if (eventType.equals("destinationClicked")) {
            logger.info("Destination button clicked for node: {}", node.getId());
            destinationVertex.set((GraphNode) node);
            logger.info("Destination node set: {}", destinationVertex.get().getId());
        } else if (eventType.equals("removeEdge")) {
            logger.info("removing edge");
            this.graphPane.getChildren().remove(node);
        }
    }

    public GraphAlgorithmThread getResolveThread(final GraphAlgorithm algorithm,
                                   SimpleObjectProperty<GraphAlgorithmThread> resolveThread,
                                   boolean isStepDisabled) {
       return new GraphAlgorithmThread(() -> {
           algorithm.start(startVertex.get(), destinationVertex.get(), isStepDisabled);
           resolveThread.set(null);
       }, algorithm, isStepDisabled);
    }
}
