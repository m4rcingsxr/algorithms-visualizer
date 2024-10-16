package com.marcinseweryn.visualizer.model.sort.algorithm;

import com.marcinseweryn.visualizer.model.sort.SortingAlgorithm;
import com.marcinseweryn.visualizer.view.SortingRectangles;
import javafx.scene.control.ListView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Implementation of the Selection Sort algorithm.
 * This class extends {@link SortingAlgorithm} and provides step-by-step visualization
 * for the Selection Sort process, including finding the minimum element and swapping it.
 */
public class SelectionSort extends SortingAlgorithm {

    private static final Logger logger = LogManager.getLogger(SelectionSort.class);

    /**
     * Constructor for initializing the SelectionSort algorithm with the necessary UI components.
     *
     * @param pseudocodeList    ListView for displaying the pseudocode steps.
     * @param sortingRectangles Visual representation of the sorting rectangles.
     */
    public SelectionSort(ListView<String> pseudocodeList, SortingRectangles sortingRectangles) {
        super(pseudocodeList, sortingRectangles);
        logger.debug("SelectionSort algorithm initialized.");
    }

    /**
     * Executes the Selection Sort algorithm with step-by-step visualization.
     * The algorithm selects the smallest element from the unsorted portion of the array and swaps it with the first unsorted element.
     */
    @Override
    public void executeAlgorithm() {
        logger.info("Executing SelectionSort algorithm.");
        pauseAtStep(0);  // Initial step, visual preparation
        pauseAtStep(1);  // Beginning of the outer loop

        for (int i = 0; i < sortedList.size() - 1; i++) {
            logger.debug("Outer loop iteration i = {}", i);
            pauseAtStep(2);  // Outer loop logic

            int minIndex = i;
            for (int j = i + 1; j < sortedList.size(); j++) {
                setComparisonStyle(minIndex);
                setComparisonStyle(j);
                logger.debug("Comparing elements at indices {} and {}", minIndex, j);
                pauseAtStep(3);  // Comparison step

                if (sortedList.get(j) < sortedList.get(minIndex)) {
                    logger.debug("New minimum found at index {}", j);
                    resetRectangleStyle(minIndex);
                    minIndex = j;
                    pauseAtStep(4);  // Update minimum
                }

                resetRectangleStyle(minIndex);
                resetRectangleStyle(j);
            }

            if (minIndex != i) {
                logger.debug("Swapping elements at indices {} and {}", i, minIndex);
                pauseAtStep(5);  // Swap step
                swapWithAnimation(i, minIndex);  // Perform the swap with animation
            }

            setSorted(i);
            logger.debug("Element at index {} marked as sorted.", i);
        }

        setSorted(sortedList.size() - 1);
        logger.debug("All elements sorted.");
    }

    /**
     * Sets the pseudocode for the Selection Sort algorithm.
     * The pseudocode outlines the steps of the algorithm for display in the UI.
     */
    @Override
    public void setPseudocode() {
        this.pseudocode.addAll(
                "selectionSort(arr[]):",
                "\tfor i from 0 to length of arr - 1:",
                "\t\tminIndex = i",
                "\t\tfor j from i + 1 to length of arr:",
                "\t\t\tif arr[j] < arr[minIndex]:",
                "\t\t\t\tminIndex = j",
                "\t\tif minIndex != i:",
                "\t\t\tswap(arr[i], arr[minIndex])"
        );
    }
}