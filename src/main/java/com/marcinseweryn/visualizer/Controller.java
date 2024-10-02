package com.marcinseweryn.visualizer;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Objects;
import java.util.Optional;

public class Controller {

    private static final Logger logger = LogManager.getLogger(Controller.class);

    // ===========================================
    // ================== GRAPH ==================
    // ===========================================
    @FXML
    TabPane algorithmTab;
    @FXML
    TabPane graphTab;
    @FXML
    Accordion vertexList;
    @FXML
    ToggleButton toggleWeight;
    @FXML
    ToggleButton toggleDistance;

    // ===========================================
    // ================== GRAPH TAB ==============
    // ===========================================
    @FXML
    ListView<SimpleStringProperty> candidateNodes;

    @FXML
    ListView<SimpleStringProperty> visitedNodes;

    // ===========================================
    // ================== SORT ===================
    // ===========================================

    // ===========================================
    // ================== COMMON =================
    // ===========================================
    @FXML
    ChoiceBox<Algorithm> algorithmList;
    @FXML
    VBox centerVBox;

    // ===========================================
    // ============= ALGORITHM SPACE =============
    // ===========================================
    @FXML
    AnchorPane graphPane;
    @FXML
    AnchorPane arrayPane;

    // ===========================================
    // =========== ALGORITHM CONTROLS ============
    // ===========================================
    @FXML
    public Button startBtn;
    @FXML
    public Button stepBtn;

    private PathFindingController findController;
    private SortingController sortController;
    private final SimpleObjectProperty<AlgorithmThread> resolveThread = new SimpleObjectProperty<>();

    @FXML
    public void initialize() {
        logger.info("Initializing Controller...");

        // Initialize controllers for path finding and sorting
        this.findController = new PathFindingController(this.graphPane, this.vertexList,
                                                        this.toggleWeight, this.toggleDistance,
                                                        this.candidateNodes, this.visitedNodes,
                                                        this.algorithmList
        );
        this.sortController = new SortingController(this.arrayPane);

        // Log once for both controllers initialization (if needed)
        logger.debug("PathFindingController and SortingController initialized.");

        arrayPane.setVisible(true);
        graphPane.setVisible(true);
        centerVBox.getChildren().remove(arrayPane);
        logger.debug("Graph space view selected.");

        loadUserAlgorithms("com/marcinseweryn/visualizer/model/path");

        // Show correct algorithm space based on selected tab
        algorithmTab.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldTab, newTab) -> {
                    String tabName = newTab.getText();
                    logger.info("{} tab selected.", tabName);

                    // Manage panes visibility based on the tab selected
                    if (tabName.equals("Path Finding")) {
                        centerVBox.getChildren().remove(arrayPane);
                        if (!centerVBox.getChildren().contains(graphPane)) {
                            centerVBox.getChildren().add(graphPane);
                        }

                        loadUserAlgorithms("com/marcinseweryn/visualizer/model/path");
                    } else if (tabName.equals("Sorting")) {
                        centerVBox.getChildren().remove(graphPane);
                        if (!centerVBox.getChildren().contains(arrayPane)) {
                            centerVBox.getChildren().add(arrayPane);
                        }

                        loadUserAlgorithms("com/marcinseweryn/visualizer/model/sort");
                    } else {
                        logger.warn("Unknown tab selected: {}", tabName);
                    }
                });

        logger.info("Controller initialization completed.");
    }

    private void loadUserAlgorithms(String path) {
        try {
            algorithmList.getItems().clear();
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Enumeration<URL> resources = classLoader.getResources(path);
            File dir = new File(resources.nextElement().getFile());

            ArrayList<Class<?>> classes = new ArrayList<>();

            // If a directory name contains a space, the above code replaces it with "%20". The
            // following changes it back.
            dir = new File(dir.toString().replace("%20", " "));

            if (dir.listFiles() != null) {
                for (File file : Objects.requireNonNull(dir.listFiles())) {
                    if (file != null && file.getName().endsWith(".class")) {
                        // Convert path with slashes to fully qualified class name with dots
                        String className = path.replace('/', '.') + '.' + file.getName().replace(
                                ".class", "");
                        classes.add(Class.forName(className));
                    }
                }
                for (Class<?> c : classes) {
                    Object a = c.getDeclaredConstructor().newInstance();
                    if (a instanceof Algorithm algorithm) {
                        algorithmList.getItems().add(algorithm);
                    }
                }
            }

            algorithmList.getSelectionModel().selectFirst();
        } catch (IOException | ClassNotFoundException | InvocationTargetException |
                 InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            logger.error(e);
        }
    }

    @FXML
    private void clickStartAlgorithm(ActionEvent actionEvent) {
        boolean isStepDisabled = actionEvent.getSource() == startBtn;

        // Check if a thread is running or if we need to start/resume it
        if (this.resolveThread.get() == null || isStepDisabled) {

            if (this.algorithmTab.getSelectionModel().getSelectedItem().getText().equals(
                    "Path Finding")) {
                Optional<GraphAlgorithm> graphAlgorithm = findController.initializeSelectedAlgorithm();

                if(graphAlgorithm.isEmpty()) {
                    throw new RuntimeException("Initialization of algorithm goes wrong...");
                }

                this.graphTab.getSelectionModel().selectFirst();
                this.resolveThread.set(new AlgorithmThread(() -> {
                    graphAlgorithm.get().start(isStepDisabled);
                    this.resolveThread.set(null);
                }, graphAlgorithm.get()));
            } else {
                // Sorting logic
            }

            resolveThread.get().start();
        } else {
            // Algorithm is paused, so resume it
            logger.debug("Resuming algorithm...");
            resolveThread.get().resumeAlgorithm();
        }
    }

    @FXML
    private void onGraphPaneMousePressed(MouseEvent mouseEvent) {
        this.findController.onAlgorithmSpaceMousePressed(mouseEvent);
    }

    @FXML
    private void onGraphPaneDragDetected(MouseEvent mouseEvent) {
        this.findController.onAlgorithmSpaceDragDetected(mouseEvent);
    }

    @FXML
    private void onGraphPaneMouseDragged(MouseEvent mouseEvent) {
        this.findController.onAlgorithmSpaceMouseDragged(mouseEvent);
    }

    @FXML
    private void onGraphPaneMouseReleased(MouseEvent mouseEvent) {
        this.findController.onAlgorithmSpaceMouseReleased(mouseEvent);
    }

    @FXML
    private void onGraphPaneDragOver(DragEvent dragEvent) {
        this.findController.onAlgorithmSpaceDragOver(dragEvent);
    }

    @FXML
    private void onGraphPaneDragDropped(DragEvent dragEvent) {
        this.findController.onAlgorithmSpaceDragDropped(dragEvent);
    }

}
