package com.marcinseweryn.visualizer.model.path;

import com.marcinseweryn.visualizer.GraphAlgorithm;
import com.marcinseweryn.visualizer.Publisher;

public class DepthFirstSearch extends GraphAlgorithm {

    public DepthFirstSearch() {}

    protected DepthFirstSearch(Publisher publisher) {
        super(publisher);
    }

    @Override
    public String toString() {
        return "Depth First Search";
    }

    @Override
    public void resolve() {

    }
}
