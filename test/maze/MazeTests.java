package maze;

/** Run this class to check the algorithms without opening a window. */
public class MazeTests {
    private static int checks = 0;

    private static void check(boolean condition, String message) {
        checks++;
        if (!condition) throw new AssertionError(message);
    }

    private static void testContainers() {
        Cell a = new Cell(0, 0);
        Cell b = new Cell(0, 1);
        Cell c = new Cell(0, 2);
        CellStack stack = new CellStack(2);
        stack.push(a);
        stack.push(b);
        check(stack.peek() == b, "Peek should return the last added cell.");
        check(stack.pop() == b, "Stack must remove newest first.");
        check(stack.pop() == a, "Stack order is wrong.");
        check(stack.isEmpty(), "Stack should be empty.");
        try {
            stack.pop();
            throw new AssertionError("Empty pop should fail.");
        } catch (IllegalStateException expected) { checks++; }

        CellQueue queue = new CellQueue(2);
        queue.enqueue(a);
        queue.enqueue(b);
        check(queue.dequeue() == a, "Queue must remove oldest first.");
        queue.enqueue(c); // Reuse the space at the beginning of the array.
        check(queue.dequeue() == b, "Queue wraparound changed the order.");
        check(queue.dequeue() == c, "Queue wraparound lost a cell.");
        check(queue.isEmpty(), "Queue should be empty.");
        try {
            queue.dequeue();
            throw new AssertionError("Empty dequeue should fail.");
        } catch (IllegalStateException expected) { checks++; }
    }

    // Independent distance check, using plain arrays instead of CellQueue.
    private static int[][] findDistances(Maze maze) {
        int rows = maze.getRows();
        int columns = maze.getColumns();
        int[][] distance = new int[rows][columns];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) distance[r][c] = -1;
        }
        Cell[] waiting = new Cell[rows * columns];
        waiting[0] = maze.getStart();
        distance[0][0] = 0;
        int front = 0;
        int back = 1;
        while (front < back) {
            Cell cell = waiting[front];
            front++;
            Cell[] neighbors = maze.getNeighbors(cell);
            for (int i = 0; i < neighbors.length; i++) {
                Cell next = neighbors[i];
                if (next != null && maze.canMove(cell, next)
                        && distance[next.getRow()][next.getColumn()] == -1) {
                    distance[next.getRow()][next.getColumn()] = distance[cell.getRow()][cell.getColumn()] + 1;
                    waiting[back] = next;
                    back++;
                }
            }
        }
        return distance;
    }

    private static MazeSolver solve(Maze maze) {
        MazeSolver solver = new MazeSolver(maze);
        int steps = 0;
        while (!solver.isFinished()) {
            solver.step();
            steps++;
            check(steps <= maze.getRows() * maze.getColumns() + 1, "Solver did not finish.");
        }
        return solver;
    }

    private static void testMaze(int rows, int columns) {
        Maze maze = new Maze(rows, columns);
        MazeGenerator generator = new MazeGenerator(maze);
        int steps = 0;
        while (!generator.isFinished()) {
            generator.step();
            steps++;
            check(steps <= 2 * rows * columns, "Generator did not finish.");
        }

        int passages = 0;
        int[][] distance = findDistances(maze);
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                Cell cell = maze.getCell(r, c);
                check(cell.isVisited() && distance[r][c] >= 0, "Every cell must be reachable.");
                if (r == 0) check(cell.hasTopWall(), "Top boundary must remain closed.");
                if (c == 0) check(cell.hasLeftWall(), "Left boundary must remain closed.");
                if (r == rows - 1) check(cell.hasBottomWall(), "Bottom boundary must remain closed.");
                if (c == columns - 1) check(cell.hasRightWall(), "Right boundary must remain closed.");
                if (r < rows - 1) {
                    check(cell.hasBottomWall() == maze.getCell(r + 1, c).hasTopWall(), "Walls must match.");
                    if (!cell.hasBottomWall()) passages++;
                }
                if (c < columns - 1) {
                    check(cell.hasRightWall() == maze.getCell(r, c + 1).hasLeftWall(), "Walls must match.");
                    if (!cell.hasRightWall()) passages++;
                }
            }
        }
        check(passages == rows * columns - 1, "Perfect maze needs exactly one fewer passage than cells.");

        MazeSolver solver = solve(maze);
        check(solver.hasFoundPath(), "Generated maze must have a solution.");
        check(solver.getPathLength() == distance[rows - 1][columns - 1], "Path must be shortest.");
        check(maze.getStart().isOnPath() && maze.getExit().isOnPath(), "Path must include both endpoints.");
        int pathCells = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                Cell cell = maze.getCell(r, c);
                if (!cell.isOnPath()) continue;
                pathCells++;
                int connections = 0;
                Cell[] neighbors = maze.getNeighbors(cell);
                for (int i = 0; i < neighbors.length; i++) {
                    Cell next = neighbors[i];
                    if (next != null && next.isOnPath() && maze.canMove(cell, next)) connections++;
                }
                int expected = 2;
                if (cell == maze.getStart() || cell == maze.getExit()) expected = 1;
                if (rows * columns == 1) expected = 0;
                check(connections == expected, "Path must form a continuous route through open passages.");
            }
        }
        check(pathCells == solver.getPathLength() + 1, "Path length counts moves, not cells.");
        check(solve(maze).getPathLength() == solver.getPathLength(), "Repeated solving must work.");
    }

    public static void main(String[] args) {
        testContainers();
        int[][] sizes = {{1,1}, {1,30}, {30,1}, {2,2}, {10,10}, {30,30}, {7,19}, {50,50}};
        for (int i = 0; i < sizes.length; i++) {
            for (int repeat = 0; repeat < 4; repeat++) testMaze(sizes[i][0], sizes[i][1]);
        }
        check(!solve(new Maze(2, 2)).hasFoundPath(), "Closed maze should have no solution.");
        int[][] invalid = {{0,10}, {10,-1}, {51,1}, {1,51}};
        for (int i = 0; i < invalid.length; i++) {
            try {
                new Maze(invalid[i][0], invalid[i][1]);
                throw new AssertionError("Invalid size should be rejected.");
            } catch (IllegalArgumentException expected) { checks++; }
        }
        Maze cycle = new Maze(2, 2);
        cycle.removeWall(cycle.getCell(0,0), cycle.getCell(0,1));
        cycle.removeWall(cycle.getCell(0,1), cycle.getCell(1,1));
        cycle.removeWall(cycle.getCell(0,0), cycle.getCell(1,0));
        cycle.removeWall(cycle.getCell(1,0), cycle.getCell(1,1));
        check(solve(cycle).getPathLength() == 2, "BFS should handle loops and find a shortest path.");
        System.out.println("PASS: 32 random mazes, stack, queue, boundaries, shortest paths and edge cases.");
        System.out.println("Checks passed: " + checks);
    }
}
