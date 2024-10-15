package com.marcinseweryn.visualizer.model.path;

import com.marcinseweryn.visualizer.model.*;
import com.marcinseweryn.visualizer.view.Edge;
import com.marcinseweryn.visualizer.view.GraphNode;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.util.List;

public class BellmanFord extends GraphAlgorithm {

    private GraphNodeVisualizer distanceNodeList;

    public BellmanFord() {
        super();
    }

    public BellmanFord(VBox algorithmTab,
                       ListView<String> pseudocodeList,
                       SimpleObjectProperty<GraphNode> startNode,
                       SimpleObjectProperty<GraphNode> destinationNode,
                       AnchorPane algorithmSpace) {
        super(algorithmTab, pseudocodeList, startNode, destinationNode, algorithmSpace);
        this.distanceNodeList = initializeGraphNodeVisualizer("DISTANCE", DataStructureType.LIST);
    }

    @Override
    public void executeAlgorithm() {
        pauseAtStep(0);
        pauseAtStep(1);

        int start = Integer.parseInt(startNode.get().getId());

        List<GraphNode> graph = getGraph();
        int N = graph.size();

        DistanceList distanceList = new DistanceList();
        distanceList.set(start, 0.0);
        distanceNodeList.addNode(startNode.get());

        pauseAtStep(2);
        for (int i = 0; i < N - 1; i++) {
            pauseAtStep(3);
            for (GraphNode graphNode : graph) {
                int from = Integer.parseInt(graphNode.getId());
                setCurrentNode(graphNode);
                if(!distanceNodeList.containsNode(graphNode)) {
                    getCurrentNode().setDistance(Double.POSITIVE_INFINITY);
                    distanceNodeList.addNode(graphNode);
                }

                pauseAtStep(4);
                for (GraphNode neighbor : graphNode.getNeighbors()) {
                    int to = Integer.parseInt(neighbor.getId());
                    setNeighborNode(neighbor);
                    Edge edge = graphNode.getConnection(neighbor);

                    pauseAtStep(5);
                    if(distanceList.get(from) + edge.getWeight() < distanceList.get(to)) {
                        pauseAtStep(6);
                        distanceList.set(to, distanceList.get(from) + edge.getWeight());
                        getNeighborNode().setDistance(distanceList.get(from) + edge.getWeight());
                    }
                    setNeighborNode(null);
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
                    if(distanceList.get(from) + edge.getWeight() < distanceList.get(to)) {
                        distanceList.set(to, Double.NEGATIVE_INFINITY);
                        neighbor.setDistance(Double.NEGATIVE_INFINITY);
                    }
                }
            }
        }

        System.out.println(distanceList);

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
