package com.marcinseweryn.visualizer.model.sort;

import com.marcinseweryn.visualizer.GraphAlgorithm;
import com.marcinseweryn.visualizer.Publisher;

public class BubbleSort extends GraphAlgorithm {

    public BubbleSort() {}

    protected BubbleSort(Publisher publisher) {
        super(publisher);
    }

    @Override
    public String toString() {
        return "Bubble Sort";
    }

    @Override
    public void resolve() {

    }
}
