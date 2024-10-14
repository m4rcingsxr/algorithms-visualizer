package com.marcinseweryn.visualizer.controller;

import com.marcinseweryn.visualizer.model.Algorithm;
import com.marcinseweryn.visualizer.model.GraphAlgorithm;
import com.marcinseweryn.visualizer.model.SortingAlgorithm;
import com.marcinseweryn.visualizer.view.SortingRectangles;
import javafx.animation.PauseTransition;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.joining;

public class SortingController {

    private static final Logger logger = LogManager.getLogger(SortingController.class);

    private final VBox algorithmSpace;
    private final ListView<SimpleStringProperty> pseudoCodeList;
    private final TextField sortInput;
    private final ChoiceBox<Algorithm> algorithmListBox;
    private List<Integer> unsortedList;
    private final Random random = new Random();

    private final PauseTransition pauseTransition = new PauseTransition(Duration.millis(500));

    private final SimpleStringProperty sortInputProperty = new SimpleStringProperty();

    private SortingRectangles sortingRectangles;

    public SortingController(VBox algorithmSpace, TextField sortInput, ChoiceBox<Algorithm> algorithmListBox, ListView<SimpleStringProperty> pseudoCodeList) {
        this.algorithmSpace = algorithmSpace;
        this.pseudoCodeList = pseudoCodeList;
        this.sortInput = sortInput;
        this.algorithmListBox = algorithmListBox;

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

        sortingRectangles = new SortingRectangles(unsortedList, max.get(), min.get(), algorithmSpace.getHeight(), algorithmSpace.getWidth());
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

    public Optional<SortingAlgorithm> initializeSelectedAlgorithm() {
        if (this.algorithmListBox.getValue() instanceof SortingAlgorithm selectedAlgorithm) {
            try {
                return Optional.of(selectedAlgorithm.getClass()
                                           .getDeclaredConstructor(
                                                   ListView.class, SortingRectangles.class
                                           )
                                           .newInstance(
                                                   pseudoCodeList, sortingRectangles
                                           ));
            } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException |
                     InstantiationException e) {
                throw new RuntimeException(e);
            }
        }

        return Optional.empty();
    }

    public void resetListState() {

    }
}
