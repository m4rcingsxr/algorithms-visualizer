package com.marcinseweryn.visualizer.model.path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

/**
 * A utility class to store and manage distances of nodes in a graph.
 * This class uses an internal list to dynamically manage distances,
 * returning Double.POSITIVE_INFINITY for uninitialized indices.
 */
public class DistanceList {

    private static final Logger logger = LogManager.getLogger(DistanceList.class);

    // Internal list to store distances
    private final ArrayList<Double> list;

    /**
     * Constructor to initialize the DistanceList with an empty list.
     */
    public DistanceList() {
        list = new ArrayList<>();
        logger.debug("DistanceList initialized with an empty list.");
    }

    /**
     * Sets the distance value at the specified index.
     * If the index is greater than the current size of the list, it expands the list
     * and fills uninitialized positions with null.
     *
     * @param index The index at which the distance value will be set.
     * @param value The distance value to set.
     */
    public void set(int index, Double value) {
        while (list.size() <= index) {
            list.add(null); // Expand the list and fill with null
            logger.debug("Expanded list size to {} and filled with null.", list.size());
        }
        list.set(index, value);
        logger.debug("Set distance at index {} to {}", index, value);
    }

    /**
     * Gets the distance value at the specified index.
     * If the index is out of bounds or the value at the index is null, it returns Double.POSITIVE_INFINITY.
     *
     * @param index The index from which to get the distance value.
     * @return The distance value at the index, or Double.POSITIVE_INFINITY if uninitialized.
     */
    public Double get(int index) {
        if (index >= list.size() || list.get(index) == null) {
            logger.debug("Returning POSITIVE_INFINITY for uninitialized index: {}", index);
            return Double.POSITIVE_INFINITY;
        }
        logger.debug("Returning distance at index {}: {}", index, list.get(index));
        return list.get(index);
    }

    /**
     * Gets the current size of the distance list.
     *
     * @return The size of the list.
     */
    public int size() {
        return list.size();
    }

    /**
     * Returns a string representation of the distance list.
     *
     * @return A string representing the distance list values.
     */
    @Override
    public String toString() {
        return list.toString();
    }
}
