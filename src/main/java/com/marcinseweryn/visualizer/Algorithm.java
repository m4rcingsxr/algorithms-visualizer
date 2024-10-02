package com.marcinseweryn.visualizer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public abstract class Algorithm {

    public static final Logger logger = LogManager.getLogger(Algorithm.class);

    protected boolean isStepDisabled = false;
    private final ReentrantLock lock = new ReentrantLock();

    private final Condition stepCondition = lock.newCondition();
    private volatile boolean isStopped;

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

    public void start(boolean isStepDisabled) {
        this.isStepDisabled = isStepDisabled;
        resolve();
    }

    public abstract void resolve();

}
