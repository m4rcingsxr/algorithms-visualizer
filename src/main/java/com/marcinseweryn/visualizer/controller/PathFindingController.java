package com.marcinseweryn.visualizer.controller;

import com.marcinseweryn.visualizer.Publisher;
import com.marcinseweryn.visualizer.Subscriber;
import com.marcinseweryn.visualizer.model.Algorithm;
import com.marcinseweryn.visualizer.model.GraphAlgorithm;
import com.marcinseweryn.visualizer.view.Edge;
import com.marcinseweryn.visualizer.view.GraphNode;
import com.marcinseweryn.visualizer.view.VertexSetup;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

/**
 * Controller for handling the Path Finding algorithm visualization.
 * Manages the interaction between nodes (GraphNode) and edges (Edge)
 * on the algorithm visualization space. Handles user inputs, drag events,
 * and node creation, while also updating the UI components like
 * the accordion and list views.
 */
public class PathFindingController implements Subscriber {

    private static final Logger logger = LogManager.getLogger(PathFindingController.class);

    // UI Elements
    private final AnchorPane algorithmSpace;
    private final Accordion renderedNodes;
    private final ToggleButton showEdgeWeightToggle;
    private final ToggleButton showEdgeDistanceToggle;
    private final ListView<SimpleStringProperty> candidateNodeList;
    private final ListView<SimpleStringProperty> visitedNodeList;
    private final ListView<String> pseudocodeList;
    private final ChoiceBox<Algorithm> algorithmListBox;

    // Internal state variables
    private final Publisher eventPublisher;
    ;
    private GraphNode startingNode; // Graph node that starts the drag process
    private GraphNode draggedNode; // Node that is created by drag from the starting node
    private GraphNode hoveredNode; // Node currently hovered over during drag
    private Edge connectingEdge; // Edge created between the starting node and the dragged node
    private boolean isNodeMarkedForDeletion = false; // // Flag to track node deletion requests

    // Starting Node for path finding algorithms
    private final SimpleObjectProperty<GraphNode> startNodeProperty = new SimpleObjectProperty<>(null);

    // Destination Node for path finding algorithms
    private final SimpleObjectProperty<GraphNode> destinationNodeProperty = new SimpleObjectProperty<>(null);

    // pseudo classes to activate - used for state visualization of the executing algorithm
    public static final PseudoClass currentNodeStyle = PseudoClass.getPseudoClass("current");
    public static final PseudoClass neighborNodeStyle = PseudoClass.getPseudoClass("neighbour");

    /**
     * Initializes the PathFindingController, setting up the necessary event subscriptions
     * and injecting UI elements for managing node and edge rendering.
     */
    public PathFindingController(AnchorPane algorithmSpace, Accordion renderedNodes,
                                 ToggleButton showEdgeWeightToggle, ToggleButton showEdgeDistanceToggle,
                                 ListView<SimpleStringProperty> candidateNodeList,
                                 ListView<SimpleStringProperty> visitedNodeList,
                                 ListView<String> pseudocodeList,
                                 ChoiceBox<Algorithm> algorithmListBox) {
        this.algorithmSpace = algorithmSpace;
        this.renderedNodes = renderedNodes;
        this.showEdgeWeightToggle = showEdgeWeightToggle;
        this.showEdgeDistanceToggle = showEdgeDistanceToggle;
        this.candidateNodeList = candidateNodeList;
        this.visitedNodeList = visitedNodeList;
        this.pseudocodeList = pseudocodeList;
        this.algorithmListBox = algorithmListBox;

        initializeAlgorithmLegend();

        this.eventPublisher = new Publisher();
        initializeEventSubscriptions();
        logger.info("PathFindingController initialized.");
    }

    private void initializeAlgorithmLegend() {
        HBox legend = new HBox(20);
        legend.setPadding(new Insets(0, 20, 10, 20));
        legend.setStyle("-fx-background-color: grey;-fx-background-radius: 15; -fx-padding: 10;");

        VBox vbox1 = new VBox(10);
        vbox1.setPadding(new Insets(10, 10, 10, 10));
        VBox vbox2 = new VBox(10);
        vbox1.setPadding(new Insets(10, 10, 10, 10));

        // Create HBox to hold the GraphNode and Label side by side
        HBox visited = new HBox(10); // 10 is the spacing between GraphNode and Label
        visited.setAlignment(Pos.CENTER_LEFT);

        GraphNode visitedNode = new GraphNode("visited"); // Your GraphNode
        Label visitedLabel = new Label("Visited");
        visitedLabel.setFont(Font.font(14));

        visited.getChildren().addAll(visitedNode, visitedLabel); // Add GraphNode and Label side by side


        GraphNode candidateNode = new GraphNode("candidate-nodes"); // Your GraphNode
        Label candidateLabel = new Label("Candidate");
        candidateLabel.setFont(Font.font(14));

        HBox candidate = new HBox(10);
        candidate.getChildren().addAll(candidateNode, candidateLabel);
        candidate.setAlignment(Pos.CENTER_LEFT);

        GraphNode pathNode = new GraphNode("path"); // Your GraphNode
        Label pathLabel = new Label("Path");
        pathLabel.setFont(Font.font(14));

        HBox path = new HBox(10);
        path.getChildren().addAll(pathNode, pathLabel);
        path.setAlignment(Pos.CENTER_LEFT);

        GraphNode currentNode = new GraphNode();
        currentNode.pseudoClassStateChanged(PathFindingController.currentNodeStyle, true);
        Label currentLabel = new Label("Current node");
        currentLabel.setFont(Font.font(14));

        HBox current = new HBox(10);
        current.getChildren().addAll(currentNode, currentLabel);
        current.setAlignment(Pos.CENTER_LEFT);

        GraphNode neighborNode = new GraphNode();
        neighborNode.pseudoClassStateChanged(PathFindingController.neighborNodeStyle, true);
        Label neighborLabel = new Label("Neighbor node");
        neighborLabel.setFont(Font.font(14));

        currentNode.setStyle("-fx-background-color: null;");
        neighborNode.setStyle("-fx-background-color: null;");

        HBox neighbor = new HBox(10);
        neighbor.getChildren().addAll(neighborNode, neighborLabel);
        neighbor.setAlignment(Pos.CENTER_LEFT);

        vbox1.getChildren().addAll(visited, candidate, path);
        vbox2.getChildren().addAll(current, neighbor);

        legend.getChildren().addAll(vbox1, vbox2);

        AnchorPane.setBottomAnchor(legend, 0.0);
        AnchorPane.setLeftAnchor(legend, 0.0);

        algorithmSpace.getChildren().add(legend);
    }


    /**
     * Initializes the event subscriptions for the controller.
     */
    private void initializeEventSubscriptions() {
        this.eventPublisher.subscribe("startClicked", this);
        this.eventPublisher.subscribe("destinationClicked", this);
        this.eventPublisher.subscribe("removeEdge", this);
    }

    /**
     * Initializes the selected algorithm from the algorithm list.
     * It creates a new instance of the selected GraphAlgorithm with the required properties.
     *
     * @return Optional of GraphAlgorithm if an algorithm is selected, otherwise empty Optional.
     */
    public Optional<GraphAlgorithm> initializeSelectedAlgorithm() {
        if (this.algorithmListBox.getValue() instanceof GraphAlgorithm selectedAlgorithm) {
            try {
                return Optional.of(selectedAlgorithm.getClass()
                                           .getDeclaredConstructor(ListView.class, ListView.class, ListView.class,
                                                                   SimpleObjectProperty.class, SimpleObjectProperty.class
                                           )
                                           .newInstance(candidateNodeList, visitedNodeList, pseudocodeList,
                                                        startNodeProperty, destinationNodeProperty
                                           ));
            } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException |
                     InstantiationException e) {
                throw new RuntimeException(e);
            }
        }

        return Optional.empty();
    }

    /**
     * Handles the mouse press event on the algorithm space.
     * Creates a new graph node at the mouse location and sets it as the starting node.
     *
     * @param mouseEvent The mouse event triggered on pressing the algorithm space.
     */
    void onAlgorithmSpaceMousePressed(MouseEvent mouseEvent) {
        logMouseEvent("Mouse pressed", mouseEvent);
        if (mouseEvent.isPrimaryButtonDown()) {
            this.startingNode = this.createGraphNodeAt(mouseEvent.getX(), mouseEvent.getY());
        }
    }

    /**
     * Handles the drag detected event on the algorithm space.
     * Initiates drag by creating a new node from the starting node and an edge connecting them.
     *
     * @param mouseEvent The mouse event triggered on detecting a drag action.
     */
    void onAlgorithmSpaceDragDetected(MouseEvent mouseEvent) {
        logMouseEvent("Drag detected", mouseEvent);
        if (mouseEvent.isPrimaryButtonDown()) {
            this.startNodeDrag(mouseEvent);
        }
    }

    /**
     * Handles the mouse drag event on the algorithm space.
     * Updates the position of the currently dragged node as the mouse moves.
     *
     * @param mouseEvent The mouse event triggered during a drag action.
     */
    void onAlgorithmSpaceMouseDragged(MouseEvent mouseEvent) {
        logMouseEvent("Mouse dragged", mouseEvent);
        if (this.draggedNode != null) {
            this.moveDraggedNodeTo(mouseEvent.getX(), mouseEvent.getY());
        }
    }

    /**
     * Handles the mouse release event on the algorithm space.
     * Completes the node drag process and either connects to an existing node or finalizes the new node creation.
     *
     * @param mouseEvent The mouse event triggered on releasing the mouse button.
     */
    void onAlgorithmSpaceMouseReleased(MouseEvent mouseEvent) {
        logMouseEvent("Mouse released", mouseEvent);
        finalizeOrResetNodeMovement();
    }

    // Logs mouse events for debugging purposes
    private void logMouseEvent(String eventDescription, MouseEvent mouseEvent) {
        logger.trace("{} at coordinates: [X: {}, Y: {}]", eventDescription, mouseEvent.getX(),
                     mouseEvent.getY()
        );
    }

    /**
     * Starts the drag operation by creating a new graph node and an edge between it and the starting node.
     * Also enables full gesture drag to allow drag-related events across nodes.
     *
     * @param mouseEvent The mouse event that triggers the drag operation.
     */
    private void startNodeDrag(MouseEvent mouseEvent) {
        this.draggedNode = createGraphNodeAt(mouseEvent.getX(), mouseEvent.getY());
        this.connectingEdge = createEdgeBetweenNodes(this.startingNode, this.draggedNode);
        this.draggedNode.startFullDrag();
        this.draggedNode.toBack();
        applyDragStyleToDraggedNodeAndCurrentEdge(true);
    }

    /**
     * Moves the dragged node to the specified x, y coordinates.
     *
     * @param x The x-coordinate to move the node to.
     * @param y The y-coordinate to move the node to.
     */
    private void moveDraggedNodeTo(double x, double y) {
        this.draggedNode.setLayoutX(x);
        this.draggedNode.setLayoutY(y);
    }

    /**
     * Creates a new GraphNode at the specified coordinates and sets up event handlers for it.
     *
     * @param x The x-coordinate of the new node.
     * @param y The y-coordinate of the new node.
     * @return The newly created GraphNode.
     */
    private GraphNode createGraphNodeAt(double x, double y) {
        logger.debug("Creating a new GraphNode at coordinates: [X: {}, Y: {}]", x, y);
        GraphNode newNode = new GraphNode(x, y);
        addEventHandlersToNode(newNode);
        addNodeToUI(newNode);
        return newNode;
    }

    /**
     * Adds event handlers to the specified GraphNode to handle interactions such as drag and click events.
     *
     * @param newNode The GraphNode to add event handlers to.
     */
    private void addEventHandlersToNode(GraphNode newNode) {
        newNode.setOnMousePressed(this::onGraphNodePressed);
        newNode.setOnMouseReleased(e -> onGraphNodeReleased(e, newNode));
        newNode.setOnDragDetected(e -> onGraphNodeDragDetected(e, newNode));
        newNode.setOnMouseDragged(e -> onGraphNodeDragged(e, newNode));
        newNode.setOnMouseDragEntered(e -> onNodeMouseDragEntered(e, newNode));
        newNode.setOnMouseDragExited(e -> onNodeMouseDragExited(e, newNode));
    }

    /**
     * Adds the newly created GraphNode to the UI, including the algorithm space and the accordion list.
     *
     * @param graphNode The GraphNode to add to the UI.
     */
    private void addNodeToUI(GraphNode graphNode) {
        addNodeToAccordion(graphNode);
        this.algorithmSpace.getChildren().add(graphNode);
    }

    /**
     * Creates an edge between two GraphNodes and binds its properties to the GraphNode properties.
     *
     * @param node1 The first node in the edge connection.
     * @param node2 The second node in the edge connection.
     * @return The newly created Edge.
     */
    private Edge createEdgeBetweenNodes(GraphNode node1, GraphNode node2) {
        Edge edge = new Edge(this.startingNode, this.draggedNode);
        bindEdgeProperties(edge);
        addEdgeToNodes(node1, node2, edge);
        return edge;
    }

    // Binds the edge's visibility properties to toggle buttons for showing weight and distance.
    private void bindEdgeProperties(Edge edge) {
        edge.weightVisibleProperty().bind(showEdgeWeightToggle.selectedProperty());
        edge.distanceVisibleProperty().bind(showEdgeDistanceToggle.selectedProperty());
    }

    // Adds the edge to both nodes and to the algorithm space UI.
    private void addEdgeToNodes(GraphNode node1, GraphNode node2, Edge edge) {
        node1.addEdge(edge);
        node2.addEdge(edge);
        this.algorithmSpace.getChildren().add(edge);
    }

    /**
     * Finalizes the node drag operation, either completing the connection to an existing node
     * or resetting the operation if no connection was made.
     */
    private void finalizeOrResetNodeMovement() {

        // if on movement - mouse release occur on existing vertex
        if (this.hoveredNode != null) {
            finalizeNodeMovement();
        }

        // release occur on algorithm space
        if (this.draggedNode != null) {
            applyDragStyleToDraggedNodeAndCurrentEdge(false);
        }
    }

    /**
     * Finalizes the node movement by connecting the dragged node to the hovered node
     * and removing the placeholder dragged node.
     */
    private void finalizeNodeMovement() {
        this.startingNode.removeEdge(this.connectingEdge);
        this.algorithmSpace.getChildren().removeAll(this.draggedNode, this.connectingEdge);
        this.removeNodeFromAccordion(this.draggedNode);
        GraphNode.decrementCount();

        this.draggedNode = this.hoveredNode;
        this.connectingEdge = createEdgeBetweenNodes(this.startingNode, this.draggedNode);
    }

    // Adds the GraphNode to the accordion in the UI for managing nodes.
    private void addNodeToAccordion(GraphNode graphNode) {
        VertexSetup setupView = new VertexSetup(graphNode, this.eventPublisher);
        TitledPane pane = new TitledPane(graphNode.getId(), setupView);
        configureAccordionPane(pane, setupView);
        this.renderedNodes.getPanes().add(pane);
    }

    // Configures the accordion pane to update the node setup view when expanded.
    private void configureAccordionPane(TitledPane pane, VertexSetup setupView) {
        pane.setMaxWidth(Double.MAX_VALUE);
        pane.expandedProperty().addListener((ChangeListener<? super Boolean>) (obs, wasExpanded, isNowExpanded) -> {
            if (isNowExpanded) {
                setupView.update();
            }
        });
    }

    // Removes the specified GraphNode from the accordion list in the UI.
    private void removeNodeFromAccordion(GraphNode node) {
        renderedNodes.getPanes().removeIf(pane -> pane.getText().equals(node.getId()));
    }

    // Applies or resets the specified style class on the current node and edge.
    private void applyDragStyleToDraggedNodeAndCurrentEdge(boolean apply) {
        if (apply) {
            this.draggedNode.getStyleClass().add("drag");
            this.connectingEdge.getStyleClass().add("drag");
        } else {
            resetDragNodeStyle();
        }
    }

    // Resets the specified style class on the current node and edge.
    private void resetDragNodeStyle() {
        if (this.draggedNode != null) {
            this.draggedNode.getStyleClass().remove("drag");
        }
        if (this.connectingEdge != null) {
            this.connectingEdge.getStyleClass().remove("drag");
        }
        this.draggedNode = null; // Reset the dragged node reference after the operation
    }

    /**
     * Handles the mouse pressed event on a GraphNode.
     * Sets the flag for marking the node for deletion if the secondary button is pressed.
     *
     * @param mouseEvent The mouse event triggered on pressing a GraphNode.
     */
    private void onGraphNodePressed(MouseEvent mouseEvent) {
        if (mouseEvent.isSecondaryButtonDown()) {
            this.isNodeMarkedForDeletion = true;
        }
    }

    /**
     * Handles the mouse release event on a GraphNode.
     * Finalizes or resets the node movement and deletes the node if it is marked for deletion.
     *
     * @param mouseEvent The mouse event triggered on releasing the mouse button.
     * @param node       The GraphNode that was released.
     */
    private void onGraphNodeReleased(MouseEvent mouseEvent, GraphNode node) {
        finalizeOrResetNodeMovement();
        removeDragStyle(node);

        if (isNodeMarkedForDeletion) {
            deleteGraphNode(node);
            removeNodeFromAccordion(node);
            isNodeMarkedForDeletion = false;
        }

        resetInteractionState();
    }

    // Removes the drag style from the specified GraphNode and its edges.
    private void removeDragStyle(GraphNode node) {
        node.getEdges().forEach(e -> e.getStyleClass().remove("drag"));
        if (this.draggedNode != null) {
            this.draggedNode.getStyleClass().remove("drag");
        }
    }

    // Deletes the specified GraphNode and its edges from the UI and updates neighboring nodes.
    private void deleteGraphNode(GraphNode node) {
        for (Edge edge : node.getEdges()) {
            edge.getNeighbour(node).getEdges().remove(edge);
            this.algorithmSpace.getChildren().remove(edge);
        }
        this.algorithmSpace.getChildren().remove(node);
    }

    // Resets the interaction state by clearing references to the starting node, dragged node, and connecting edge.
    private void resetInteractionState() {
        this.startingNode = null;
        this.draggedNode = null;
        this.connectingEdge = null;
        this.hoveredNode = null;
    }

    // handle new graph node dragged from existing graph node
    private void onGraphNodeDragged(MouseEvent e, GraphNode graphNode) {
        if (this.draggedNode != null) {
            moveNodeRelativeToGraphNode(e, graphNode);
        }
    }

    // place relative to existing graph node new created node
    private void moveNodeRelativeToGraphNode(MouseEvent e, GraphNode graphNode) {
        this.draggedNode.setLayoutX(graphNode.getLayoutX() + e.getX() + graphNode.getTranslateX());
        this.draggedNode.setLayoutY(graphNode.getLayoutY() + e.getY() + graphNode.getTranslateY());
    }

    /**
     * Handles the drag detected event on a GraphNode.
     * Starts the drag operation from the existing GraphNode or marks the node for deletion.
     *
     * @param mouseEvent The mouse event triggered on detecting a drag action.
     * @param graphNode  The GraphNode from which the drag started.
     */
    private void onGraphNodeDragDetected(MouseEvent mouseEvent, GraphNode graphNode) {
        if (mouseEvent.isPrimaryButtonDown()) {
            startDragFromExistingGraphNode(mouseEvent, graphNode);
        } else if (mouseEvent.isSecondaryButtonDown()) {
            markNodeForDeletion(graphNode);
        }
    }

    /**
     * Starts the drag operation from an existing GraphNode, creating a new node connected to it with an edge.
     *
     * @param mouseEvent The mouse event that initiates the drag.
     * @param graphNode  The existing GraphNode from which the drag starts.
     */
    private void startDragFromExistingGraphNode(MouseEvent mouseEvent, GraphNode graphNode) {
        graphNode.startFullDrag();
        this.startingNode = graphNode;  // Set the initial node to the existing graph node.
        this.draggedNode = createGraphNodeAt(
                graphNode.getLayoutX() + mouseEvent.getX() + graphNode.getTranslateX(),
                graphNode.getLayoutY() + mouseEvent.getY() + graphNode.getTranslateY()
        );
        this.connectingEdge = createEdgeBetweenNodes(startingNode, draggedNode);
        this.draggedNode.toBack();
        applyDragStyleToDraggedNodeAndCurrentEdge(true);
    }

    /**
     * Marks a GraphNode for deletion, applying the appropriate styles to indicate it is being removed.
     *
     * @param graphNode The GraphNode to mark for deletion.
     */
    private void markNodeForDeletion(GraphNode graphNode) {
        this.isNodeMarkedForDeletion = false;
        applyDragStyleToNodeAndEdges(graphNode);
        this.draggedNode = graphNode; // Update the dragged node reference to the graphNode being deleted.
    }

    /**
     * Applies the deletion style to a GraphNode and its edges, bringing them to the front visually.
     *
     * @param graphNode The GraphNode to apply the deletion style to.
     */
    private void applyDragStyleToNodeAndEdges(GraphNode graphNode) {
        graphNode.getStyleClass().add("drag");
        graphNode.toFront();
        for (Edge edge : graphNode.getEdges()) {
            edge.toFront();
            edge.getStyleClass().add("drag");
        }
    }

    /**
     * Handles the mouse drag entered event for a GraphNode, setting it as the hovered node for connection.
     *
     * @param e         The mouse drag event triggered when drag enters the GraphNode.
     * @param graphNode The GraphNode that is being hovered over.
     */
    private void onNodeMouseDragEntered(MouseDragEvent e, GraphNode graphNode) {
        if (graphNode != startingNode && graphNode != draggedNode) {
            applyHoverStyleToNode(graphNode);
        }
    }

    /**
     * Applies the hover style to a GraphNode when a drag operation enters it.
     * Sets the hovered node reference for potential connection.
     *
     * @param graphNode The GraphNode being hovered over.
     */
    private void applyHoverStyleToNode(GraphNode graphNode) {
        graphNode.getStyleClass().add("drag");
        this.hoveredNode = graphNode;
        this.draggedNode.setVisible(false);
    }

    /**
     * Handles the mouse drag exited event for a GraphNode, removing the hover style and resetting state.
     *
     * @param e         The mouse drag event triggered when drag exits the GraphNode.
     * @param graphNode The GraphNode that was exited.
     */
    private void onNodeMouseDragExited(MouseDragEvent e, GraphNode graphNode) {
        if (draggedNode != null) {
            draggedNode.setVisible(true);
        }

        if (hoveredNode != null) {
            graphNode.getStyleClass().remove("drag");
            hoveredNode = null;
        }
    }

    /**
     * Updates the controller based on specific events related to the graph nodes.
     * Handles events such as node selection for start and destination.
     *
     * @param eventType The type of event (e.g., "startClicked", "destinationClicked").
     * @param node      The GraphNode associated with the event.
     */
    @Override
    public void update(String eventType, Node node) {
        switch (eventType) {
            case "startClicked" -> {
                logger.info("Start button clicked for node: {}", node.getId());

                if(startNodeProperty.get() != null) {
                    startNodeProperty.get().getStyleClass().remove("start");
                }

                startNodeProperty.set((GraphNode) node);
                node.getStyleClass().add("start");
            }
            case "destinationClicked" -> {
                logger.info("Destination button clicked for node: {}", node.getId());

                if(destinationNodeProperty.get() != null) {
                    destinationNodeProperty.get().getStyleClass().remove("destination");
                }

                destinationNodeProperty.set((GraphNode) node);
                node.getStyleClass().add("destination");

                logger.info("Destination node set: {}", destinationNodeProperty.get().getId());
            }
            case "removeEdge" -> {
                logger.info("Removing edge");
                this.algorithmSpace.getChildren().remove(node);
            }
            default -> logger.info("Not supported");
        }
    }

    // Optional drag and drop events (not currently used, but available for future implementations).
    void onAlgorithmSpaceDragOver(DragEvent dragEvent) {
        logger.trace("Drag over at coordinates: [X: {}, Y: {}]", dragEvent.getX(), dragEvent.getY());
    }

    void onAlgorithmSpaceDragDropped(DragEvent dragEvent) {
        logger.trace("Drag dropped at coordinates: [X: {}, Y: {}]", dragEvent.getX(), dragEvent.getY());
    }

    public void resetGraphState() {
        for (Node n : algorithmSpace.getChildren()) {
            if(n instanceof GraphNode node) {
                node.setParentNode(null);
            }
        }

        Platform.runLater(() -> {
            for (Node n : algorithmSpace.getChildren()) {
                if (n instanceof GraphNode node) {
                    node.pseudoClassStateChanged(PathFindingController.neighborNodeStyle, false);
                    node.pseudoClassStateChanged(PathFindingController.currentNodeStyle, false);
                    node.setPrimaryClass("vertex");
                }

                if (n instanceof Edge edge) {
                    edge.getStyleClass().removeAll("path");
                }

                candidateNodeList.getItems().clear();
                visitedNodeList.getItems().clear();
                pseudocodeList.getItems().clear();

                startNodeProperty.get().setPrimaryClass("start");
                destinationNodeProperty.get().setPrimaryClass("destination");
            }


        });

    }
}
