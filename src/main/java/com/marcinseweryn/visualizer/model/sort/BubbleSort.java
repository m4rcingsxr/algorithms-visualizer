package com.marcinseweryn.visualizer.model.sort;

import com.marcinseweryn.visualizer.model.SortingAlgorithm;
import com.marcinseweryn.visualizer.view.SortingRectangles;
import javafx.scene.control.ListView;

import java.util.ArrayList;
import java.util.List;

public class BubbleSort extends SortingAlgorithm {

    public BubbleSort() {
        super(null, null);
    }

    public BubbleSort(ListView<String> pseudocodeList, SortingRectangles sortingRectangles) {
        super(pseudocodeList, sortingRectangles);
    }

    @Override
    public String toString() {
        return "Bubble Sort";
    }

    @Override
    public void executeAlgorithm() {
        List<Integer> sortedList = new ArrayList<>(sortingRectangles.getUnsortedList());

        pauseAtStep(0);
        pauseAtStep(1);
        for (int i = 0; i < sortedList.size() - 1; i++) {
            pauseAtStep(2);
            for (int j = 0; j < sortedList.size() - i - 1; j++) {
                setComparison(j);
                setComparison(j + 1);
                pauseAtStep(3);
                if (sortedList.get(j) > sortedList.get(j + 1)) {
                    pauseAtStep(4);
                    swap(sortedList, j, j + 1);
                    swapAnimation(j, j + 1);
                }
                removeComparison(j);
                removeComparison(j + 1);
            }

            setSorted(sortedList.size() - i - 1);
        }

        setSorted(0);
    }

    private void swap(List<Integer> sortedList, int i, int j) {
        Integer temp = sortedList.get(i);
        sortedList.set(i, sortedList.get(j));
        sortedList.set(j, temp);
    }

    @Override
    public void setPseudocode() {
        this.pseudocode.addAll(
                "bubbleSort(arr[])",
                "for (i = 0; i < arr.length - 1; i ++)",
                "   for (j = 0; j < arr.length - i; j++)",
                "       if (arr[j] > arr[j+1])",
                "           swap(arr, j, j + 1)"
        );
    }
}
