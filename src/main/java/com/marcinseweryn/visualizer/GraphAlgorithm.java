package com.marcinseweryn.visualizer;

import com.marcinseweryn.visualizer.view.GraphNode;
import javafx.application.Platform;
import javafx.scene.control.ListView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public abstract class GraphAlgorithm {

    public static Logger logger = LogManager.getLogger(GraphAlgorithm.class);

    protected GraphNode startVertex;
    protected GraphNode destinationVertex;

    private List<GraphNode> path = new ArrayList<>();

    private boolean isStepDisabled = false;
    private final ReentrantLock lock = new ReentrantLock();

    private final Condition stepCondition = lock.newCondition();
    private volatile boolean isStopped;

    protected NodeVisualizer candidateNodes;
    protected NodeVisualizer visitedNodes;

    private Publisher publisher;
    private GraphNode currentVertex;
    private GraphNode neighbourVertex;


    protected GraphAlgorithm() {
    }

    protected GraphAlgorithm(Publisher publisher) {
        this.publisher = publisher;
    }

    void start(GraphNode startVertex, GraphNode destinationVertex, boolean isStepDisabled, ListView candidateNodes, ListView visitedNodes) {
        this.candidateNodes = new Queue(NodeVisualizer.TYPE_CANDIDATE_NODES, candidateNodes, visitedNodes);
        this.visitedNodes = new Stack(NodeVisualizer.TYPE_VISITED, candidateNodes, visitedNodes);

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
                n.getStyleClass().removeAll("start", "destination", "visited", "pending-nodes", "path");
                n.getStyleClass().add("path");
            });
        }
    }

    public void resumeAlgorithm() {
        if(!this.isStepDisabled) {
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
    }

    protected void step(int stepNum) {
        if(!this.isStepDisabled) {
            lock.lock();
            try {
                logger.debug("Lock acquired, about to wait at step: " + stepNum);

                isStopped = true;
                while (isStopped) {
                    stepCondition.await();  // Wait for the signal
                }

                logger.debug("Resumed from waiting at step: " + stepNum);
            } catch (InterruptedException e) {
                logger.error("Thread interrupted while waiting", e);
                Thread.currentThread().interrupt();
            } finally {
                logger.debug("Lock released after step: " + stepNum);
                lock.unlock();
            }
        } else {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                logger.error("Thread interrupted while waiting", e);
                Thread.currentThread().interrupt();
            }
        }
    }

    protected void setCurrent(GraphNode currentVertex) {
        if (this.currentVertex != null) //set previous value to false first
            this.currentVertex.pseudoClassStateChanged(PathFindingController.ps_currentNode, false);
        if(currentVertex != null) {
            currentVertex.pseudoClassStateChanged(PathFindingController.ps_currentNode, true);
        }
        this.currentVertex = currentVertex;
    }

    protected GraphNode getCurrent() {
        return currentVertex;
    }

    protected void setNeighbour(GraphNode neighbourVertex) {
        if (this.neighbourVertex != null) //set previous value to false first
            this.neighbourVertex.pseudoClassStateChanged(PathFindingController.ps_neighbourNode, false);
        if(neighbourVertex != null) {
            neighbourVertex.pseudoClassStateChanged(PathFindingController.ps_neighbourNode, true);
        }
        this.neighbourVertex = neighbourVertex;
    }

    protected GraphNode getNeighbour() {
        return neighbourVertex;
    }



}
