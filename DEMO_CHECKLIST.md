# Maze demonstration checklist

Use this sheet when checking the app before presenting it. Tick a box only after trying it.

## Normal use

- [ ] Generate a 10 x 10 maze. Watch passages appear and the orange cell backtrack at dead ends.
- [ ] Pause generation, check that the maze stops changing, then resume.
- [ ] Solve the maze. Check that the green route connects the top-left start to the bottom-right exit without crossing a wall.
- [ ] Solve the same maze again. The route and number of moves should stay the same.
- [ ] Generate and solve a 30 x 30 maze. Check that the window still responds while animating.

## Input and controls

- [ ] Enter a size and press Enter in each size field to start generation.
- [ ] Try Alt+G, Alt+S, and Alt+P for Generate, Solve, and Pause / Resume.
- [ ] Enter a blank value, a word, zero, and 51. Each should show the size error without replacing the current maze.
- [ ] Generate a new maze during an animation. The new maze should replace the old one.
- [ ] Resize the window. The maze should stay inside the canvas with square cells.

## Edge cases

- [ ] Solve a 1 x 1 maze. The result should be zero moves.
- [ ] Solve a 1 x 10 maze. The result should be nine moves.
- [ ] Solve a 10 x 1 maze. The result should also be nine moves.

## Explain the algorithms

- [ ] Explain why DFS uses a stack and how popping allows backtracking.
- [ ] Explain why BFS uses a queue and finds a shortest path.
- [ ] Show where both sides of a shared wall are removed.
- [ ] Show how the parent array is used to mark the final path.
- [ ] Confirm the Stack and Queue classes match the versions required by your lab.

Record the date, maze size, and any unexpected behaviour when something fails. The automated tests complement these checks but do not replace demonstrating the GUI yourself.
