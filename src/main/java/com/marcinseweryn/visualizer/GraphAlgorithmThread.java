package com.marcinseweryn.visualizer;

public class GraphAlgorithmThread extends Thread {

    private final GraphAlgorithm algorithm;
    private final boolean isStepDisabled;

    public GraphAlgorithmThread(Runnable runnable, GraphAlgorithm algorithm,  boolean isStepDisabled) {
        super(runnable);
        this.algorithm = algorithm;
        this.isStepDisabled = isStepDisabled;
    }

    public GraphAlgorithm getAlgorithm() {
        return algorithm;
    }

}
