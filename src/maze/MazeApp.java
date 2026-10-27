package maze;

import java.awt.*;
import java.text.ParseException;
import java.util.Random;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/** Swing timer drives small algorithm steps on the event dispatch thread. */
public final class MazeApp {
    private final JFrame frame = new JFrame("Maze Lab | DFS + BFS");
    private final MazePanel canvas = new MazePanel();
    private final JSpinner rows = new JSpinner(new SpinnerNumberModel(20, 1, 100, 1));
    private final JSpinner columns = new JSpinner(new SpinnerNumberModel(30, 1, 100, 1));
    private final JTextField seed = new JTextField("42", 13);
    private final JSlider speed = new JSlider(1, 100, 30);
    private final JButton pause = new JButton("Pause"), step = new JButton("Step"), solve = new JButton("Solve again");
    private final JLabel status = new JLabel(" ");
    private final JCheckBox autoSolve = new JCheckBox("Solve after generation", true);
    private final Timer timer = new Timer(16, event -> tick());
    private Maze maze;
    private MazeGenerator generator;
    private MazeSolver solver;
    private boolean running;
    private long activeSeed;

    public MazeApp() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel root = new JPanel(new BorderLayout(0, 12));
        root.setBorder(new EmptyBorder(18, 20, 16, 20));
        root.setBackground(MazePanel.BACKGROUND);
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("MAZE LAB");
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        JLabel subtitle = new JLabel("Randomized depth-first generation  /  Breadth-first shortest path");
        subtitle.setForeground(new Color(171, 189, 209));
        header.add(title); header.add(Box.createVerticalStrut(5)); header.add(subtitle);
        JPanel settings = new JPanel(new FlowLayout(FlowLayout.LEFT, 9, 8));
        settings.add(new JLabel("Rows")); settings.add(rows);
        settings.add(new JLabel("Columns")); settings.add(columns);
        settings.add(new JLabel("Seed")); settings.add(seed);
        JButton generate = new JButton("Generate");
        JButton random = new JButton("Random maze");
        settings.add(generate); settings.add(random);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 9, 6));
        actions.add(pause); actions.add(step); actions.add(solve); actions.add(autoSolve);
        actions.add(new JLabel("Speed"));
        speed.setPreferredSize(new Dimension(140, 30));
        speed.setToolTipText("Algorithm steps per second: 1 to 1000");
        actions.add(speed);
        header.add(Box.createVerticalStrut(14)); header.add(settings); header.add(actions);
        root.add(header, BorderLayout.NORTH); root.add(canvas, BorderLayout.CENTER);
        JPanel footer = new JPanel(new BorderLayout(0, 8));
        footer.setOpaque(false);
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        legend.setOpaque(false);
        addLegend(legend, "Unvisited", MazePanel.STANDARD);
        addLegend(legend, "Carved", MazePanel.CARVED);
        addLegend(legend, "Explored", MazePanel.EXPLORED);
        addLegend(legend, "Queued", MazePanel.FRONTIER);
        addLegend(legend, "Active", MazePanel.ACTIVE);
        addLegend(legend, "Shortest path", MazePanel.PATH);
        status.setForeground(Color.WHITE);
        footer.add(legend, BorderLayout.NORTH); footer.add(status, BorderLayout.SOUTH);
        root.add(footer, BorderLayout.SOUTH);
        frame.setContentPane(root);
        generate.addActionListener(event -> generate());
        random.addActionListener(event -> { seed.setText(Long.toString(new Random().nextLong())); generate(); });
        pause.addActionListener(event -> { running = !running; update(); });
        step.addActionListener(event -> { advance(); update(); });
        solve.addActionListener(event -> { solver = new MazeSolver(maze); running = true; update(); });
        speed.addChangeListener(event -> adjustSpeed());
        frame.pack(); frame.setMinimumSize(new Dimension(930, 600)); frame.setLocationRelativeTo(null);
        adjustSpeed(); generate(); frame.setVisible(true); timer.start();
    }
    private void addLegend(JPanel panel, String text, Color color) {
        JLabel label = new JLabel("  " + text);
        label.setForeground(new Color(210, 220, 234));
        label.setIcon(new javax.swing.Icon() {
            public int getIconWidth() { return 12; }
            public int getIconHeight() { return 12; }
            public void paintIcon(Component c, Graphics g, int x, int y) {
                g.setColor(color); g.fillRect(x, y, 12, 12);
            }
        });
        panel.add(label);
    }
    private void generate() {
        try {
            rows.commitEdit(); columns.commitEdit();
            long parsedSeed = Long.parseLong(seed.getText().trim());
            Maze fresh = new Maze((Integer) rows.getValue(), (Integer) columns.getValue());
            maze = fresh; activeSeed = parsedSeed;
            generator = new MazeGenerator(maze, activeSeed); solver = null; running = true;
            update();
        } catch (ParseException | IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(frame, "Use dimensions from 1 to 100 and an integer seed within the signed 64-bit range.",
                    "Check maze settings", JOptionPane.WARNING_MESSAGE);
        }
    }
    private int stepsPerSecond() { return (int) Math.round(Math.pow(1000, (speed.getValue() - 1) / 99.0)); }
    private void adjustSpeed() { timer.setDelay(Math.max(16, 1000 / stepsPerSecond())); }
    private void tick() {
        if (!running) return;
        int batch = Math.max(1, (int) Math.round(stepsPerSecond() * timer.getDelay() / 1000.0));
        for (int i = 0; i < batch && running; i++) advance();
        update();
    }
    private void advance() {
        if (!generator.isDone()) {
            generator.step();
            if (generator.isDone()) {
                if (autoSolve.isSelected()) solver = new MazeSolver(maze);
                else running = false;
            }
        } else if (solver != null && !solver.isDone()) {
            solver.step();
            if (solver.isDone()) running = false;
        } else running = false;
    }
    private void update() {
        boolean active = !generator.isDone() || (solver != null && !solver.isDone());
        pause.setText(running ? "Pause" : "Resume"); pause.setEnabled(active);
        step.setEnabled(active && !running); solve.setEnabled(generator.isDone() && (solver == null || solver.isDone()));
        String description;
        if (!generator.isDone()) description = "Generating: " + generator.visitedCount() + " / " + maze.size() + " cells carved";
        else if (solver == null) description = "Maze ready. Select Solve again to run BFS.";
        else if (!solver.isDone()) description = "Searching: " + solver.exploredCount() + " / " + maze.size() + " cells explored";
        else description = solver.foundPath() ? "Solved: " + (solver.path().size() - 1) + " moves, " + solver.exploredCount() + " cells explored" : "No path exists";
        status.setText(description + (active && !running ? "  |  Paused" : "") + "  |  " + maze.rows() + " x " + maze.columns() + "  |  Seed " + activeSeed);
        canvas.showState(maze, generator, solver);
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) { /* Swing's default look and feel is sufficient. */ }
            new MazeApp();
        });
    }
}
