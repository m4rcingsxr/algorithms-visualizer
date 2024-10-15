package com.marcinseweryn.visualizer.model.sort;

import com.marcinseweryn.visualizer.model.Algorithm;
import com.marcinseweryn.visualizer.view.SortingRectangles;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for sorting algorithms.
 * Provides core functionality for visualizing sorting steps, swapping elements,
 * and managing rectangle comparisons during the sorting process.
 * This class extends {@link Algorithm} to leverage its step-by-step and continuous execution modes.
 */
public abstract class SortingAlgorithm extends Algorithm {

    private static final Logger logger = LogManager.getLogger(SortingAlgorithm.class);

    // Reference to the UI component that visualizes sorting rectangles
    protected final SortingRectangles sortingRectangles;

    //  list that will be used to follow algorithm logic, original remain the same
    protected final ArrayList<Integer> sortedList;

    /**
     * Constructor for initializing a SortingAlgorithm instance with the pseudocode list and sorting rectangles.
     *
     * @param pseudocodeList      The ListView displaying pseudocode steps for the algorithm.
     * @param sortingRectangles   The visual representation of the rectangles being sorted.
     */
    protected SortingAlgorithm(ListView<String> pseudocodeList, SortingRectangles sortingRectangles) {
        super(pseudocodeList);
        this.sortingRectangles = sortingRectangles;
        this.sortedList = new ArrayList<>(sortingRectangles.getUnsortedList());
        logger.debug("Initialized SortingAlgorithm with pseudocodeList and sortingRectangles.");
    }

    /**
     * Performs a swap animation between two elements at specified indices.
     *
     * @param i The index of the first element.
     * @param j The index of the second element.
     */
    protected void swapWithAnimation(int i, int j) {
        logger.debug("Executing swap animation between elements at indices {} and {}", i, j);
        Platform.runLater(() -> swap(sortingRectangles.getChildren(), i, j));
    }

    /**
     * Sets a specific rectangle's style to indicate it is being compared.
     *
     * @param index The index of the rectangle being compared.
     */
    protected void setComparisonStyle(int index) {
        logger.debug("Setting comparison style for rectangle at index {}", index);
        setRectangleStyle(index, "comparing-rectangle");
    }

    /**
     * Resets the style of a rectangle at the specified index.
     *
     * @param index The index of the rectangle to reset.
     */
    protected void resetRectangleStyle(int index) {
        logger.debug("Resetting style for rectangle at index {}", index);
        Platform.runLater(() -> sortingRectangles.resetStyles(index));
    }

    /**
     * Marks a rectangle as sorted by applying the "sorted" style.
     *
     * @param index The index of the rectangle to mark as sorted.
     */
    protected void setSorted(int index) {
        logger.debug("Marking rectangle at index {} as sorted", index);
        setRectangleStyle(index, "sorted-rectangle");
    }

    /**
     * Applies the specified style to a rectangle at the given index.
     *
     * @param index The index of the rectangle to style.
     * @param style The CSS style class to apply.
     */
    private void setRectangleStyle(int index, String style) {
        Platform.runLater(() -> {
            sortingRectangles.removeStyles(index);
            sortingRectangles.getChildren().get(index).getStyleClass().add(style);
        });
        logger.debug("Applied style {} to rectangle at index {}", style, index);
    }

    /**
     * Swaps two elements in the sorting list.
     *
     * @param sortedList The list containing the elements to swap.
     * @param i          The index of the first element.
     * @param j          The index of the second element.
     */
    private void swap(List<Node> sortedList, int i, int j) {
        logger.debug("Swapping elements at indices {} and {}", i, j);
        Node tempI = sortedList.get(i);
        Node tempJ = sortedList.get(j);
        sortedList.set(i, new VBox());  // Temporary VBox used to hold the position
        sortedList.set(j, new VBox());
        sortedList.set(i, tempJ);
        sortedList.set(j, tempI);

        // swap in logical list
        Integer temp = this.sortedList.get(i);
        this.sortedList.set(i, this.sortedList.get(j));
        this.sortedList.set(j, temp);

        logger.debug("Swap completed between indices {} and {}", i, j);
    }

}
