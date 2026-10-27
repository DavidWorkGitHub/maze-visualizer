# Maze Visualizer

A Java Swing desktop project that generates a maze using **depth-first search (DFS)** and solves it using **breadth-first search (BFS)**. Both algorithms are animated.

This version uses ordinary classes, arrays, loops, booleans, and `ActionListener` button handling. It has no external libraries. The earlier version is preserved on the `codex/advanced-reference` branch.

## Run it

Open the project in your Java IDE and run `src/maze/MazeApp.java`. Set `src` as the source folder if your IDE asks. The source works with Java 8 or newer.

Or, with a JDK on your terminal's PATH:

```sh
javac -d out/classes src/maze/*.java
java -cp out/classes maze.MazeApp
```

On this Windows setup, the optional helper also finds your installed JDK:

```powershell
./build.ps1 -Run
```

The helper requires JDK 17 or newer and creates `out/maze-visualizer.jar`. You do not need to understand or use the PowerShell helper to work on the Java project.

## Controls

1. Enter rows and columns between **1 and 50**.
2. Click **Generate** to watch DFS carve the maze.
3. When generation finishes, click **Solve** to watch BFS find the exit.
4. Click **Pause / Resume** to stop or continue either animation.

You can generate a new maze at any time, or solve a completed maze again. Invalid sizes show a helpful message. Blue cells have been visited, orange is the current cell, and green is the final path. The start is the top-left cell and the exit is the bottom-right cell. Letters are hidden when cells are too small to read.

## Classes and design choices

| Class | Responsibility |
| --- | --- |
| `Cell` | Row, column, four wall booleans, visited flag, and final-path flag. |
| `Maze` | A `Cell[][]` grid, boundary checks, and removing matching walls. |
| `CellStack` | Array-based stack for DFS backtracking: last in, first out. |
| `CellQueue` | Array-based queue for BFS: first in, first out. |
| `MazeGenerator` | One DFS step at a time. |
| `MazeSolver` | One BFS step at a time, plus parent links to mark the path. |
| `MazePanel` | A JPanel that draws the cells and their walls. |
| `MazeApp` | Window, buttons, input validation, and animation timer. |

Grid coordinates are `[row][column]`. Row zero is at the top, and column zero is on the left. A neighbor outside the grid is `null`. Opening a passage removes both cells' shared wall. Outer walls remain closed; the start and exit are cells inside the maze.

DFS only carves into unvisited cells. This connects every cell without making loops, giving exactly one route between any two cells. The stack remembers where to return after a dead end.

BFS explores cells in increasing distance from the start. A cell is marked visited when it enters the queue, so it cannot be added twice. The `parent` array remembers the cell that discovered it. Following these links backwards from the exit marks the winning path. The displayed length counts moves, so a 1 x 1 maze has a path length of zero.

The timer performs one algorithm step every 15 milliseconds. This lets Swing redraw the maze and respond to buttons between steps. There are no blocking sleeps or background threads to manage. Each algorithm uses O(rows x columns) time and memory.

**Lab classes:** the brief asks for the Stack and Queue implementations from class, but those files have not been supplied. `CellStack` and `CellQueue` are simple replacements. Substitute the lab versions and adjust the calls in the generator and solver before submission to meet that requirement.

## Test it

The tests are separate from the app; run them when checking changes:

```sh
javac -d out/classes src/maze/*.java test/maze/*.java
java -cp out/classes maze.MazeTests
```

Or use `./build.ps1 -Test` in PowerShell.

Tests cover 32 randomly generated mazes: 1 x 1, 1 x 30, 30 x 1, 2 x 2, 10 x 10, 30 x 30, 7 x 19, and 50 x 50, each repeated four times. They check stack/queue ordering, boundaries, matching walls, connectivity, no loops, valid shortest paths, repeated solving, invalid sizes, an unreachable exit, and a maze containing a cycle.

For a demonstration, generate and solve 10 x 10, then 30 x 30. Pause during each animation. Try 1 x 1, a single row, and an invalid size. At 30 x 30, generation takes approximately 27 seconds, depending on the computer.

## Understand the code

Read [WALKTHROUGH.md](WALKTHROUGH.md) alongside the source. Start with `Cell`, then `Maze`, the stack and queue, the two algorithms, and finally the GUI.

Git history records the original implementation and the later simplification. Use `git log --oneline` to see the actual commits. The history has not been rewritten to pretend the simpler version came first.
