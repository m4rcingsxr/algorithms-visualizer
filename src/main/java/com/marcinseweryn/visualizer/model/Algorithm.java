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
 * Abstract base class for algorithms that support step-by-step and continuous execution modes.
 * It provides pause and resume functionality using a thread-safe lock mechanism, allowing
 * algorithms to either run continuously or wait at certain steps for user interaction.
 *
 * This class applies the Template Method design pattern. The general structure of the algorithm
 * execution is defined here, but the concrete steps of the algorithm (`executeAlgorithm` and `setPseudocode`)
 * are delegated to subclasses.
 */
public abstract class Algorithm {

    private static final Logger logger = LogManager.getLogger(Algorithm.class);

    // The ListView UI component that displays pseudocode steps for the algorithm
    private final ListView<String> pseudocodeList;

    // Observable list that holds pseudocode steps, displayed in the pseudocodeList UI
    protected final ObservableList<String> pseudocode = FXCollections.observableArrayList();

    // Determines if the algorithm runs in continuous execution mode (true) or step-by-step (false)
    private boolean isContinuousMode = false;

    // Thread control mechanism to pause and resume execution
    private final ReentrantLock executionLock = new ReentrantLock();
    private final Condition canProceed = executionLock.newCondition();

    // Flag indicating whether the algorithm is currently paused
    private boolean isPaused;

    /**
     * Constructor to initialize the Algorithm object with a ListView to display pseudocode.
     * The pseudocodeList is set to update with pseudocode steps in a thread-safe manner.
     *
     * @param pseudocodeList The ListView to display algorithm pseudocode.
     */
    protected Algorithm(ListView<String> pseudocodeList) {
        this.pseudocodeList = pseudocodeList;
        if (pseudocodeList != null) {
            // Ensuring the UI is updated in the JavaFX application thread
            Platform.runLater(() -> pseudocodeList.setItems(pseudocode));
        }
    }

    /**
     * Starts the algorithm execution.
     * The execution mode is determined by the provided flag (continuous or step-by-step).
     *
     * @param continuousMode If true, the algorithm will run continuously without pauses.
     */
    public void start(boolean continuousMode) {
        this.isContinuousMode = continuousMode;
        logger.info("Starting algorithm execution in {} mode.", continuousMode ? "continuous" : "step-by-step");

        // Initialize the pseudocode for display
        Platform.runLater(this::setPseudocode);
        executeAlgorithm();  // Begin executing the algorithm's logic
    }

    /**
     * Resumes the algorithm execution if it is paused in step-by-step mode.
     * Signals the waiting thread to continue execution by unlocking the pause condition.
     */
    public void resumeAlgorithm() {
        if (!this.isContinuousMode) {
            logger.debug("Attempting to resume algorithm execution...");

            executionLock.lock();
            try {
                logger.debug("Lock acquired, signaling all waiting threads to resume.");
                canProceed.signalAll();
                isPaused = false;
            } finally {
                logger.debug("Lock released after signaling.");
                executionLock.unlock();
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                logger.error("Interrupted while sleeping after resume.", e);
                Thread.currentThread().interrupt();
            }
        } else {
            logger.debug("Resume ignored because the algorithm is in continuous mode.");
        }
    }

    /**
     * Pauses the algorithm at a specific step when in step-by-step mode.
     * In continuous mode, a brief delay is introduced instead of pausing.
     *
     * @param stepNumber The current step number where the algorithm is pausing.
     */
    protected void pauseAtStep(int stepNumber) {
        // Scroll and select the current step in the UI's pseudocode ListView
        if (pseudocodeList != null) {
            Platform.runLater(() -> {
                pseudocodeList.scrollTo(stepNumber);
                pseudocodeList.getSelectionModel().select(stepNumber);
            });
        }

        if (!isContinuousMode) {
            executionLock.lock();
            try {
                logger.debug("Pausing execution at step: {}", stepNumber);
                isPaused = true;

                // Loop to prevent spurious wakeup
                while (isPaused) {
                    canProceed.await();
                }

                logger.debug("Resuming execution after step: {}", stepNumber);
            } catch (InterruptedException e) {
                logger.error("Thread interrupted while paused at step: {}", stepNumber, e);
                Thread.currentThread().interrupt();
            } finally {
                logger.debug("Lock released after resuming from step: {}", stepNumber);
                executionLock.unlock();
            }
        } else {
            // In continuous mode, add a brief delay between steps to slow down execution
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                logger.error("Thread interrupted during delay in continuous mode", e);
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Abstract method for algorithm logic execution.
     * Subclasses must implement the algorithm's steps and call {@link #pauseAtStep(int)} where necessary.
     */
    public abstract void executeAlgorithm();

    /**
     * Abstract method to set the pseudocode steps for the algorithm.
     * Subclasses must provide the list of pseudocode steps for display in the UI.
     */
    public abstract void setPseudocode();

}
