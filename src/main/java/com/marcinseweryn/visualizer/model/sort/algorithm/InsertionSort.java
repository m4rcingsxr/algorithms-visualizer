package com.marcinseweryn.visualizer.model.sort.algorithm;

import com.marcinseweryn.visualizer.model.sort.SortingAlgorithm;
import com.marcinseweryn.visualizer.view.SortingRectangles;
import javafx.scene.control.ListView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Implementation of the Insertion Sort algorithm.
 * This class extends {@link SortingAlgorithm} and provides step-by-step visualization
 * for the Insertion Sort process, including shifting elements and inserting into the correct position.
 */
public class InsertionSort extends SortingAlgorithm {

    private static final Logger logger = LogManager.getLogger(InsertionSort.class);

    /**
     * Constructor for initializing the InsertionSort algorithm with the necessary UI components.
     *
     * @param pseudocodeList    ListView for displaying the pseudocode steps.
     * @param sortingRectangles Visual representation of the sorting rectangles.
     */
    public InsertionSort(ListView<String> pseudocodeList, SortingRectangles sortingRectangles) {
        super(pseudocodeList, sortingRectangles);
        logger.debug("InsertionSort algorithm initialized.");
    }

    /**
     * Executes the Insertion Sort algorithm with step-by-step visualization.
     * The algorithm inserts each element into its correct position in the sorted portion of the array.
     */
    @Override
    public void executeAlgorithm() {
        logger.info("Executing InsertionSort algorithm.");
        pauseAtStep(0); // Initial step, visual preparation

        pauseAtStep(1); // Outer loop iterating through the unsorted portion of the array
        for (int i = 1; i < sortedList.size(); i++) {
            pauseAtStep(2);
            setComparisonStyle(i);  // Highlight the current element being compared

            int key = sortedList.get(i);  // Store the key value to be inserted
            logger.debug("Outer loop iteration i = {}, key = {}", i, key);
            pauseAtStep(3);

            int j = i - 1;

            // Shifting elements greater than key to the right
            pauseAtStep(4);
            while (j >= 0 && sortedList.get(j) > key) {
                logger.debug("Shifting element at index {} to index {}", j, j + 1);
                setComparisonStyle(j);  // Highlight the element being shifted

                pauseAtStep(5);
                setWithAnimation(j + 1, sortedList.get(j));  // Visualize the shift
                resetRectangleStyle(j);  // Reset the style for the current comparison

                pauseAtStep(6);
                j--;
            }

            // Insert the key at its correct position
            pauseAtStep(7);
            setWithAnimation(j + 1, key);  // Place the key in its correct position
            logger.debug("Inserting key = {} at index {}", key, j + 1);
            resetRectangleStyle(i);  // Reset the style of the inserted key for the next iteration
        }

        // After sorting is complete, mark all elements as sorted
        for (int k = 0; k < sortedList.size(); k++) {
            setSorted(k);
        }

        logger.debug("All elements sorted.");
    }


    /**
     * Sets the pseudocode for the Insertion Sort algorithm.
     * The pseudocode outlines the steps of the algorithm for display in the UI.
     */
    @Override
    public void setPseudocode() {
        this.pseudocode.addAll(
                "insertionSort(arr[]):",
                "\tfor i from 1 to length of arr:",
                "\t\tkey = arr[i]",
                "\t\tj = i - 1",
                "\t\twhile j >= 0 and arr[j] > key:",
                "\t\t\tarr[j + 1] = arr[j]",
                "\t\t\tj--",
                "\t\tarr[j + 1] = key"
        );
    }
}
