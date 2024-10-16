package com.marcinseweryn.visualizer.controller;

import com.marcinseweryn.visualizer.Publisher;
import com.marcinseweryn.visualizer.Subscriber;
import com.marcinseweryn.visualizer.model.path.GraphAlgorithm;
import com.marcinseweryn.visualizer.view.Edge;
import com.marcinseweryn.visualizer.view.GraphNode;
import com.marcinseweryn.visualizer.view.VertexSetup;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.util.*;

import static java.util.stream.Collectors.toMap;

/**
 * Controller for handling the Path Finding algorithm visualization.
 * Manages the interaction between nodes (GraphNode) and edges (Edge)
 * on the algorithm visualization space. Handles user inputs, drag events,
 * and node creation, while also updating the UI components like
 * the accordion and list views.
 */
public class GraphTabController implements Subscriber {

    private static final Logger logger = LogManager.getLogger(GraphTabController.class);

    @FXML
    public VBox algorithmTab;

    @FXML
    private TabPane graphTab;
    @FXML
    private Accordion renderedNodes;

    @FXML
    private ToggleButton showEdgeWeightToggle;
    @FXML
    private ToggleButton showEdgeDistanceToggle;

    @FXML
    private ListView<String> pseudoCodeListGraph;

    // Internal state variables
    private Publisher eventPublisher;

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
    private MainController mainController;
    private AnchorPane algorithmSpace;

    /**
     * Initializes the PathFindingController, setting up the necessary event subscriptions
     * and injecting UI elements for managing node and edge rendering.
     */
    public GraphTabController() {

        this.eventPublisher = new Publisher();
        initializeEventSubscriptions();

        logger.info("PathFindingController initialized.");
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
     * It dynamically creates a new instance of the selected GraphAlgorithm using reflection.
     *
     * @return Optional of GraphAlgorithm if an algorithm is selected and instantiated successfully, otherwise empty Optional.
     */
    public Optional<GraphAlgorithm> initializeSelectedAlgorithm() {
        this.clearAlgorithmViews();  // Clear previous algorithm views

        // Get the selected class name from the ChoiceBox
        String selectedClassName = this.mainController.getAlgorithmChoiceBox().getValue();

        if (selectedClassName != null && !selectedClassName.isEmpty()) {
            try {
                // Load the class dynamically using reflection
                Class<?> algorithmClass = Class.forName(selectedClassName);

                // Ensure that the class is a subtype of GraphAlgorithm
                if (GraphAlgorithm.class.isAssignableFrom(algorithmClass)) {
                    // Create a new instance of the selected GraphAlgorithm using its constructor
                    GraphAlgorithm algorithmInstance = (GraphAlgorithm) algorithmClass
                            .getDeclaredConstructor(VBox.class, ListView.class,
                                                    SimpleObjectProperty.class, SimpleObjectProperty.class,
                                                    AnchorPane.class
                            )
                            .newInstance(algorithmTab, pseudoCodeListGraph, startNodeProperty, destinationNodeProperty, algorithmSpace);

                    return Optional.of(algorithmInstance);  // Return the created algorithm instance
                } else {
                    logger.error("Selected class {} is not a subclass of GraphAlgorithm", selectedClassName);
                }
            } catch (ClassNotFoundException e) {
                logger.error("Algorithm class {} not found", selectedClassName, e);
            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                logger.error("Failed to instantiate algorithm class {}", selectedClassName, e);
            }
        }

        return Optional.empty();  // Return an empty Optional if no valid algorithm is selected
    }


    // remove all except pseudocode
    private void clearAlgorithmViews() {
        algorithmTab.getChildren().removeIf(
                node -> !((Label) ((VBox) node).getChildren().get(0)).getText().equalsIgnoreCase("code"));
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
        algorithmSpace.getChildren().add(graphNode);
    }

    /**
     * Creates an edge between two GraphNodes and binds its properties to the GraphNode properties.
     *
     * @param node1 The first node in the edge connection.
     * @param node2 The second node in the edge connection.
     * @return The newly created Edge.
     */
    private Edge createEdgeBetweenNodes(GraphNode node1, GraphNode node2) {
        Edge edge = new Edge(node1, node2);
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
        algorithmSpace.getChildren().add(edge);
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
        algorithmSpace.getChildren().removeAll(this.draggedNode, this.connectingEdge);
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
        node.getAllEdges().forEach(e -> e.getStyleClass().remove("drag"));
        if (this.draggedNode != null) {
            this.draggedNode.getStyleClass().remove("drag");
        }
    }

    // Deletes the specified GraphNode and its edges from the UI and updates neighboring nodes.
    private void deleteGraphNode(GraphNode node) {
        for (Edge edge : node.getAllEdges()) {
            edge.getNeighbour(node).getAllEdges().remove(edge);
            algorithmSpace.getChildren().remove(edge);
        }
        algorithmSpace.getChildren().remove(node);
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
        for (Edge edge : graphNode.getAllEdges()) {
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

                if (startNodeProperty.get() != null) {
                    startNodeProperty.get().getStyleClass().remove("start");
                }

                startNodeProperty.set((GraphNode) node);
                node.getStyleClass().add("start");
            }
            case "destinationClicked" -> {
                logger.info("Destination button clicked for node: {}", node.getId());

                if (destinationNodeProperty.get() != null) {
                    destinationNodeProperty.get().getStyleClass().remove("destination");
                }

                destinationNodeProperty.set((GraphNode) node);
                node.getStyleClass().add("destination");

                logger.info("Destination node set: {}", destinationNodeProperty.get().getId());
            }
            case "removeEdge" -> {
                logger.info("Removing edge");
                algorithmSpace.getChildren().remove(node);
            }
            default -> logger.info("Not supported");
        }
    }

    // Optional drag and drop events (not currently used, but available for future implementations).
    void onAlgorithmSpaceDragOver(DragEvent dragEvent) {
        logger.trace("Drag over at coordinates: [X: {}, Y: {}]", dragEvent.getX(), dragEvent.getY());
        // allow files to be dropped into the algorithm space
        if (dragEvent.getDragboard().hasFiles()) {
            dragEvent.acceptTransferModes(TransferMode.ANY);
        }


    }

    void onAlgorithmSpaceDragDropped(DragEvent dragEvent) {
        logger.trace("Drag dropped at coordinates: [X: {}, Y: {}]", dragEvent.getX(), dragEvent.getY());

        clearAlgorithmSpace();

        List<File> draggedFiles = dragEvent.getDragboard().getFiles();

        try (Scanner scanner = new Scanner(draggedFiles.get(0))) {
            scanner.useDelimiter(",");

            int numberOfGraphNodes = scanner.nextInt();
            int startNodeId = scanner.nextInt();
            int destinationNodeId = scanner.nextInt();

            HashMap<Integer, Double[]> coordinates = new HashMap<>();

            for (int i = 0; i < numberOfGraphNodes; i++)
                coordinates.put(scanner.nextInt(), new Double[]{scanner.nextDouble(), scanner.nextDouble()});

            Map<Integer, GraphNode> graphNodes = coordinates
                    .entrySet()
                    .stream()
                    .collect(toMap(Map.Entry::getKey,
                                   entry -> createGraphNodeAt(entry.getValue()[0], entry.getValue()[1])
                    ));

            if (startNodeId != -1) {
                GraphNode graphNode = graphNodes.get(startNodeId);
                startNodeProperty.set(graphNode);

                graphNode.getStyleClass().add("start");
            }

            if (destinationNodeId != -1) {
                GraphNode graphNode = graphNodes.get(destinationNodeId);
                destinationNodeProperty.set(graphNode);

                graphNode.getStyleClass().add("destination");
            }

            while (scanner.hasNextInt()) {
                GraphNode nodeA = graphNodes.get(scanner.nextInt());
                GraphNode nodeB = graphNodes.get(scanner.nextInt());
                Edge edge = createEdgeBetweenNodes(nodeA, nodeB);
                edge.setArrowHeadVisible(nodeA, scanner.nextBoolean());
                edge.setArrowHeadVisible(nodeB, scanner.nextBoolean());
                edge.setWeight(scanner.nextDouble());
            }


        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    public void resetGraphState() {
        for (Node n : algorithmSpace.getChildren()) {
            if (n instanceof GraphNode node) {
                node.setParentNode(null);
            }
        }

        Platform.runLater(() -> {
            for (Node n : algorithmSpace.getChildren()) {
                if (n instanceof GraphNode node) {
                    node.pseudoClassStateChanged(GraphTabController.neighborNodeStyle, false);
                    node.pseudoClassStateChanged(GraphTabController.currentNodeStyle, false);
                    node.setPrimaryClass("vertex");
                }

                if (n instanceof Edge edge) {
                    edge.removeStyleClass("path");
                }

//                candidateNodeList.getItems().clear();
//                visitedNodeList.getItems().clear();
                pseudoCodeListGraph.getItems().clear();

                startNodeProperty.get().setPrimaryClass("start");
                destinationNodeProperty.get().setPrimaryClass("destination");
            }


        });

    }

    public void clearAlgorithmSpace() {
        this.destinationNodeProperty.set(null);
        this.startNodeProperty.set(null);
        this.pseudoCodeListGraph.getItems().clear();
//        this.candidateNodeList.getItems().clear();
//        this.visitedNodeList.getItems().clear();
        algorithmSpace.getChildren().clear();
        this.renderedNodes.getPanes().clear();
        GraphNode.setCount(0);
    }

    // noNodes,start,destination,[node,x,y],[nodeA,nodeB,headAVisible,HeadBVisible,weight]
    private String exportGraphToString() {
        if (algorithmSpace.getChildren().isEmpty()) {
            return "";
        } else {
            StringBuilder graph = new StringBuilder();


            graph.append(startNodeProperty.get() != null ? startNodeProperty.get().getId() : "-1")
                    .append(",")
                    .append(destinationNodeProperty.get() != null ? destinationNodeProperty.get().getId() : "-1")
                    .append(",");

            List<GraphNode> nodes = new ArrayList<>();


            for (Node child : algorithmSpace.getChildren()) {
                if (child instanceof GraphNode node) {
                    nodes.add(node);
                    graph.append(node.getId()).append(",").append(node.getLayoutX()).append(",").append(
                            node.getLayoutY()).append(",");
                }
            }

            graph.insert(0, nodes.size() + ",");

            for (Node child : algorithmSpace.getChildren()) {
                if (child instanceof Edge edge) {
                    graph.append(edge.getNodeA().getId()).append(",")
                            .append(edge.getNodeB().getId()).append(",")
                            .append(edge.isArrowHeadVisible(edge.getNodeA())).append(",")
                            .append(edge.isArrowHeadVisible(edge.getNodeB())).append(",")
                            .append(edge.getWeight()).append(",");
                }
            }

            return graph.toString();
        }
    }

    public void generateCompleteGraph(int numberOfNodes, double radius, double centerX, double centerY) {
        clearAlgorithmSpace();

        List<GraphNode> graphNodes = new ArrayList<>();

        // Calculate angle step in radians
        double angleStep = 2 * Math.PI / numberOfNodes;

        // Create nodes in a circular layout
        for (int i = 0; i < numberOfNodes; i++) {
            double angle = i * angleStep;
            double x = centerX + radius * Math.cos(angle);
            double y = centerY + radius * Math.sin(angle);

            GraphNode node = createGraphNodeAt(x, y);
            graphNodes.add(node);
        }

        Random random = new Random();  // Random generator for edge weights

        for (int i = 0; i < graphNodes.size(); i++) {
            for (int j = i + 1; j < graphNodes.size(); j++) {
                GraphNode nodeA = graphNodes.get(i);
                GraphNode nodeB = graphNodes.get(j);

                double weight = generateRandomIntWeight(random);
                Edge edgeBetweenNodes = createEdgeBetweenNodes(nodeA, nodeB);
                edgeBetweenNodes.setWeight(weight);
            }
        }

        startNodeProperty.set(graphNodes.get(5));
        destinationNodeProperty.set(graphNodes.get(2));
        startNodeProperty.get().setPrimaryClass("start");
        destinationNodeProperty.get().setPrimaryClass("destination");

    }

    private int generateRandomIntWeight(Random random) {
        return 1 + random.nextInt(20);
    }

    public void generateDenseGraph() {
        generateDenseGraph(25, 180);
    }

    /**
     * Generates a dense graph programmatically with a given number of nodes.
     *
     * @param numberOfNodes The number of nodes to generate.
     * @param nodeSpacing   The spacing between nodes in the grid.
     */
    private void generateDenseGraph(int numberOfNodes, double nodeSpacing) {
        clearAlgorithmSpace();

        List<GraphNode> graphNodes = new ArrayList<>();

        int gridSize = (int) Math.ceil(Math.sqrt(numberOfNodes));

        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                double x = col * nodeSpacing + 40;
                double y = row * nodeSpacing + 40;

                GraphNode node = createGraphNodeAt(x, y);
                graphNodes.add(node);
            }
        }

        // Create edges between every pair of nodes (dense graph)
        for (int i = 0; i < graphNodes.size(); i++) {
            for (int j = i + 1; j < graphNodes.size(); j++) {
                GraphNode nodeA = graphNodes.get(i);
                GraphNode nodeB = graphNodes.get(j);

                createEdgeBetweenNodes(nodeA, nodeB);
            }
        }
    }

    public void generateTreeGraph() {
        GraphNode root = buildTree(440, 30, 200);
        root.setPrimaryClass("start");

        startNodeProperty.set(root);
        destinationNodeProperty.get().setPrimaryClass("destination");

    }

    private GraphNode buildTree(double x, double y, double nodeSpacing) {
        GraphNode node = createGraphNodeAt(x, y);

        if (y > 600) {
            destinationNodeProperty.set(node);
            return node;
        }

        createEdgeBetweenNodes(node, buildTree(x - nodeSpacing, y + 150, nodeSpacing / 2));
        createEdgeBetweenNodes(node, buildTree(x + nodeSpacing, y + 150, nodeSpacing / 2));

        return node;
    }

    public void selectAlgorithmTab() {
        graphTab.getSelectionModel().selectFirst();
    }

    @FXML
    private void onExportGraphButtonClick() {
        Window mainStage = algorithmSpace.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save graph");
        fileChooser.setInitialFileName("graph-" + LocalDate.now() + ".txt");
        File file = fileChooser.showSaveDialog(mainStage);
        if (file != null) {
            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))) {
                bufferedWriter.write(exportGraphToString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @FXML
    private void onClearGraphButtonClick() {
        this.mainController.runningAlgorithmThreadProperty().set(null);
        this.mainController.getResetButton().setDisable(true);
        clearAlgorithmSpace();
    }

    @FXML
    private void onClickGenerateTreeGraph(ActionEvent actionEvent) {
        clearAlgorithmSpace();
        generateTreeGraph();
    }

    @FXML
    private void onClickGenerateCompleteGraph(ActionEvent actionEvent) {
        clearAlgorithmSpace();
        generateCompleteGraph(7, 300, 420, 420);
    }

    public void injectController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setAlgorithmSpace(AnchorPane algorithmSpace) {
        this.algorithmSpace = algorithmSpace;
    }
}
