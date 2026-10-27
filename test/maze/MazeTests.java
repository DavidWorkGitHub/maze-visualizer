package maze;

import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.io.File;
import java.util.Arrays;
import java.util.NoSuchElementException;
import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;
import maze.Cell.Direction;

/** Dependency-free regression tests; throws AssertionError on any failure. */
public final class MazeTests {
    private static int assertions;
    private static void check(boolean condition, String message) {
        assertions++;
        if (!condition) throw new AssertionError(message);
    }
    private static void rejects(Runnable operation, Class<? extends RuntimeException> type) {
        try { operation.run(); } catch (RuntimeException ex) {
            check(type.isInstance(ex), "Unexpected exception: " + ex); return;
        }
        throw new AssertionError("Expected " + type.getSimpleName());
    }
    private static Maze generated(int rows, int columns, long seed) {
        Maze maze = new Maze(rows, columns);
        MazeGenerator generator = new MazeGenerator(maze, seed);
        int steps = 0;
        while (!generator.isDone()) { generator.step(); check(++steps <= 2 * maze.size(), "DFS must terminate"); }
        check(generator.visitedCount() == maze.size(), "DFS visits every cell");
        generator.step();
        return maze;
    }
    /** Independent array-based distance oracle, separate from the linked-queue solver. */
    private static int[] distances(Maze maze) {
        int[] distance = new int[maze.size()], queue = new int[maze.size()];
        Arrays.fill(distance, -1);
        distance[0] = 0;
        int head = 0, tail = 1;
        while (head < tail) {
            int id = queue[head++];
            Cell cell = maze.cell(id / maze.columns(), id % maze.columns());
            for (Direction d : Direction.values()) {
                Cell next = maze.neighbor(cell, d);
                if (next == null || cell.hasWall(d)) continue;
                int nextId = next.row() * maze.columns() + next.column();
                if (distance[nextId] == -1) {
                    distance[nextId] = distance[id] + 1;
                    queue[tail++] = nextId;
                }
            }
        }
        return distance;
    }
    private static MazeSolver solve(Maze maze) {
        MazeSolver solver = new MazeSolver(maze);
        int steps = 0;
        while (!solver.isDone()) { solver.step(); check(++steps <= maze.size() + 1, "BFS must terminate"); }
        solver.step();
        return solver;
    }
    private static void validate(int rows, int columns, long seed) {
        Maze maze = generated(rows, columns, seed);
        Maze duplicate = generated(rows, columns, seed);
        int edges = 0;
        for (int r = 0; r < rows; r++) for (int c = 0; c < columns; c++) {
            Cell cell = maze.cell(r, c);
            for (Direction d : Direction.values()) {
                Cell next = maze.neighbor(cell, d);
                check(cell.hasWall(d) == duplicate.cell(r, c).hasWall(d), "Seed reproducibility");
                if (next == null) check(cell.hasWall(d), "Outer boundary intact");
                else {
                    check(cell.hasWall(d) == next.hasWall(d.opposite()), "Reciprocal walls");
                    if (!cell.hasWall(d)) edges++;
                }
            }
        }
        int[] distance = distances(maze);
        for (int value : distance) check(value >= 0, "Every cell reachable");
        check(edges / 2 == maze.size() - 1, "Connected graph with V-1 edges is a tree");
        MazeSolver solver = solve(maze);
        check(solver.foundPath(), "Exit reachable");
        check(solver.path().get(0) == maze.start(), "Path starts at entrance");
        check(solver.path().get(solver.path().size() - 1) == maze.exit(), "Path ends at exit");
        check(solver.path().size() - 1 == distance[maze.size() - 1], "Shortest path length");
        for (int i = 1; i < solver.path().size(); i++)
            check(maze.neighbors(solver.path().get(i - 1), true).contains(solver.path().get(i)), "Path respects walls");
        check(solve(maze).path().size() == solver.path().size(), "Solver can run again");
    }
    public static void main(String[] args) throws Exception {
        LinkedStack<Integer> stack = new LinkedStack<>();
        LinkedQueue<Integer> queue = new LinkedQueue<>();
        rejects(stack::pop, NoSuchElementException.class);
        rejects(stack::peek, NoSuchElementException.class);
        rejects(queue::dequeue, NoSuchElementException.class);
        for (int i = 0; i < 100; i++) { stack.push(i); queue.enqueue(i); }
        check(stack.size() == 100 && queue.size() == 100, "Container sizes");
        for (int i = 0; i < 100; i++) {
            check(stack.peek() == 99 - i && stack.pop() == 99 - i, "LIFO order");
            check(queue.dequeue() == i, "FIFO order");
        }
        check(stack.isEmpty() && queue.isEmpty(), "Empty after removal");
        queue.enqueue(7); check(queue.dequeue() == 7, "Queue reuse after drain");
        rejects(() -> new Maze(0, 10), IllegalArgumentException.class);
        rejects(() -> new Maze(10, -1), IllegalArgumentException.class);
        rejects(() -> new Maze(101, 1), IllegalArgumentException.class);
        int[][] dimensions = {{1,1}, {1,30}, {30,1}, {2,2}, {10,10}, {30,30}, {7,19}, {100,100}};
        for (int[] size : dimensions) for (long seed : new long[] {0, 42, -17, Long.MAX_VALUE})
            validate(size[0], size[1], seed);
        check(!solve(new Maze(2, 2)).foundPath(), "Disconnected maze reports no path");
        Maze cycle = new Maze(2, 2);
        cycle.carve(cycle.cell(0,0), cycle.cell(0,1));
        cycle.carve(cycle.cell(0,1), cycle.cell(1,1));
        cycle.carve(cycle.cell(0,0), cycle.cell(1,0));
        cycle.carve(cycle.cell(1,0), cycle.cell(1,1));
        check(solve(cycle).path().size() == 3, "BFS handles cycles and equal shortest routes");
        SwingUtilities.invokeAndWait(() -> {
            try {
                Maze maze = generated(20, 30, 42);
                MazeSolver solver = solve(maze);
                MazePanel panel = new MazePanel();
                panel.setSize(1000, 720); panel.showState(maze, null, solver);
                BufferedImage image = new BufferedImage(1000, 720, BufferedImage.TYPE_INT_RGB);
                Graphics2D graphics = image.createGraphics();
                panel.paint(graphics); graphics.dispose();
                ImageIO.write(image, "png", new File("out/maze-preview.png"));
                check(image.getRGB(500,360) != MazePanel.BACKGROUND.getRGB(), "Canvas renders maze");
            } catch (Exception ex) { throw new RuntimeException(ex); }
        });
        System.out.println("PASS: " + assertions + " assertions, 32 seeded mazes, containers, edge cases and canvas rendering.");
    }
}
