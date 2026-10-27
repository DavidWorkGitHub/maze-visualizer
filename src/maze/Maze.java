package maze;

/** Stores the grid and handles neighbors and matching walls. */
public class Maze {
    private int rows;
    private int columns;
    private Cell[][] cells;

    public Maze(int rows, int columns) {
        if (rows < 1 || rows > 50 || columns < 1 || columns > 50) {
            throw new IllegalArgumentException("Rows and columns must be between 1 and 50.");
        }
        this.rows = rows;
        this.columns = columns;
        cells = new Cell[rows][columns];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                cells[r][c] = new Cell(r, c);
            }
        }
    }

    public int getRows() { return rows; }
    public int getColumns() { return columns; }
    public Cell getCell(int row, int column) { return cells[row][column]; }
    public Cell getStart() { return cells[0][0]; }
    public Cell getExit() { return cells[rows - 1][columns - 1]; }

    // Array positions: 0 = above, 1 = right, 2 = below, 3 = left.
    // A null entry means that direction goes outside the grid.
    public Cell[] getNeighbors(Cell cell) {
        Cell[] neighbors = new Cell[4];
        int r = cell.getRow();
        int c = cell.getColumn();

        if (r > 0) neighbors[0] = cells[r - 1][c];
        if (c < columns - 1) neighbors[1] = cells[r][c + 1];
        if (r < rows - 1) neighbors[2] = cells[r + 1][c];
        if (c > 0) neighbors[3] = cells[r][c - 1];
        return neighbors;
    }

    public void removeWall(Cell from, Cell to) {
        int rowChange = to.getRow() - from.getRow();
        int columnChange = to.getColumn() - from.getColumn();

        if (rowChange == -1 && columnChange == 0) {
            from.removeTopWall();
            to.removeBottomWall();
        } else if (rowChange == 1 && columnChange == 0) {
            from.removeBottomWall();
            to.removeTopWall();
        } else if (columnChange == -1 && rowChange == 0) {
            from.removeLeftWall();
            to.removeRightWall();
        } else if (columnChange == 1 && rowChange == 0) {
            from.removeRightWall();
            to.removeLeftWall();
        } else {
            throw new IllegalArgumentException("Cells must be next to each other.");
        }
    }

    public boolean canMove(Cell from, Cell to) {
        if (to == null) return false;
        int rowChange = to.getRow() - from.getRow();
        int columnChange = to.getColumn() - from.getColumn();

        if (rowChange == -1 && columnChange == 0) return !from.hasTopWall();
        if (rowChange == 1 && columnChange == 0) return !from.hasBottomWall();
        if (columnChange == -1 && rowChange == 0) return !from.hasLeftWall();
        if (columnChange == 1 && rowChange == 0) return !from.hasRightWall();
        return false;
    }

    // BFS needs a fresh visited state, but the generated walls must stay.
    public void resetSearch() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                cells[r][c].setVisited(false);
                cells[r][c].setOnPath(false);
            }
        }
    }
}
