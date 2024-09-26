package com.marcinseweryn.visualizer.view;

public class Edge extends Arrow {

    private GraphNode vertex1;
    private GraphNode vertex2;

    public Edge(GraphNode vertex1, GraphNode vertex2) {
        super(vertex1.getLayoutX(), vertex1.getLayoutY(), vertex2.getLayoutX(),
              vertex2.getLayoutY()
        );

        this.vertex1 = vertex1;
        this.vertex2 = vertex2;

        this.x1.bind(vertex1.layoutXProperty());
        this.y1.bind(vertex1.layoutYProperty());
        this.x2.bind(vertex2.layoutXProperty());
        this.y2.bind(vertex2.layoutYProperty());
    }

    public GraphNode getNeighbour(GraphNode vertex) {
        return vertex == vertex1 ? vertex2 : vertex1;
    }
}
