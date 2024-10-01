package com.marcinseweryn.visualizer;

import com.marcinseweryn.visualizer.view.GraphNode;
import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public abstract class GraphAlgorithm {

    public static Logger logger = LogManager.getLogger(GraphAlgorithm.class);

    private Publisher publisher;

    protected GraphNode startVertex;
    protected GraphNode destinationVertex;
    protected GraphNode currentVertex;
    private GraphNode neighbourVertex;


    private List<GraphNode> path = new ArrayList<>();

    private boolean isStepDisabled = false;
    private final ReentrantLock lock = new ReentrantLock();

    private final Condition stepCondition = lock.newCondition();
    private volatile boolean isStopped;

    protected GraphAlgorithm() {
    }

    protected GraphAlgorithm(Publisher publisher) {
        this.publisher = publisher;
    }

    void start(GraphNode startVertex, GraphNode destinationVertex, boolean isStepDisabled) {
        this.startVertex = startVertex;
        this.destinationVertex = destinationVertex;

        this.isStepDisabled = isStepDisabled;

        resolve();
    }

    public abstract void resolve();


    // used by single thread - not modified by multiple threads
    protected void addToPath(GraphNode node) {
        path.add(node);
    }

    protected void drawPath() {
        for (int i = 0; i < path.size(); i++) {
            GraphNode n = path.get(i);
            Platform.runLater(() -> {
                n.getStyleClass().add("path");
            });
        }
    }

    public void resumeAlgorithm() {
        lock.lock();
        try {
            logger.debug("Lock acquired, signaling condition to resume algorithm...");
            stepCondition.signalAll();  // Signal the waiting thread
            isStopped = false;
            logger.debug("Condition signaled");
        } finally {
            logger.debug("Lock released after signaling");
            lock.unlock();
        }

        try {
            Thread.sleep(100);  // Add a short delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    protected void step(int stepNum) {
        lock.lock();
        try {
            logger.debug("Lock acquired, about to wait at step: " + stepNum);

            isStopped = true;
            while (isStopped) {
                stepCondition.await();  // Wait for the signal
            }

            logger.debug("Resumed from waiting at step: " + stepNum);
            Thread.sleep(5);  // Simulate step delay

        } catch (InterruptedException e) {
            logger.error("Thread interrupted while waiting", e);
            Thread.currentThread().interrupt();
        } finally {
            logger.debug("Lock released after step: " + stepNum);
            lock.unlock();
        }
    }

}
