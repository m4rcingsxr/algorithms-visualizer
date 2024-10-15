package com.marcinseweryn.visualizer.controller;

import com.marcinseweryn.visualizer.model.sort.SortingAlgorithm;
import com.marcinseweryn.visualizer.view.SortingRectangles;
import javafx.animation.PauseTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.joining;

public class SortTabController {

    private static final Logger logger = LogManager.getLogger(SortTabController.class);

    @FXML
    private ListView<SimpleStringProperty> pseudoCodeListSort;
    @FXML
    private TabPane sortTab;
    @FXML
    private TextField sortInput;

    private List<Integer> unsortedList;

    private final Random random = new Random();

    private final PauseTransition pauseTransition = new PauseTransition(Duration.millis(500));

    private final SimpleStringProperty sortInputProperty = new SimpleStringProperty();

    private SortingRectangles sortingRectangles;
    private MainController mainController;
    private VBox algorithmSpace;

    public SortTabController() {
    }

    @FXML
    public void initialize() {
        sortInputProperty.bindBidirectional(sortInput.textProperty());
        sortInputProperty.addListener(((observable, oldValue, newValue) -> {
            pauseTransition.setOnFinished(event -> updateAlgorithmSpace(newValue));
            pauseTransition.playFromStart();
        }));
    }

    private void updateAlgorithmSpace(String newVal) {
        if (newVal.isEmpty() || newVal.isBlank()) return;

        // Clear previous rectangles
        algorithmSpace.getChildren().clear();

        // Parse CSV to list
        String[] split = newVal.split(",");
        unsortedList = Arrays.stream(split).map(Integer::parseInt).toList();

        if (unsortedList.isEmpty()) {
            return;
        }

        // Render list view
        Optional<Integer> max = unsortedList.stream().max(Comparator.naturalOrder());
        Optional<Integer> min = unsortedList.stream().min(Comparator.naturalOrder());

        if (max.isEmpty()) {
            throw new IllegalStateException();
        }

        sortingRectangles = new SortingRectangles(unsortedList, max.get(), min.get(), algorithmSpace.getHeight(),
                                                  algorithmSpace.getWidth()
        );
        algorithmSpace.getChildren().add(sortingRectangles);
    }

    public void clearAlgorithmSpace() {
        algorithmSpace.getChildren().clear();
        this.unsortedList = new ArrayList<>();
    }

    public void generateUnsortedList(int noElements) {
        sortInputProperty.set(IntStream
                                      .range(0, noElements)
                                      .map(i -> random.nextInt(100))
                                      .mapToObj(String::valueOf)
                                      .collect(joining(","))
        );
    }

    /**
     * Initializes the selected sorting algorithm from the algorithm list.
     * It dynamically creates a new instance of the selected SortingAlgorithm using reflection.
     *
     * @return Optional of SortingAlgorithm if an algorithm is selected and instantiated successfully, otherwise empty Optional.
     */
    public Optional<SortingAlgorithm> initializeSelectedAlgorithm() {
        // Get the selected class name from the ChoiceBox
        String selectedClassName = this.mainController.getAlgorithmChoiceBox().getValue();

        if (selectedClassName != null && !selectedClassName.isEmpty()) {
            try {
                // Load the class dynamically using reflection
                Class<?> algorithmClass = Class.forName(selectedClassName);

                // Ensure that the class is a subtype of SortingAlgorithm
                if (SortingAlgorithm.class.isAssignableFrom(algorithmClass)) {
                    // Create a new instance of the selected SortingAlgorithm using its constructor
                    SortingAlgorithm algorithmInstance = (SortingAlgorithm) algorithmClass
                            .getDeclaredConstructor(ListView.class, SortingRectangles.class)
                            .newInstance(pseudoCodeListSort, sortingRectangles);

                    return Optional.of(algorithmInstance);  // Return the created algorithm instance
                } else {
                    logger.error("Selected class {} is not a subclass of SortingAlgorithm", selectedClassName);
                }
            } catch (ClassNotFoundException e) {
                logger.error("Algorithm class {} not found", selectedClassName, e);
            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                logger.error("Failed to instantiate algorithm class {}", selectedClassName, e);
            }
        }

        return Optional.empty();  // Return an empty Optional if no valid algorithm is selected
    }


    public void resetListState() {

    }

    public void selectAlgorithmTab() {
        sortTab.getSelectionModel().selectFirst();
    }


    @FXML
    public void onClickGenerateBigUnsortedList(ActionEvent actionEvent) {
        clearAlgorithmSpace();
        generateUnsortedList(100);
    }

    @FXML
    public void onClickGenerateSmallUnsortedList(ActionEvent actionEvent) {
        clearAlgorithmSpace();
        generateUnsortedList(10);
    }

    public void injectController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setAlgorithmSpace(VBox algorithmSpace) {
        this.algorithmSpace = algorithmSpace;
    }
}
