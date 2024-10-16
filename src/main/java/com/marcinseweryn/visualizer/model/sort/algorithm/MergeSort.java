package com.marcinseweryn.visualizer.model.sort.algorithm;

import com.marcinseweryn.visualizer.model.sort.SortingAlgorithm;
import com.marcinseweryn.visualizer.view.SortingRectangles;
import javafx.scene.control.ListView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MergeSort extends SortingAlgorithm {

    private static final Logger logger = LogManager.getLogger(MergeSort.class);

    public MergeSort(ListView<String> pseudocodeList, SortingRectangles sortingRectangles) {
        super(pseudocodeList, sortingRectangles);
        logger.debug("MergeSort initialized.");
    }

    @Override
    public void executeAlgorithm() {
        logger.info("Starting MergeSort execution.");
        pauseAtStep(0);
        sort(0, sortedList.size() - 1);
        logger.info("MergeSort execution completed.");
    }

    public void sort(int start, int end) {
        logger.debug("Sorting subarray from index {} to {}", start, end);
        pauseAtStep(1);
        if (start < end) {
            pauseAtStep(2);
            int mid = (start + end) / 2;
            logger.debug("Dividing at index mid = {}", mid);
            pauseAtStep(3);
            sort(start, mid);
            pauseAtStep(4);
            sort(mid + 1, end);
            pauseAtStep(5);
            logger.debug("Merging subarrays from {} to {} and {} to {}", start, mid, mid + 1, end);
            merge(start, mid, end);
        }
    }

    private void merge(int left, int mid, int right) {
        logger.debug("Merging subarrays: left = {}, mid = {}, right = {}", left, mid, right);
        pauseAtStep(6);

        int length1 = mid - left + 1;
        int length2 = right - mid;

        logger.debug("Creating temporary arrays: leftArr[length1] = {}, rightArr[length2] = {}", length1, length2);
        pauseAtStep(7);

        int[] leftArr = new int[length1];
        int[] rightArr = new int[length2];

        setComparisonStyle(left, mid, right);  // Animate the division

        pauseAtStep(9);

        // Copy data to temporary arrays
        for (int i = 0; i < length1; i++) {
            leftArr[i] = sortedList.get(left + i);
            logger.trace("Copied element to leftArr[{}] = {}", i, leftArr[i]);
        }

        pauseAtStep(10);

        for (int i = 0; i < length2; i++) {
            rightArr[i] = sortedList.get(mid + 1 + i);
            logger.trace("Copied element to rightArr[{}] = {}", i, rightArr[i]);
        }

        pauseAtStep(11);
        int i = 0, j = 0, k = left;

        pauseAtStep(12);
        // Merge the temporary arrays back into the original array
        while (i < length1 && j < length2) {
            pauseAtStep(13);
            if (leftArr[i] <= rightArr[j]) {
                logger.debug("Placing leftArr[{}] = {} into sortedList[{}]", i, leftArr[i], k);
                pauseAtStep(15);
                setWithAnimation(k, leftArr[i]);
                setSorted(k);
                pauseAtStep(16);
                i++;
            } else {
                logger.debug("Placing rightArr[{}] = {} into sortedList[{}]", j, rightArr[j], k);
                pauseAtStep(18);
                setWithAnimation(k, rightArr[j]);
                setSorted(k);
                pauseAtStep(19);
                j++;
            }
            pauseAtStep(20);
            k++;
        }

        // Copy remaining elements of leftArr, if any
        pauseAtStep(21);
        while (i < length1) {
            logger.debug("Copying remaining leftArr[{}] = {} into sortedList[{}]", i, leftArr[i], k);
            pauseAtStep(22);
            setWithAnimation(k, leftArr[i]);
            setSorted(k);
            pauseAtStep(23);
            i++;
            k++;
        }

        // Copy remaining elements of rightArr, if any
        pauseAtStep(24);
        while (j < length2) {
            logger.debug("Copying remaining rightArr[{}] = {} into sortedList[{}]", j, rightArr[j], k);
            pauseAtStep(25);
            setWithAnimation(k, rightArr[j]);
            setSorted(k);
            pauseAtStep(26);
            j++;
            k++;
        }

        logger.debug("Merged subarray from {} to {}.", left, right);
    }

    @Override
    public void setPseudocode() {
        this.pseudocode.addAll(
                "mergeSort(arr[], start, end):",                           // 0
                "\tif start < end:",                                                 // 1
                "\t\tmid = (start + end) / 2",                                       // 2
                "\t\tmergeSort(arr, start, mid)",                                    // 3
                "\t\tmergeSort(arr, mid + 1, end)",                                  // 4
                "\t\tmerge(arr, start, mid, end)",                                   // 5

                "merge(arr[], left, mid, right):",                                   // 6
                "\tlength1 = mid - left + 1",                                        // 7
                "\tlength2 = right - mid",                                           // 8
                "\tCreate temporary arrays leftArr[length1] and rightArr[length2]",  // 9
                "\tCopy elements from arr[left..mid] to leftArr[]",                  // 10
                "\tCopy elements from arr[mid + 1..right] to rightArr[]",            // 11

                "\ti = 0, j = 0, k = left",                                          // 12
                "\twhile i < length1 and j < length2:",                              // 13
                "\t\tif leftArr[i] <= rightArr[j]:",                                 // 14
                "\t\t\tarr[k] = leftArr[i]",                                         // 15
                "\t\t\ti++",                                                         // 16
                "\t\telse:",                                                         // 17
                "\t\t\tarr[k] = rightArr[j]",                                        // 18
                "\t\t\tj++",                                                         // 19
                "\t\tk++",                                                           // 20

                "\twhile i < length1:",                                              // 21
                "\t\tarr[k] = leftArr[i]",                                           // 22
                "\t\ti++, k++",                                                      // 23

                "\twhile j < length2:",                                              // 24
                "\t\tarr[k] = rightArr[j]",                                          // 25
                "\t\tj++, k++"                                                       // 26
        );
    }


}
