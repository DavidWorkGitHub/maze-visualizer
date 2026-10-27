package maze;

import java.util.Random;

/** Randomized DFS, with an explicit stack for backtracking. */
public class MazeGenerator {
    private Maze maze;
    private CellStack stack;
    private Random random = new Random();
    private Cell current;

    // Always give the generator a NEW maze with all walls intact.
    public MazeGenerator(Maze maze) {
        this.maze = maze;
        stack = new CellStack(maze.getRows() * maze.getColumns());
        current = maze.getStart();
        current.setVisited(true);
        stack.push(current);
    }

    // One call does one small step, so the GUI can animate the algorithm.
    public void step() {
        if (isFinished()) return;

        current = stack.peek();
        Cell[] neighbors = maze.getNeighbors(current);
        Cell[] choices = new Cell[4];
        int count = 0;

        // Collect only neighbors that have not been visited.
        for (int i = 0; i < neighbors.length; i++) {
            if (neighbors[i] != null && !neighbors[i].isVisited()) {
                choices[count] = neighbors[i];
                count++;
            }
        }

        if (count > 0) {
            Cell next = choices[random.nextInt(count)];
            maze.removeWall(current, next);
            next.setVisited(true);
            stack.push(next);
            current = next;
        } else {
            // Dead end: return to the previous cell on the stack.
            stack.pop();
            if (stack.isEmpty()) current = null;
            else current = stack.peek();
        }
    }

    public boolean isFinished() {
        return stack.isEmpty();
    }

    public Cell getCurrent() {
        return current;
    }
}
