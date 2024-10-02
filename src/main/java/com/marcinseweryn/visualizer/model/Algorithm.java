package com.marcinseweryn.visualizer.model;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Abstract class representing a general algorithm with step-by-step and continuous execution modes.
 * Provides functionality to pause and resume execution using a lock mechanism.
 */
public abstract class Algorithm {

    public static final Logger logger = LogManager.getLogger(Algorithm.class);
    private final ListView<String> pseudocodeList;

    // Flag to determine if step mode is disabled (continuous execution)
    protected boolean isContinuousMode = false;

    // Lock and condition variables for managing thread control
    private final ReentrantLock executionLock = new ReentrantLock();
    private final Condition canProceed = executionLock.newCondition();

    protected final ObservableList<String> pseudocode = FXCollections.observableArrayList();

    // Flag to indicate if the algorithm is currently paused
    private volatile boolean isPaused;

    protected Algorithm(ListView<String> pseudocodeList) {
        this.pseudocodeList = pseudocodeList;
        if(pseudocodeList != null) {
            Platform.runLater(() -> pseudocodeList.setItems(pseudocode));
        }
    }

    /**
     * Resumes the algorithm if it is in step mode.
     * Signals the waiting thread to continue execution.
     */
    public void resumeAlgorithm() {
        if (!this.isContinuousMode) {
            executionLock.lock();
            try {
                logger.debug("Lock acquired, signaling condition to resume algorithm...");
                canProceed.signalAll();
                isPaused = false;
                logger.debug("Condition signaled");
            } finally {
                logger.debug("Lock released after signaling");
                executionLock.unlock();
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Pauses the execution at a specific step when in step-by-step mode.
     * The method waits for a signal before resuming.
     *
     * @param stepNumber The current step number where the algorithm pauses.
     */
    protected void pauseAtStep(int stepNumber) {
        Platform.runLater(() -> {
            pseudocodeList.scrollTo(stepNumber);
            pseudocodeList.getSelectionModel().select(stepNumber);
        });
        if (!isContinuousMode) {
            executionLock.lock();
            try {
                logger.debug("Pausing at step: " + stepNumber);
                isPaused = true;

                // Wait until a signal is received to resume
                while (isPaused) {
                    canProceed.await();
                }

                logger.debug("Resumed from pause at step: " + stepNumber);
            } catch (InterruptedException e) {
                logger.error("Thread interrupted while pausing at step: " + stepNumber, e);
                Thread.currentThread().interrupt();
            } finally {
                logger.debug("Lock released after step: " + stepNumber);
                executionLock.unlock();
            }
        } else {
            // If continuous mode is enabled, introduce a short delay between steps
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                logger.error("Thread interrupted during continuous mode delay", e);
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Starts the algorithm, setting the mode of execution based on the provided flag.
     *
     * @param continuousMode If true, the algorithm runs continuously without pausing at steps.
     */
    public void start(boolean continuousMode) {
        this.isContinuousMode = continuousMode;
        setPseudocode();
        executeAlgorithm();  // Begin the algorithm's execution
    }

    /**
     * Abstract method to be implemented by subclasses to define the algorithm's logic.
     * The implementation should call {@link #pauseAtStep(int)} at appropriate points
     * in the algorithm to enable step-by-step execution.
     */
    public abstract void executeAlgorithm();

    public abstract void setPseudocode();

}
