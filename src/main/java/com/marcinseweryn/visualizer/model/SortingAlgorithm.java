package com.marcinseweryn.visualizer.model;

import com.marcinseweryn.visualizer.view.SortingRectangles;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

import java.util.List;

public abstract class SortingAlgorithm extends Algorithm {

    protected final SortingRectangles sortingRectangles;

    protected SortingAlgorithm(ListView<String> pseudocodeList, SortingRectangles sortingRectangles) {
        super(pseudocodeList);
        this.sortingRectangles = sortingRectangles;
    }

    protected void swapAnimation(int i, int j) {
        Platform.runLater(() -> swap(sortingRectangles.getChildren(), i, j));
    }

    private void swap(List<Node> sortedList, int i, int j) {
        Node tempI = sortedList.get(i);
        Node tempJ = sortedList.get(j);
        sortedList.set(i, new VBox());
        sortedList.set(j, new VBox());
        sortedList.set(i, tempJ);
        sortedList.set(j, tempI);

    }

    protected void setComparison(int index) {
        Platform.runLater(() -> {
            this.sortingRectangles.getChildren().get(index).getStyleClass().remove("sorting-rectangle");
            this.sortingRectangles.getChildren().get(index).getStyleClass().add("comparing-rectangle");
        });
    }

    protected void removeComparison(int index) {
        Platform.runLater(() -> {
            this.sortingRectangles.getChildren().get(index).getStyleClass().remove("comparing-rectangle");
            this.sortingRectangles.getChildren().get(index).getStyleClass().add("sorting-rectangle");
        });
    }

    protected void setSorted(int index) {
        Platform.runLater(() -> {
            this.sortingRectangles.getChildren().get(index).getStyleClass().remove("sorting-rectangle");
            this.sortingRectangles.getChildren().get(index).getStyleClass().add("sorted-rectangle");
        });
    }

}
