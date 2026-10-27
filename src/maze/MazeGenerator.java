package maze;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Iterative randomized DFS. Each step carves one edge or backtracks once. */
public final class MazeGenerator {
    private final Maze maze;
    private final Random random;
    private final LinkedStack<Cell> stack = new LinkedStack<>();
    private Cell current;
    private int visitedCount = 1;

    /** Requires a fresh maze with all walls intact. Seed makes runs reproducible. */
    public MazeGenerator(Maze maze, long seed) {
        this.maze = maze;
        random = new Random(seed);
        maze.resetVisited();
        current = maze.start();
        current.setVisited(true);
        stack.push(current);
    }
    public void step() {
        if (isDone()) return;
        current = stack.peek();
        List<Cell> candidates = new ArrayList<>();
        for (Cell next : maze.neighbors(current, false))
            if (!next.visited()) candidates.add(next);
        if (candidates.isEmpty()) {
            stack.pop();
            current = stack.isEmpty() ? null : stack.peek();
        } else {
            Cell next = candidates.get(random.nextInt(candidates.size()));
            maze.carve(current, next);
            next.setVisited(true);
            visitedCount++;
            stack.push(next);
            current = next;
        }
    }
    public boolean isDone() { return stack.isEmpty(); }
    public Cell current() { return current; }
    public int visitedCount() { return visitedCount; }
}
