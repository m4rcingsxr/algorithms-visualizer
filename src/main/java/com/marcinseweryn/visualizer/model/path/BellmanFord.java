package com.marcinseweryn.visualizer.model.path;

import com.marcinseweryn.visualizer.model.DistanceList;
import com.marcinseweryn.visualizer.model.GraphAlgorithm;
import com.marcinseweryn.visualizer.view.Edge;
import com.marcinseweryn.visualizer.view.GraphNode;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;

import java.util.Arrays;
import java.util.List;

public class BellmanFord extends GraphAlgorithm {

    public BellmanFord() {
        super();
    }

    public BellmanFord(ListView<SimpleStringProperty> candidateNoteListView,
                       ListView<SimpleStringProperty> visitedNodeList,
                       ListView<String> pseudocodeList,
                       SimpleObjectProperty<GraphNode> startNode,
                       SimpleObjectProperty<GraphNode> destinationNode,
                       AnchorPane algorithmSpace) {
        super(candidateNoteListView, visitedNodeList, pseudocodeList, startNode, destinationNode, algorithmSpace);
    }

    // todo: fix - wont work for mixed graph ids - replace with distance list
    @Override
    public void executeAlgorithm() {
        int start = Integer.parseInt(startNode.get().getId());

        List<GraphNode> graph = getGraph();
        int N = graph.size();

        double[] distance = new double[N];
        Arrays.fill(distance, Double.POSITIVE_INFINITY);

        distance[start] = 0;

        for (int i = 0; i < N - 1; i++) {
            for (GraphNode graphNode : graph) {
                int from = Integer.parseInt(graphNode.getId());
                for (GraphNode neighbor : graphNode.getNeighbors()) {
                    int to = Integer.parseInt(neighbor.getId());
                    Edge edge = graphNode.getConnection(neighbor);
                    if(distance[from] + edge.getWeight() < distance[to]) {
                        distance[to] = distance[from] + edge.getWeight();
                    }
                }
            }
        }

        // find negative cycles
        for (int i = 0; i < N - 1; i++) {
            for (GraphNode graphNode : graph) {
                int from = Integer.parseInt(graphNode.getId());
                for (GraphNode neighbor : graphNode.getNeighbors()) {
                    int to = Integer.parseInt(neighbor.getId());
                    Edge edge = graphNode.getConnection(neighbor);
                    if(distance[from] + edge.getWeight() < distance[to]) {
                        distance[to] = Double.NEGATIVE_INFINITY;
                    }
                }
            }
        }
    }

    @Override
    public void setPseudocode() {
        this.pseudocode.addAll(
                "Arrays.fill(distance, Double.POSITIVE_INFINITY)",
                "distance[start] = 0",
                "for i = 0; i < N - 1; i++", // N - num of vertices
                "    for node : graph",
                "        for edge : node.edges",
                "            if distance[edge.from] + edge.weight < distance[edge.to]",
                "                 distance[edge.to] = distance[edge.from] + edge.weight",
                "\nfor i = 0; i < N - 1; i++", // check for negative cycles
                "     for node : graph",
                "         for edge : node.edges",
                "             if distance[edge.from] + edge.weight < distance[edge.to]",
                "                 distance[edge.to] = Double.NEGATIVE_INFINITY"
        );
    }
}
