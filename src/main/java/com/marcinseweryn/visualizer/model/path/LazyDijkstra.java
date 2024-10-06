package com.marcinseweryn.visualizer.model.path;

import com.marcinseweryn.visualizer.model.DataStructureType;
import com.marcinseweryn.visualizer.model.GraphAlgorithm;
import com.marcinseweryn.visualizer.view.GraphNode;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.ListView;

import java.util.ArrayList;

public class LazyDijkstra extends GraphAlgorithm {

    private final DistanceList distance = new DistanceList();

    public LazyDijkstra() {
        super();
    }

    public LazyDijkstra(ListView<SimpleStringProperty> candidateNodes,
                        ListView<SimpleStringProperty> visitedNodes,
                        ListView<String> pseudocodeList,
                        SimpleObjectProperty<GraphNode> startNode,
                        SimpleObjectProperty<GraphNode> destinationNode) {
        super(candidateNodes, visitedNodes, pseudocodeList, startNode, destinationNode);
    }

    @Override
    public void executeAlgorithm() {
        dijkstra();
    }

    public void dijkstra() {
        initializeCandidateNodesAs(DataStructureType.PRIORITY_QUEUE);
        pauseAtStep(1);
        pauseAtStep(2);
        this.startNode.get().setDistance(0.0F);
        pauseAtStep(3);
        this.candidateNodeList.addNodeAndVisualize(this.startNode.get());
        this.distance.set(0, 0.0);

        pauseAtStep(4);
        while (!this.candidateNodeList.isEmpty()) {
            pauseAtStep(5);
            setCurrentNode(this.candidateNodeList.removeNode());

            pauseAtStep(6);
            this.visitedNodeList.addNodeAndVisualize(getCurrentNode());

            pauseAtStep(7);
            if (this.getCurrentNode().getId().equals(this.destinationNode.get().getId())) {
                pauseAtStep(8);
                shortestPath(this.distance.get(Integer.parseInt(this.getCurrentNode().getId())));
                return;
            }

            pauseAtStep(9);
            if (this.distance.get(Integer.parseInt(getCurrentNode().getId())) < getCurrentNode().getDistance()) {
                continue; // lazy delete the outdated nodes
            }

            pauseAtStep(10);
            for (GraphNode neighbor : getCurrentNode().getNeighbors()) {
                setNeighborNode(neighbor);

                pauseAtStep(11);
                if (visitedNodeList.containsNode(getNeighborNode())) continue;

                pauseAtStep(12);
                double newDistance = getCurrentNode().getDistance() + getCurrentNode().getConnection(
                        getNeighborNode()).getWeight();

                pauseAtStep(13);
                if (newDistance < this.distance.get(Integer.parseInt(getNeighborNode().getId()))) {

                    pauseAtStep(14);
                    pauseAtStep(15);
                    this.distance.set(Integer.parseInt(getNeighborNode().getId()), newDistance);
                    getNeighborNode().setParentNode(getCurrentNode());
                    getNeighborNode().setDistance(newDistance);
                    pauseAtStep(16);
                    this.candidateNodeList.addNodeAndVisualize(getNeighborNode());
                }
                setNeighborNode(null);
            }
        }

        shortestPath(this.destinationNode.get().getDistance());
    }

    private void shortestPath(Double distance) {
        if (Double.isInfinite(distance)) {
            return;
        }

        for (GraphNode node = destinationNode.get(); node != null; node = node.getParentNode()) {
            addToPath(node);
        }
        visualizePath();
    }


    @Override
    public void setPseudocode() {
        this.pseudocode.addAll(
                "Dijkstra(start, end)",
                "   Arrays.fill(distance, Double.POSITIVE_INFINITY)",
                "   distance[start] = 0",
                "   pq.offer(new Node(0, 0))",
                "   while(!pq.isEmpty())",
                "       node = pq.poll()",
                "       visited.add(node.id)",
                "       if(node.id == end)",
                "           reconstructPath(distance[end]) and return",
                "       if(distance[node.id] < node.value) continue",
                "       for (edge : edges(node.id))",
                "           if (edge.to is in visited) continue",
                "           newDistance = this.distance[edge.from] + this.distance[edge.to]",
                "           if (newDistance < distance[edge.to])",
                "               distance[edge.to] = newDistance",
                "               parent[edge.to] = edge.from",
                "               pq.offer(new Node(edge.to, distance[edge.to]))",

                "\nShortestPath(distance)",
                "   if (distance is infinite) return",
                "   for (N = destination; N != null; N = N.parent())",
                "       addToPath(node)",
                "   visualizePath()"
        );
    }

    private static class DistanceList {
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

}

