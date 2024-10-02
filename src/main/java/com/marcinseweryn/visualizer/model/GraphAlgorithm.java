package com.marcinseweryn.visualizer.model;

import com.marcinseweryn.visualizer.controller.PathFindingController;
import com.marcinseweryn.visualizer.view.GraphNode;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.ListView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public abstract class GraphAlgorithm extends Algorithm {

    public static final Logger logger = LogManager.getLogger(GraphAlgorithm.class);

    protected SimpleObjectProperty<GraphNode> startNode = new SimpleObjectProperty<>();
    protected SimpleObjectProperty<GraphNode> destinationNode = new SimpleObjectProperty<>();

    private List<GraphNode> path = new ArrayList<>();

    protected NodeVisualizer candidateNodes;
    protected NodeVisualizer visitedNodes;

    private GraphNode currentVertex;
    private GraphNode neighbourVertex;

    protected GraphAlgorithm() {
        super();
    }

    protected GraphAlgorithm(
            ListView<SimpleStringProperty> candidateNodes,
            ListView<SimpleStringProperty> visitedNodes,
            SimpleObjectProperty<GraphNode> startNode,
            SimpleObjectProperty<GraphNode> destinationNode) {
        this.candidateNodes = new Queue(ListType.CANDIDATE_NODES, candidateNodes, visitedNodes);
        this.visitedNodes = new Stack(ListType.VISITED, candidateNodes, visitedNodes);

        this.startNode.bind(startNode);
        this.destinationNode.bind(destinationNode);
    }

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

    protected void setCurrent(GraphNode currentVertex) {
        if (this.currentVertex != null) //set previous value to false first
            this.currentVertex.pseudoClassStateChanged(PathFindingController.currentNodeStyle, false);
        if(currentVertex != null) {
            currentVertex.pseudoClassStateChanged(PathFindingController.currentNodeStyle, true);
        }
        this.currentVertex = currentVertex;
    }

    protected GraphNode getCurrent() {
        return currentVertex;
    }

    protected void setNeighbour(GraphNode neighbourVertex) {
        if (this.neighbourVertex != null) //set previous value to false first
            this.neighbourVertex.pseudoClassStateChanged(PathFindingController.neighborNodeStyle, false);
        if(neighbourVertex != null) {
            neighbourVertex.pseudoClassStateChanged(PathFindingController.neighborNodeStyle, true);
        }
        this.neighbourVertex = neighbourVertex;
    }

    protected GraphNode getNeighbour() {
        return neighbourVertex;
    }


}
