package maze;

import java.util.ArrayList;
import java.util.List;
import maze.Cell.Direction;

/** Rectangular grid indexed as [row][column], with row zero at the top. */
public final class Maze {
    public static final int MAX_DIMENSION = 100;
    private final Cell[][] cells;
    public Maze(int rows, int columns) {
        if (rows < 1 || columns < 1 || rows > MAX_DIMENSION || columns > MAX_DIMENSION)
            throw new IllegalArgumentException("Dimensions must be between 1 and 100");
        cells = new Cell[rows][columns];
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < columns; c++) cells[r][c] = new Cell(r, c);
    }
    public int rows() { return cells.length; }
    public int columns() { return cells[0].length; }
    public int size() { return rows() * columns(); }
    public Cell cell(int row, int column) { return cells[row][column]; }
    public Cell start() { return cell(0, 0); }
    public Cell exit() { return cell(rows() - 1, columns() - 1); }
    public Cell neighbor(Cell cell, Direction direction) {
        int r = cell.row() + direction.dr, c = cell.column() + direction.dc;
        return r < 0 || c < 0 || r >= rows() || c >= columns() ? null : cells[r][c];
    }
    public List<Cell> neighbors(Cell cell, boolean passagesOnly) {
        List<Cell> result = new ArrayList<>(4);
        for (Direction direction : Direction.values()) {
            Cell next = neighbor(cell, direction);
            if (next != null && (!passagesOnly || !cell.hasWall(direction))) result.add(next);
        }
        return result;
    }
    /** Remove both sides of an internal wall, never an outer boundary. */
    void carve(Cell from, Cell to) {
        for (Direction direction : Direction.values()) {
            if (neighbor(from, direction) == to && to != null) {
                from.removeWall(direction);
                to.removeWall(direction.opposite());
                return;
            }
        }
        throw new IllegalArgumentException("Cells must be adjacent in this maze");
    }
    public void resetVisited() {
        for (Cell[] row : cells) for (Cell cell : row) cell.setVisited(false);
    }
}
