package com.marcinseweryn.visualizer.controller;

import com.marcinseweryn.visualizer.model.Algorithm;
import com.marcinseweryn.visualizer.model.GraphAlgorithm;
import com.marcinseweryn.visualizer.view.Edge;
import com.marcinseweryn.visualizer.view.GraphNode;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Main controller for the Algorithm Visualizer application.
 * Handles the interaction between the user interface and the algorithms, including
 * managing tabs for sorting and graph algorithms, controlling the start/stop of algorithms,
 * and dynamically loading available algorithm classes.
 */
public class MainController {

    private static final Logger logger = LogManager.getLogger(MainController.class);

    // Graph-specific UI elements
    @FXML
    private TabPane graphTab;
    @FXML
    private Accordion renderedNodes;
    @FXML
    public Button exportGraphBtn;
    @FXML
    public Button clearGraphBtn;
    @FXML
    private ToggleButton showEdgeWeightToggle;
    @FXML
    private ToggleButton showEdgeDistanceToggle;
    @FXML
    private ListView<SimpleStringProperty> candidateNodeList;
    @FXML
    private ListView<SimpleStringProperty> visitedNodeList;

    @FXML
    private ListView<String> pseudoCodeList;

    // Sorting and common UI elements
    @FXML
    private BorderPane main;

    @FXML
    private TabPane algorithmTab;
    @FXML
    private ChoiceBox<Algorithm> algorithmListBox;
    @FXML
    private VBox algorithmContainer;
    @FXML
    private AnchorPane graphPane;
    @FXML
    private AnchorPane sortingPane;

    // Algorithm control buttons
    @FXML
    private Button startButton;

    @FXML
    private Button stepButton;

    @FXML
    private Button resetButton;


    // Controllers for managing specific algorithm types
    private PathFindingController pathFindingController;
    private SortingController sortingController;

    // Property to manage the currently running algorithm thread
    private final SimpleObjectProperty<AlgorithmThread> runningAlgorithmThread = new SimpleObjectProperty<>();

    /**
     * Initializes the MainController. Sets up controllers for sorting and graph algorithms,
     * configures the tab selection listener, and loads the initial list of pathfinding algorithms.
     */
    @FXML
    public void initialize() {
        logger.info("Initializing MainController...");

        // Initialize controllers for pathfinding and sorting algorithms
        initializeControllers();

        // Set up tab selection listener to switch between graph and sorting algorithms
        setupTabSelectionListener();

        // Load the initial list of pathfinding algorithms
        loadAlgorithmList("com/marcinseweryn/visualizer/model/path");

        // Initially display the graph pane by default
        algorithmContainer.getChildren().remove(sortingPane);
        logger.debug("Graph pane selected by default.");

        candidateNodeList.prefHeightProperty().bind(algorithmContainer.heightProperty().multiply(0.3));
        visitedNodeList.prefHeightProperty().bind(algorithmContainer.heightProperty().multiply(0.3));
        pseudoCodeList.prefHeightProperty().bind(algorithmContainer.heightProperty().multiply(0.4));

        // Bind the left pane to 30% of the BorderPane width
        algorithmTab.prefWidthProperty().bind(main.widthProperty().multiply(0.2));

        // Bind the center pane to 70% of the BorderPane width
        algorithmContainer.prefWidthProperty().bind(main.widthProperty().multiply(0.8));

        logger.info("Controller initialization completed.");
    }

    /**
     * Initializes the PathFindingController and SortingController.
     * These controllers manage specific algorithm categories: pathfinding and sorting.
     */
    private void initializeControllers() {
        this.pathFindingController = new PathFindingController(
                graphPane, renderedNodes, showEdgeWeightToggle, showEdgeDistanceToggle, candidateNodeList,
                visitedNodeList, pseudoCodeList, algorithmListBox
        );
        this.sortingController = new SortingController(sortingPane);

        logger.debug("PathFindingController and SortingController initialized.");
    }

    /**
     * Sets up a listener for tab selection. This method is responsible for switching between
     * the pathfinding and sorting tabs, showing the appropriate pane (graph or sorting),
     * and loading the corresponding algorithms.
     */
    private void setupTabSelectionListener() {
        algorithmTab.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldTab, newTab) -> {
                    String selectedTab = newTab.getText();
                    logger.info("{} tab selected.", selectedTab);

                    // Show the graph pane when the Path Finding tab is selected
                    if (selectedTab.equals("Path Finding")) {
                        showGraphPane();
                        loadAlgorithmList("com/marcinseweryn/visualizer/model/path");
                    }
                    // Show the sorting pane when the Sorting tab is selected
                    else if (selectedTab.equals("Sorting")) {
                        showSortingPane();
                        loadAlgorithmList("com/marcinseweryn/visualizer/model/sort");
                    } else {
                        logger.warn("Unknown tab selected: {}", selectedTab);
                    }
                });
    }

    /**
     * Displays the graph pane for pathfinding algorithms by switching the visibility
     * of the panes inside the algorithm container.
     */
    private void showGraphPane() {
        algorithmContainer.getChildren().remove(sortingPane);
        if (!algorithmContainer.getChildren().contains(graphPane)) {
            algorithmContainer.getChildren().add(graphPane);
        }
    }

    /**
     * Displays the sorting pane for sorting algorithms by switching the visibility
     * of the panes inside the algorithm container.
     */
    private void showSortingPane() {
        algorithmContainer.getChildren().remove(graphPane);
        if (!algorithmContainer.getChildren().contains(sortingPane)) {
            algorithmContainer.getChildren().add(sortingPane);
        }
    }

    /**
     * Loads the list of algorithm classes from the specified resource path.
     * The method dynamically loads available algorithms from the file system and
     * populates the algorithm list (ChoiceBox).
     *
     * @param resourcePath The path to the directory containing the algorithm classes.
     */
    private void loadAlgorithmList(String resourcePath) {
        try {
            // Clear the current algorithm list
            algorithmListBox.getItems().clear();

            // Load classes from the resource path
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Enumeration<URL> resources = classLoader.getResources(resourcePath);
            File directory = new File(resources.nextElement().getFile().replace("%20", " "));

            ArrayList<Class<?>> algorithmClasses = new ArrayList<>();
            if (directory.listFiles() != null) {
                for (File file : Objects.requireNonNull(directory.listFiles())) {
                    if (file != null && file.getName().endsWith(".class")) {
                        String className = resourcePath.replace('/', '.') + '.' + file.getName().replace(".class", "");
                        algorithmClasses.add(Class.forName(className));
                    }
                }

                // Instantiate each class and add it to the algorithm list
                for (Class<?> clazz : algorithmClasses) {
                    Object algorithmInstance = clazz.getDeclaredConstructor().newInstance();
                    if (algorithmInstance instanceof Algorithm algorithm) {
                        algorithmListBox.getItems().add(algorithm);
                    }
                }
            }

            // Select the first algorithm by default
            algorithmListBox.getSelectionModel().selectFirst();
        } catch (IOException | ClassNotFoundException | InvocationTargetException | InstantiationException |
                 IllegalAccessException | NoSuchMethodException e) {
            logger.error(e);
        }
    }

    /**
     * Event handler for the Start button click.
     * This method starts or resumes the selected algorithm depending on whether a thread is already running.
     *
     * @param event The action event triggered by the button click.
     */
    @FXML
    private void onStartButtonClick(ActionEvent event) {
        boolean isStepModeDisabled = event.getSource() == startButton;

        if (isStepModeDisabled) {
            startButton.setDisable(true);
            stepButton.setDisable(true);
            resetButton.setDisable(true);
        }

        // If no algorithm is running or the user wants to disable step mode, start a new algorithm
        if (runningAlgorithmThread.get() == null || isStepModeDisabled) {
            startNewAlgorithm(isStepModeDisabled);
        }
        // If an algorithm is running, resume it
        else {
            resumeAlgorithm();
        }
    }

    @FXML
    public void onResetButtonClick(ActionEvent event) {
        if (isPathFindingTabSelected()) {
            startButton.setDisable(false);
            stepButton.setDisable(false);
            resetButton.setDisable(true);
            pathFindingController.resetGraphState();
        } else {
            // ...
        }
    }

    /**
     * Starts a new algorithm based on the selected tab (Path Finding or Sorting).
     * This method handles the initialization of the selected algorithm and starts a new thread to run it.
     *
     * @param isStepModeDisabled Boolean flag indicating whether step mode is disabled.
     */
    private void startNewAlgorithm(boolean isStepModeDisabled) {
        if (isPathFindingTabSelected()) {
            Optional<GraphAlgorithm> selectedAlgorithm = pathFindingController.initializeSelectedAlgorithm();

            if (selectedAlgorithm.isEmpty()) {
                throw new RuntimeException("Failed to initialize the algorithm.");
            }

            // Switch to the first graph tab
            graphTab.getSelectionModel().selectFirst();

            // Create and start a new thread for the algorithm
            runningAlgorithmThread.set(new AlgorithmThread(() -> {
                selectedAlgorithm.get().start(isStepModeDisabled);
                runningAlgorithmThread.set(null); // Reset the running thread when the algorithm completes
                resetButton.setDisable(false);
            }, selectedAlgorithm.get()));
        } else {
            // Add sorting logic here if needed
        }

        runningAlgorithmThread.get().start();
    }

    private boolean isPathFindingTabSelected() {
        return algorithmTab.getSelectionModel().getSelectedItem().getText().equals("Path Finding");
    }

    /**
     * Resumes a paused algorithm by invoking the resume functionality on the current running thread.
     */
    private void resumeAlgorithm() {
        logger.debug("Resuming algorithm...");
        runningAlgorithmThread.get().resumeAlgorithm();
    }

    // Event handlers for mouse and drag events on the graph pane. These are delegated to the PathFindingController.

    /**
     * Handles mouse press events on the graph pane, delegating to the PathFindingController.
     *
     * @param mouseEvent The mouse event triggered on pressing the graph pane.
     */
    @FXML
    private void onGraphPaneMousePressed(MouseEvent mouseEvent) {
        this.pathFindingController.onAlgorithmSpaceMousePressed(mouseEvent);
    }

    /**
     * Handles drag detection events on the graph pane, delegating to the PathFindingController.
     *
     * @param mouseEvent The mouse event triggered on drag detection on the graph pane.
     */
    @FXML
    private void onGraphPaneDragDetected(MouseEvent mouseEvent) {
        this.pathFindingController.onAlgorithmSpaceDragDetected(mouseEvent);
    }

    /**
     * Handles mouse drag events on the graph pane, delegating to the PathFindingController.
     *
     * @param mouseEvent The mouse event triggered during dragging on the graph pane.
     */
    @FXML
    private void onGraphPaneMouseDragged(MouseEvent mouseEvent) {
        this.pathFindingController.onAlgorithmSpaceMouseDragged(mouseEvent);
    }

    /**
     * Handles mouse release events on the graph pane, delegating to the PathFindingController.
     *
     * @param mouseEvent The mouse event triggered on releasing the mouse on the graph pane.
     */
    @FXML
    private void onGraphPaneMouseReleased(MouseEvent mouseEvent) {
        this.pathFindingController.onAlgorithmSpaceMouseReleased(mouseEvent);
    }

    /**
     * Handles drag over events on the graph pane, delegating to the PathFindingController.
     *
     * @param dragEvent The drag event triggered when dragging over the graph pane.
     */
    @FXML
    private void onGraphPaneDragOver(DragEvent dragEvent) {
        this.pathFindingController.onAlgorithmSpaceDragOver(dragEvent);
    }

    /**
     * Handles drag dropped events on the graph pane, delegating to the PathFindingController.
     *
     * @param dragEvent The drag event triggered on dropping an item on the graph pane.
     */
    @FXML
    private void onGraphPaneDragDropped(DragEvent dragEvent) {
        this.pathFindingController.onAlgorithmSpaceDragDropped(dragEvent);
    }

    @FXML
    private void onExportGraphButtonClick() {
        Window mainStage = graphPane.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save graph");
        fileChooser.setInitialFileName("graph-" + LocalDate.now() + ".txt");
        File file = fileChooser.showSaveDialog(mainStage);
        if (file != null) {
            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))) {
                bufferedWriter.write(pathFindingController.exportGraphToString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @FXML
    private void onClearGraphButtonClick() {
        this.runningAlgorithmThread.set(null);
        this.resetButton.setDisable(true);
        this.pathFindingController.clearAlgorithmSpace();
    }

}
