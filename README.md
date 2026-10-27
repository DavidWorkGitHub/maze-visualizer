# Maze Visualizer

A Java Swing desktop application that animates randomized depth-first maze generation and breadth-first shortest-path search. No third-party Java dependencies.

## Build and run

Use a **JDK 17 or newer** to build (tested locally with JDK 23). The compiled application targets Java 8 or newer. A graphical desktop is required to launch the app; tests also work headlessly.

From PowerShell in the project directory:

```powershell
./build.ps1 -Test -Run
```

The script looks for a compiler in `JAVA_HOME`, then `PATH`, then your user `.jdks` folder. `-Test` and `-Run` are optional. It produces `out/maze-visualizer.jar`.

Alternatively, from a terminal with the JDK on PATH:

```sh
javac --release 8 -d out src/maze/*.java
java -cp out maze.MazeApp
```

## Using the application

- Set rows and columns from 1 to 100. Try 10 x 10 and 30 x 30.
- **Generate** starts a new maze with the entered seed. Identical dimensions and seed reproduce the maze. **Random maze** chooses a new seed.
- **Pause / Resume** controls animation. **Step** advances one operation while paused.
- The speed slider ranges from approximately 1 to 1000 operations per second. Raise it for large grids.
- **Solve after generation** automatically starts BFS. Uncheck it to inspect the completed maze, then use **Solve again** to start the search manually.
- **Solve again** also reruns BFS on the same completed maze.
- S is the top-left start, E the bottom-right exit. At 1 x 1 they coincide and the solution has zero moves. On very dense grids letters are omitted for readability.
- Gray shows carved cells, orange the active cell, blue explored cells, purple queued cells, and green the final shortest path. The footer reports progress, dimensions, seed, and solution length in moves.
- Changing dimension or seed fields takes effect on the next generation. Generating during an animation replaces the previous maze safely.

## Design

`Cell` encapsulates four boolean wall states and a visited flag. `Maze` stores a `Cell[rows][columns]` grid: row increases downward and column increases rightward. Boundary neighbors return null. Carving removes the matching wall on both cells and preserves the outside border.

`MazeGenerator` performs iterative randomized DFS using `LinkedStack<Cell>`. It starts at the top-left cell, randomly selects an unvisited neighbor, carves the connecting wall, and pushes that neighbor. When no unvisited neighbor remains, it pops to backtrack. A fresh maze is required for each generator. Every added edge joins a previously unvisited cell, so the completed connected maze has exactly V-1 edges and no loops: a perfect maze.

`MazeSolver` uses `LinkedQueue<Cell>` for BFS. It resets visited flags without changing walls, marks cells on enqueue to prevent duplicates, and records predecessor cells. When the exit is dequeued, it follows predecessors and reverses the result. Each passage has equal cost, so BFS returns a shortest path. An empty result indicates an unreachable exit.

The linked stack and queue are provided because the course's lab implementations were not supplied. **If your assignment requires those exact classes, replace these implementations or adapt their method calls before submission.** Stack uses push/pop/peek; queue uses enqueue/dequeue. Both have constant-time insertion and removal and throw `NoSuchElementException` on invalid removal.

`MazePanel` extends JPanel and paints square cells, walls, state colors, and endpoint labels. `MazeApp` owns the controls and Swing timer. Each timer event performs a bounded batch of small algorithm steps, then repaints. All model updates and rendering occur on Swing's event dispatch thread; no blocking sleeps or recursive calls are used. Resizing fits the maze to the available canvas.

Both algorithms take O(rows * columns) time and space on this grid. Painting takes O(rows * columns) per frame. The UI limit of 100 per dimension bounds rendering and memory use.

## Testing and demonstration

```powershell
./build.ps1 -Test
```

Or with a JDK on PATH:

```sh
javac --release 8 -d out src/maze/*.java test/maze/*.java
java "-Djava.awt.headless=true" -cp out maze.MazeTests
```

Tests cover stack/queue ordering, empty operations, queue reuse, invalid dimensions, deterministic seeds, reciprocal walls, intact boundaries, full connectivity, exactly V-1 passages, path adjacency, shortest distance against an independent array-based traversal, repeated solving, unreachable exits, cycles, and offscreen Swing painting. Dimensions include 1 x 1, 1 x 30, 30 x 1, 2 x 2, 10 x 10, 30 x 30, 7 x 19, and 100 x 100 across four seeds. A rendered preview is written to `out/maze-preview.png`. GitHub Actions runs the tests on pushes and pull requests.

For a live demonstration: generate 10 x 10 at a low speed, pause and step through backtracking, then watch BFS and its final path. Repeat at 30 x 30 with a higher speed. Finally demonstrate 1 x 1 and a single row. Confirm the controls stay responsive while generating, solving, and resizing.

## Development history

This repository records real implementation milestones: grid and containers, DFS, BFS, Swing UI, then tests and documentation. Inspect them with `git log --oneline`. Keep the Git repository (or push it to GitHub) to preserve this history; a source ZIP alone does not include `.git` history.
