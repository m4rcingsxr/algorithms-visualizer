# Algorithm Visualizer
![output.gif](output.gif)
![output2.gif](output2.gif)

## Overview

Algorithm Visualizer is a JavaFX-based application that visually demonstrates how different algorithms (sorting and
graph traversal) work. The application supports both graph algorithms (like DFS, BFS, Bellman-Ford) and sorting
algorithms (like QuickSort, MergeSort, BubbleSort). Each algorithm's progress can be visualized step-by-step, allowing
users to better understand the mechanics behind these algorithms.

### Features:

- **Visual Representation**: Both sorting and graph algorithms are visually rendered, making it easy to follow each step
  of the algorithm.
- **Step-by-Step Execution**: Algorithms can be paused and resumed, giving users control over the speed of execution.
- **Multiple Data Structures**: Support for various data structures like stacks, queues, and priority queues used in
  graph algorithms. These structures are essential for visualizing the algorithm's internal state (e.g., pseudocode
  steps, visited nodes), allowing developers to define and customize their own algorithms while ensuring the correct
  graphical representation.
- **Graph Export/Import**: Ability to save and load graph structures.
- **Easily Extendable for New Algorithms**: The application is designed with an extendable architecture, allowing new
  algorithms to be easily added. The use of abstract classes such as `SortingAlgorithm` and `GraphAlgorithm` enables
  developers to introduce new algorithm logic with minimal structural changes.

---

## How to Use

### Running the Application

1. Clone the repository.
2. Make sure you have JavaFX set up.
3. Run the `VisualizerApp` class to start the application.
4. Use the interface to select either sorting or graph algorithms and watch the step-by-step visualization.

### Available Algorithms

#### Sorting Algorithms:

- QuickSort
- MergeSort
- BubbleSort
- InsertionSort
- SelectionSort

#### Graph Algorithms:

- BFS (Breadth-First Search)
- DFS (Depth-First Search)
- Bellman-Ford
- Dijkstra's Algorithm

---

## Known Issues & Future Improvements

I am aware that there are a few bugs and areas for improvement within the current implementation. While these issues do
not drastically affect the overall functionality of the application, I recognize that some edge cases and optimizations
could benefit from further refinement.

If you're interested in contributing or would like to discuss the project further, feel free to reach out!

## Contributing

If you'd like to contribute, feel free to create a pull request or open an issue to discuss potential improvements!
