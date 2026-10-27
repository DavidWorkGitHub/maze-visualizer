package maze;

/** One square of the maze. Every new cell starts with four walls. */
public class Cell {
    private int row;
    private int column;
    private boolean topWall = true;
    private boolean bottomWall = true;
    private boolean leftWall = true;
    private boolean rightWall = true;
    private boolean visited = false;
    private boolean onPath = false;

    public Cell(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public int getRow() { return row; }
    public int getColumn() { return column; }
    public boolean hasTopWall() { return topWall; }
    public boolean hasBottomWall() { return bottomWall; }
    public boolean hasLeftWall() { return leftWall; }
    public boolean hasRightWall() { return rightWall; }
    public boolean isVisited() { return visited; }
    public boolean isOnPath() { return onPath; }

    public void setVisited(boolean value) { visited = value; }
    public void setOnPath(boolean value) { onPath = value; }

    // Only Maze should remove walls, so both sides are updated together.
    void removeTopWall() { topWall = false; }
    void removeBottomWall() { bottomWall = false; }
    void removeLeftWall() { leftWall = false; }
    void removeRightWall() { rightWall = false; }
}
