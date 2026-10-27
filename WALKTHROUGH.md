# Understanding the maze project

## 1. A cell is just data

Start with `Cell.java`. Each cell knows its row and column, whether each wall exists, whether an algorithm has visited it, and whether it belongs to the final path.

`private` fields keep the data inside the class. Getter methods let other classes read it. A new cell starts with all four walls present.

**Try:** find the value that changes when a cell becomes part of the solution.

## 2. A maze is a two-dimensional array

`Maze.java` creates the grid with two nested loops. The first loop chooses a row and the second creates each column in that row.

`getNeighbors` returns an array with four places: above, right, below, left. Some entries can be null. For example, the top-left cell has no neighbor above or to its left.

`removeWall` updates two cells together. Moving right means removing the current cell's right wall and the next cell's left wall.

**Try:** draw a 2 x 3 grid on paper and label every cell's row and column.

## 3. A stack remembers the route

`CellStack` stores cells in an array. `size` is both the number of stored cells and the next free array position.

- `push`: put a cell at `items[size]`, then increase size.
- `peek`: read `items[size - 1]` without removing it.
- `pop`: decrease size, then remove the cell at that position.

If you push A, B, C, popping returns C, B, A. This is last in, first out.

## 4. DFS generates the maze

Follow `MazeGenerator.step()`:

1. Look at the cell on top of the stack.
2. Collect its unvisited neighbors into the `choices` array.
3. If there are choices, pick one randomly, remove the shared wall, mark it visited, and push it.
4. If there are no choices, pop the stack to backtrack.
5. Finish when the stack is empty.

The generator never opens a passage into an already visited cell. That is why it cannot create a loop. Backtracking allows it to return to places with unexplored neighbors.

**Try:** run a 3 x 3 maze and pause when the orange cell reaches a dead end.

## 5. A queue explores in arrival order

`CellQueue` also uses an array, with `front`, `rear`, and `size`.

- Enqueue adds at the rear.
- Dequeue removes from the front.
- When an index reaches the array's end, it returns to zero to reuse free spaces.

If you enqueue A, B, C, dequeuing returns A, B, C. This is first in, first out.

**Try:** trace a queue of capacity three: add A and B, remove A, add C and D, then remove the remaining cells.

## 6. BFS finds the shortest path

`MazeSolver` clears the visited flags, then queues the start cell. Each step removes one cell and adds its unvisited neighbors, but only when there is an open passage.

The queue ensures that cells one move away are processed before cells two moves away, and so on. Since every move costs the same amount, the first route to the exit is a shortest route.

When a cell is discovered, the solver stores its predecessor in `parent[row][column]`. Once it reaches the exit, `markPath()` follows these links backwards, setting `onPath` to true until it reaches the start.

**Try:** explain why marking visited when adding to the queue prevents duplicate entries.

## 7. Swing displays the result

`MazePanel` extends `JPanel`. Swing calls `paintComponent` when it needs to draw. The method loops over cells, fills each square with the appropriate color, then draws its walls.

`MazeApp` implements `ActionListener`. Its `actionPerformed` method checks which button (or timer) caused the event, then calls the appropriate method.

The timer calls one algorithm step, then requests a repaint. This separates the algorithm from the drawing and lets the window stay responsive. The short `Runnable` in `main` simply asks Swing to create the window on its own event thread.

**Try:** change the visited-cell color in `MazePanel`, run the program, and explain why the algorithms still work unchanged.

## Before your demonstration

Be ready to explain the difference between a stack and queue, why DFS needs backtracking, why BFS finds a shortest path, why walls must be changed in pairs, and how parent links reconstruct the solution. Match the Stack and Queue implementations to the ones required by your course.
