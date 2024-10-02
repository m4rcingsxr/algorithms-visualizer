package com.marcinseweryn.visualizer.controller;

import com.marcinseweryn.visualizer.model.Algorithm;

/**
 * A custom thread class for running algorithms in the visualizer.
 * It extends the standard Java {@link Thread} class and associates the thread with an algorithm instance.
 * This class is used to start or resume the execution of an algorithm in a separate thread.
 */
public class AlgorithmThread extends Thread {

    // The algorithm associated with this thread, providing control over its execution.
    private final Algorithm algorithm;

    /**
     * Constructs a new AlgorithmThread instance.
     * The thread is initialized with a Runnable and an associated Algorithm instance.
     *
     * @param runnable  The runnable task to be executed in the thread, typically starting the algorithm.
     * @param algorithm The algorithm that this thread will control.
     */
    public AlgorithmThread(Runnable runnable, Algorithm algorithm) {
        super(runnable); // Calls the Thread constructor with the runnable task
        this.algorithm = algorithm;
    }

    /**
     * Resumes the execution of the algorithm.
     * This method is intended to be called when the algorithm is paused and needs to be resumed.
     */
    public void resumeAlgorithm() {
        this.algorithm.resumeAlgorithm(); // Calls the resume function in the associated algorithm
    }

}
