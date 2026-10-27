package maze;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JPanel;
import maze.Cell.Direction;

/** Scales the maze to the available space while preserving square cells. */
public final class MazePanel extends JPanel {
    static final Color BACKGROUND = new Color(18, 24, 38);
    static final Color STANDARD = new Color(32, 42, 59);
    static final Color CARVED = new Color(57, 77, 98);
    static final Color EXPLORED = new Color(30, 113, 146);
    static final Color FRONTIER = new Color(101, 81, 176);
    static final Color ACTIVE = new Color(255, 170, 67);
    static final Color PATH = new Color(69, 211, 162);
    private Maze maze;
    private MazeGenerator generator;
    private MazeSolver solver;
    private Set<Cell> path = new HashSet<>();
    public MazePanel() { setBackground(BACKGROUND); setPreferredSize(new Dimension(820, 620)); }
    public void showState(Maze maze, MazeGenerator generator, MazeSolver solver) {
        this.maze = maze;
        this.generator = generator;
        this.solver = solver;
        path = solver == null ? new HashSet<Cell>() : new HashSet<>(solver.path());
        repaint();
    }
    @Override protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        if (maze == null) return;
        Graphics2D g = (Graphics2D) graphics.create();
        try {
            double size = Math.min((getWidth() - 40.0) / maze.columns(),
                                   (getHeight() - 40.0) / maze.rows());
            if (size <= 0) return;
            double left = (getWidth() - size * maze.columns()) / 2;
            double top = (getHeight() - size * maze.rows()) / 2;
            Cell current = solver != null ? solver.current() : generator == null ? null : generator.current();
            for (int r = 0; r < maze.rows(); r++) for (int c = 0; c < maze.columns(); c++) {
                Cell cell = maze.cell(r, c);
                Color fill = cell.visited() ? CARVED : STANDARD;
                if (solver != null) fill = solver.explored(cell) ? EXPLORED : cell.visited() ? FRONTIER : CARVED;
                if (cell == current) fill = ACTIVE;
                if (path.contains(cell)) fill = PATH;
                int x = (int) Math.round(left + c * size), y = (int) Math.round(top + r * size);
                int x2 = (int) Math.round(left + (c + 1) * size), y2 = (int) Math.round(top + (r + 1) * size);
                g.setColor(fill);
                g.fillRect(x, y, x2 - x, y2 - y);
            }
            g.setColor(new Color(194, 209, 225));
            g.setStroke(new BasicStroke(size >= 12 ? 1.5f : 1f));
            for (int r = 0; r < maze.rows(); r++) for (int c = 0; c < maze.columns(); c++) {
                Cell cell = maze.cell(r, c);
                int x = (int) Math.round(left + c * size), y = (int) Math.round(top + r * size);
                int x2 = (int) Math.round(left + (c + 1) * size), y2 = (int) Math.round(top + (r + 1) * size);
                if (cell.hasWall(Direction.TOP)) g.drawLine(x, y, x2, y);
                if (cell.hasWall(Direction.LEFT)) g.drawLine(x, y, x, y2);
                if (r == maze.rows() - 1 && cell.hasWall(Direction.BOTTOM)) g.drawLine(x, y2, x2, y2);
                if (c == maze.columns() - 1 && cell.hasWall(Direction.RIGHT)) g.drawLine(x2, y, x2, y2);
            }
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (size >= 14) {
                g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, (int) Math.min(16, size * .48)));
                drawMarker(g, maze.start(), maze.size() == 1 ? "S/E" : "S", left, top, size);
                if (maze.size() != 1) drawMarker(g, maze.exit(), "E", left, top, size);
            }
        } finally { g.dispose(); }
    }
    private void drawMarker(Graphics2D g, Cell cell, String text, double left, double top, double size) {
        FontMetrics metrics = g.getFontMetrics();
        g.setColor(Color.WHITE);
        float x = (float) (left + (cell.column() + .5) * size - metrics.stringWidth(text) / 2.0);
        float y = (float) (top + (cell.row() + .5) * size + (metrics.getAscent() - metrics.getDescent()) / 2.0);
        g.drawString(text, x, y);
    }
}
