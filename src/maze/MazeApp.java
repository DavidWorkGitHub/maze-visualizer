package maze;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/** Main window and button handling. The algorithms live in separate classes. */
public class MazeApp extends JFrame implements ActionListener {
    private JTextField rowsField = new JTextField("20", 3);
    private JTextField columnsField = new JTextField("20", 3);
    private JButton generateButton = new JButton("Generate Maze");
    private JButton clearButton = new JButton("Clear");
    private JButton solveButton = new JButton("Solve Maze");
    private JButton pauseButton = new JButton("Pause");
    private JLabel status = new JLabel("Enter a size and click Generate.");
    private MazePanel canvas = new MazePanel();
    private Timer timer;
    private Maze maze;
    private MazeGenerator generator;
    private MazeSolver solver;
    private boolean generating = false;

    public MazeApp() {
        super("Maze Visualizer - DFS + BFS");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Explain each control when the mouse rests over it.
        rowsField.setToolTipText("Number of rows, from 1 to 50. Try 10 to start.");
        columnsField.setToolTipText("Number of columns, from 1 to 50. Try 10 to start.");
        generateButton.setToolTipText("Create a new random maze using DFS.");
        solveButton.setToolTipText("Find the shortest path after generation finishes.");
        pauseButton.setToolTipText("Pause or resume the current animation.");
        clearButton.setToolTipText("Remove the current maze from the canvas.");

        // Alt + the letter activates a button without using the mouse.
        generateButton.setMnemonic('G');
        solveButton.setMnemonic('S');
        pauseButton.setMnemonic('P');
        clearButton.setMnemonic('C');

        JPanel heading = new JPanel(new GridLayout(2, 1));
        JLabel title = new JLabel("MAZE VISUALIZER", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(new Color(43, 57, 75));
        heading.add(title);

        JPanel controls = new JPanel(new FlowLayout());
        controls.add(new JLabel("Rows (1-50):"));
        controls.add(rowsField);
        controls.add(new JLabel("Columns (1-50):"));
        controls.add(columnsField);
        controls.add(generateButton);
        controls.add(solveButton);
        controls.add(pauseButton);
        controls.add(clearButton);
        heading.add(controls);
        heading.setBorder(new EmptyBorder(12, 8, 4, 8));

        JPanel footer = new JPanel(new GridLayout(4, 1, 0, 4));
        footer.setBorder(new EmptyBorder(8, 16, 12, 16));
        footer.add(new JLabel("White: unvisited    Blue: visited    Orange: current cell    Green: final path"));
        footer.add(new JLabel("Start: top-left (S)    Exit: bottom-right (E)"));
        footer.add(new JLabel("Animation: one DFS or BFS step at a time"));
        footer.add(status);
        add(heading, BorderLayout.NORTH);
        add(canvas, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);

        generateButton.addActionListener(this);
        solveButton.addActionListener(this);
        pauseButton.addActionListener(this);
        clearButton.addActionListener(this);
        rowsField.addActionListener(this);
        columnsField.addActionListener(this);
        solveButton.setEnabled(false);
        pauseButton.setEnabled(false);

        // Swing calls actionPerformed every 15 ms, allowing one algorithm step.
        timer = new Timer(15, this);
        pack();
        setMinimumSize(new java.awt.Dimension(720, 480));
        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        // Pressing Enter in either size field also generates a maze.
        if (event.getSource() == generateButton
                || event.getSource() == rowsField
                || event.getSource() == columnsField) {
            startGeneration();
        } else if (event.getSource() == solveButton) {
            startSolving();
        } else if (event.getSource() == clearButton) {
            clearMaze();
        } else if (event.getSource() == pauseButton) {
            if (timer.isRunning()) {
                timer.stop();
                pauseButton.setText("Resume");
            } else {
                timer.start();
                pauseButton.setText("Pause");
            }
        } else if (event.getSource() == timer) {
            advanceAnimation();
        }
    }

    private void startGeneration() {
        try {
            int rows = Integer.parseInt(rowsField.getText().trim());
            int columns = Integer.parseInt(columnsField.getText().trim());
            // Validate before replacing the current maze.
            Maze newMaze = new Maze(rows, columns);
            timer.stop();
            maze = newMaze;
            generator = new MazeGenerator(maze);
            solver = null;
            generating = true;
            canvas.setMaze(maze);
            canvas.setCurrent(generator.getCurrent());
            solveButton.setEnabled(false);
            pauseButton.setEnabled(true);
            pauseButton.setText("Pause");
            status.setText("Generating " + rows + " x " + columns + " maze using DFS...");
            timer.start();
        } catch (IllegalArgumentException exception) {
            JOptionPane.showMessageDialog(this, "Enter whole numbers from 1 to 50 for rows and columns.",
                    "Invalid size", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void clearMaze() {
        timer.stop();
        maze = null;
        generator = null;
        solver = null;
        generating = false;
        canvas.clearMaze();
        solveButton.setEnabled(false);
        pauseButton.setEnabled(false);
        pauseButton.setText("Pause");
        status.setText("Canvas cleared. Enter a size and click Generate.");
    }

    private void startSolving() {
        solver = new MazeSolver(maze);
        generating = false;
        canvas.setCurrent(null);
        solveButton.setEnabled(false);
        pauseButton.setEnabled(true);
        pauseButton.setText("Pause");
        status.setText("Searching for the shortest path using BFS...");
        timer.start();
    }

    private void advanceAnimation() {
        if (generating) {
            generator.step();
            canvas.setCurrent(generator.getCurrent());
            if (generator.isFinished()) {
                finishAnimation();
                status.setText("Maze generated. Click Solve to watch BFS find the exit.");
            }
        } else {
            solver.step();
            canvas.setCurrent(solver.getCurrent());
            if (solver.isFinished()) {
                finishAnimation();
                if (solver.hasFoundPath()) {
                    status.setText("Solved! Shortest path: " + solver.getPathLength() + " moves.");
                } else {
                    status.setText("No path to the exit was found.");
                }
            }
        }
    }

    private void finishAnimation() {
        timer.stop();
        pauseButton.setEnabled(false);
        solveButton.setEnabled(true);
        canvas.setCurrent(null);
    }

    public static void main(String[] args) {
        // Swing windows should be created on Swing's event thread.
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MazeApp();
            }
        });
    }
}
