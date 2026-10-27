package maze;

/** A grid vertex. Walls are changed only through Maze.carve(). */
public final class Cell {
    public enum Direction {
        TOP(-1, 0), RIGHT(0, 1), BOTTOM(1, 0), LEFT(0, -1);
        final int dr, dc;
        Direction(int dr, int dc) { this.dr = dr; this.dc = dc; }
        public Direction opposite() { return values()[(ordinal() + 2) % 4]; }
    }
    private final int row, column;
    private final boolean[] walls = {true, true, true, true};
    private boolean visited;
    Cell(int row, int column) { this.row = row; this.column = column; }
    public int row() { return row; }
    public int column() { return column; }
    public boolean hasWall(Direction direction) { return walls[direction.ordinal()]; }
    void removeWall(Direction direction) { walls[direction.ordinal()] = false; }
    public boolean visited() { return visited; }
    void setVisited(boolean visited) { this.visited = visited; }
}
