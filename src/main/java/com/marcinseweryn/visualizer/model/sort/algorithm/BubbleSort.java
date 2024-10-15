package com.marcinseweryn.visualizer.model.sort.algorithm;

import com.marcinseweryn.visualizer.model.sort.SortingAlgorithm;
import com.marcinseweryn.visualizer.view.SortingRectangles;
import javafx.scene.control.ListView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Implementation of the Bubble Sort algorithm.
 * This class extends {@link SortingAlgorithm} and provides step-by-step visualization
 * for the Bubble Sort process, including comparison, swapping, and marking sorted elements.
 */
public class BubbleSort extends SortingAlgorithm {

    private static final Logger logger = LogManager.getLogger(BubbleSort.class);

    /**
     * Constructor for initializing the BubbleSort algorithm with the necessary UI components.
     *
     * @param pseudocodeList     ListView for displaying the pseudocode steps.
     * @param sortingRectangles  Visual representation of the sorting rectangles.
     */
    public BubbleSort(ListView<String> pseudocodeList, SortingRectangles sortingRectangles) {
        super(pseudocodeList, sortingRectangles);
        logger.debug("BubbleSort algorithm initialized.");
    }

    /**
     * Executes the Bubble Sort algorithm with step-by-step visualization.
     * The algorithm compares adjacent elements and swaps them if necessary, marking sorted elements.
     */
    @Override
    public void executeAlgorithm() {
        logger.info("Executing BubbleSort algorithm.");
        pauseAtStep(0);  // Initial step, visual preparation
        pauseAtStep(1);  // Beginning of the outer loop

        for (int i = 0; i < sortedList.size() - 1; i++) {
            logger.debug("Outer loop iteration i = {}", i);
            pauseAtStep(2);  // Outer loop logic

            for (int j = 0; j < sortedList.size() - i - 1; j++) {
                logger.debug("Comparing elements at indices {} and {}", j, j + 1);
                setComparisonStyle(j);
                setComparisonStyle(j + 1);
                pauseAtStep(3);  // Comparison step

                if (sortedList.get(j) > sortedList.get(j + 1)) {
                    logger.debug("Swapping elements at indices {} and {}", j, j + 1);
                    pauseAtStep(4);  // Swap step
                    swapWithAnimation(j, j + 1);  // Perform the swap with animation
                }

                // Reset styles after comparison
                resetRectangleStyle(j);
                resetRectangleStyle(j + 1);
            }

            // Mark the element at the end of the current pass as sorted
            setSorted(sortedList.size() - i - 1);
            logger.debug("Element at index {} marked as sorted.", sortedList.size() - i - 1);
        }

        // Mark the first element as sorted after the final pass
        setSorted(0);
        logger.debug("All elements sorted.");
    }

    /**
     * Sets the pseudocode for the Bubble Sort algorithm.
     * The pseudocode outlines the steps of the algorithm for display in the UI.
     */
    @Override
    public void setPseudocode() {
        this.pseudocode.addAll(
                "bubbleSort(arr[]):",
                "\tfor i from 0 to length of arr - 1:",
                "\t\tfor j from 0 to length of arr - i - 1:",
                "\t\t\tif arr[j] > arr[j + 1]:",
                "\t\t\t\tswap(arr[j], arr[j + 1])"
        );
    }
}
