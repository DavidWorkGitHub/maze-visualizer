package maze;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import javax.swing.JPanel;

/** The drawing canvas: paint cell colors first, then draw their walls. */
public class MazePanel extends JPanel {
    private Maze maze;
    private Cell current;

    public MazePanel() {
        setBackground(new Color(238, 242, 247));
        setPreferredSize(new Dimension(720, 600));
    }

    public void setMaze(Maze maze) {
        this.maze = maze;
        current = null;
        repaint();
    }

    public void setCurrent(Cell cell) {
        current = cell;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (maze == null) return;

        // Use the smaller size so square cells fit in both directions.
        int cellSize = Math.min((getWidth() - 40) / maze.getColumns(),
                               (getHeight() - 40) / maze.getRows());
        if (cellSize < 1) return;
        int left = (getWidth() - cellSize * maze.getColumns()) / 2;
        int top = (getHeight() - cellSize * maze.getRows()) / 2;

        for (int r = 0; r < maze.getRows(); r++) {
            for (int c = 0; c < maze.getColumns(); c++) {
                Cell cell = maze.getCell(r, c);
                int x = left + c * cellSize;
                int y = top + r * cellSize;

                g.setColor(Color.WHITE);
                if (cell.isVisited()) g.setColor(new Color(185, 218, 245));
                if (cell == current) g.setColor(new Color(255, 190, 90));
                if (cell.isOnPath()) g.setColor(new Color(115, 220, 155));
                g.fillRect(x, y, cellSize, cellSize);

                g.setColor(new Color(43, 57, 75));
                if (cell.hasTopWall()) g.drawLine(x, y, x + cellSize, y);
                if (cell.hasBottomWall()) g.drawLine(x, y + cellSize, x + cellSize, y + cellSize);
                if (cell.hasLeftWall()) g.drawLine(x, y, x, y + cellSize);
                if (cell.hasRightWall()) g.drawLine(x + cellSize, y, x + cellSize, y + cellSize);
            }
        }

        // Draw endpoint labels when cells are large enough to read them.
        if (cellSize >= 18) {
            g.setColor(new Color(28, 44, 65));
            g.setFont(new Font("SansSerif", Font.BOLD, 12));
            if (maze.getRows() == 1 && maze.getColumns() == 1) {
                g.drawString("S/E", left + 3, top + 14);
            } else {
                g.drawString("S", left + 3, top + 14);
                int exitX = left + (maze.getColumns() - 1) * cellSize;
                int exitY = top + (maze.getRows() - 1) * cellSize;
                g.drawString("E", exitX + 3, exitY + 14);
            }
        }
    }
}
