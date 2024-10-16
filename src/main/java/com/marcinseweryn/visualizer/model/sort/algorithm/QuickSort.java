package com.marcinseweryn.visualizer.model.sort.algorithm;

import com.marcinseweryn.visualizer.model.sort.SortingAlgorithm;
import com.marcinseweryn.visualizer.view.SortingRectangles;
import javafx.scene.control.ListView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Implementation of the Quick Sort algorithm.
 * This class extends {@link SortingAlgorithm} and provides step-by-step visualization
 * for the Quick Sort process, including partitioning, comparisons, and swaps.
 */
public class QuickSort extends SortingAlgorithm {

    private static final Logger logger = LogManager.getLogger(QuickSort.class);

    /**
     * Constructor for initializing the QuickSort algorithm with the necessary UI components.
     *
     * @param pseudocodeList    ListView for displaying the pseudocode steps.
     * @param sortingRectangles Visual representation of the sorting rectangles.
     */
    public QuickSort(ListView<String> pseudocodeList, SortingRectangles sortingRectangles) {
        super(pseudocodeList, sortingRectangles);
        logger.debug("QuickSort algorithm initialized.");
    }

    /**
     * Executes the Quick Sort algorithm with step-by-step visualization.
     * The algorithm uses partitioning to divide the list and recursively sort subarrays.
     */
    @Override
    public void executeAlgorithm() {
        logger.info("Executing QuickSort algorithm.");
        pauseAtStep(0);  // Initial step
        sort(0, sortedList.size() - 1);

        // Mark all rectangles as sorted after the algorithm finishes
        logger.debug("Marking all elements as sorted.");
        for (int i = 0; i < sortedList.size(); i++) {
            setSorted(i);
        }
        logger.info("QuickSort execution completed.");
    }

    /**
     * Recursively sorts the array by partitioning around a pivot element.
     *
     * @param left  The left index of the subarray.
     * @param right The right index of the subarray.
     */
    public void sort(int left, int right) {
        logger.debug("Sorting subarray from index {} to {}", left, right);
        pauseAtStep(1);
        if (left < right) {

            // Partition the array around a pivot element and get the pivot index
            pauseAtStep(2);
            int pivot = partition(left, right);
            logger.debug("Pivot element placed at index {}", pivot);
            setSorted(pivot);

            // Recursively sort the elements before and after the pivot
            logger.debug("Recursively sorting the left subarray from {} to {}", left, pivot - 1);
            pauseAtStep(3);
            sort(left, pivot - 1);

            logger.debug("Recursively sorting the right subarray from {} to {}", pivot + 1, right);
            pauseAtStep(4);
            sort(pivot + 1, right);
        }
    }

    /**
     * Partitions the array around the pivot element.
     *
     * @param left  The left index of the subarray.
     * @param right The right index of the subarray.
     * @return The index of the pivot element after partitioning.
     */
    private int partition(int left, int right) {
        logger.debug("Partitioning subarray from index {} to {}", left, right);
        pauseAtStep(6);

        // Set the pivot as the last element in the array
        int pivot = sortedList.get(right);
        logger.debug("Pivot selected: {} at index {}", pivot, right);
        setRectangleStyle(right, "pivot");

        pauseAtStep(7);
        int i = left - 1;  // Index of the smaller element

        // Iterate over the array and rearrange elements based on pivot
        logger.debug("Iterating over the subarray for partitioning.");
        pauseAtStep(8);
        for (int j = left; j < right; j++) {
            setComparisonStyle(j);
            logger.debug("Comparing element at index {} with pivot: {}", j, pivot);

            // If current element is smaller than or equal to pivot, swap it with the element at i
            pauseAtStep(9);
            if (sortedList.get(j) < pivot) {
                logger.debug("Element {} at index {} is less than pivot, swapping with index {}", sortedList.get(j), j, i + 1);
                pauseAtStep(10);

                i++;  // Increment index of smaller element
                pauseAtStep(11);
                swapWithAnimation(i, j);  // Swap the elements at indices i and j
                resetRectangleStyle(i);
            }

            resetRectangleStyle(j);
        }

        // Swap the pivot element with the element at index i + 1 to place it in the correct position
        pauseAtStep(12);
        logger.debug("Swapping pivot with element at index {}", i + 1);
        swapWithAnimation(i + 1, right);

        pauseAtStep(13);
        return i + 1;  // Return the pivot index
    }

    /**
     * Sets the pseudocode for the Quick Sort algorithm.
     * The pseudocode outlines the steps of the algorithm for display in the UI.
     */
    @Override
    public void setPseudocode() {
        this.pseudocode.addAll(
                "quickSort(arr[], left, right):",                      // 0
                "\tif left < right:",                                            // 1
                "\t\tpivot = partition(arr, left, right)",                       // 2
                "\t\tquickSort(arr, left, pivot - 1)",                           // 3
                "\t\tquickSort(arr, pivot + 1, right)",                          // 4

                "partition(arr[], left, right):",                                // 5
                "\tpivot = arr[right]",                                          // 6
                "\ti = left - 1",                                                // 7
                "\tfor j from left to right - 1:",                               // 8
                "\t\tif arr[j] < pivot:",                                        // 9
                "\t\t\ti++",                                                     // 10
                "\t\t\tswap(arr[i], arr[j])",                                    // 11
                "\tswap(arr[i + 1], arr[right])",                                // 12
                "\treturn i + 1"                                                 // 13
        );
    }

}
