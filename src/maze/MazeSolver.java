package maze;

/** BFS uses a queue to explore cells in order of distance from the start. */
public class MazeSolver {
    private Maze maze;
    private CellQueue queue;
    private Cell[][] parent;
    private Cell current;
    private boolean finished = false;
    private boolean foundPath = false;
    private int pathLength = 0;

    public MazeSolver(Maze maze) {
        this.maze = maze;
        queue = new CellQueue(maze.getRows() * maze.getColumns());
        parent = new Cell[maze.getRows()][maze.getColumns()];
        maze.resetSearch();
        maze.getStart().setVisited(true);
        queue.enqueue(maze.getStart());
    }

    public void step() {
        if (finished) return;
        if (queue.isEmpty()) {
            finished = true;
            current = null;
            return;
        }

        current = queue.dequeue();
        if (current == maze.getExit()) {
            markPath();
            foundPath = true;
            finished = true;
            return;
        }

        Cell[] neighbors = maze.getNeighbors(current);
        for (int i = 0; i < neighbors.length; i++) {
            Cell next = neighbors[i];
            if (next != null && !next.isVisited() && maze.canMove(current, next)) {
                // Mark on enqueue so that a cell cannot be queued twice.
                next.setVisited(true);
                parent[next.getRow()][next.getColumn()] = current;
                queue.enqueue(next);
            }
        }
    }

    private void markPath() {
        Cell cell = maze.getExit();
        // Follow parent links backwards from the exit to the start.
        while (cell != null) {
            cell.setOnPath(true);
            Cell previous = parent[cell.getRow()][cell.getColumn()];
            if (previous != null) pathLength++;
            cell = previous;
        }
    }

    public boolean isFinished() { return finished; }
    public boolean hasFoundPath() { return foundPath; }
    public int getPathLength() { return pathLength; }
    public Cell getCurrent() { return current; }
}
