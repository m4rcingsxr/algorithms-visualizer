package com.marcinseweryn.visualizer.model;

import java.util.ArrayList;

public class DistanceList {
        private final ArrayList<Double> list;

        public DistanceList() {
            list = new ArrayList<>();
        }

        public void set(int index, Double value) {
            while (list.size() <= index) {
                list.add(null);
            }
            list.set(index, value);
        }

        public Double get(int index) {
            if (index >= list.size() || list.get(index) == null) {
                return Double.POSITIVE_INFINITY;
            }

            return list.get(index);
        }

        public int size() {
            return list.size();
        }

        @Override
        public String toString() {
            return list.toString();
        }

    }