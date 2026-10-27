package maze;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** BFS marks cells on enqueue, so each cell enters the queue at most once. */
public final class MazeSolver {
    private final Maze maze;
    private final LinkedQueue<Cell> queue = new LinkedQueue<>();
    private final Cell[][] parents;
    private final boolean[][] explored;
    private List<Cell> path = Collections.emptyList();
    private Cell current;
    private boolean done;
    private int exploredCount;
    public MazeSolver(Maze maze) {
        this.maze = maze;
        parents = new Cell[maze.rows()][maze.columns()];
        explored = new boolean[maze.rows()][maze.columns()];
        maze.resetVisited();
        maze.start().setVisited(true);
        queue.enqueue(maze.start());
    }
    public void step() {
        if (done) return;
        if (queue.isEmpty()) { done = true; current = null; return; }
        current = queue.dequeue();
        explored[current.row()][current.column()] = true;
        exploredCount++;
        if (current == maze.exit()) {
            List<Cell> result = new ArrayList<>();
            for (Cell cell = current; cell != null; cell = parents[cell.row()][cell.column()])
                result.add(cell);
            Collections.reverse(result);
            path = Collections.unmodifiableList(result);
            done = true;
            return;
        }
        for (Cell next : maze.neighbors(current, true)) {
            if (!next.visited()) {
                next.setVisited(true);
                parents[next.row()][next.column()] = current;
                queue.enqueue(next);
            }
        }
    }
    public boolean isDone() { return done; }
    public boolean foundPath() { return !path.isEmpty(); }
    public List<Cell> path() { return path; }
    public Cell current() { return current; }
    public boolean explored(Cell cell) { return explored[cell.row()][cell.column()]; }
    public int exploredCount() { return exploredCount; }
}
