package com.marcinseweryn.visualizer;

import com.marcinseweryn.visualizer.view.GraphNode;

public class AlgorithmThread extends Thread {

    private final Algorithm algorithm;

    public AlgorithmThread(Runnable runnable, Algorithm algorithm) {
        super(runnable);
        this.algorithm = algorithm;
    }



    public void resumeAlgorithm() {
        this.algorithm.resumeAlgorithm();
    }

}
