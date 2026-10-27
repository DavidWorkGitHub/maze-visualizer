package maze;

/** Array-based stack: last in, first out. Replace with the lab stack if required. */
public class CellStack {
    private Cell[] items;
    private int size = 0;

    public CellStack(int capacity) {
        if (capacity < 1) throw new IllegalArgumentException("Capacity must be positive.");
        items = new Cell[capacity];
    }

    public void push(Cell cell) {
        if (size == items.length) throw new IllegalStateException("Stack is full.");
        items[size] = cell;
        size++;
    }

    public Cell pop() {
        if (isEmpty()) throw new IllegalStateException("Stack is empty.");
        size--;
        Cell cell = items[size];
        items[size] = null;
        return cell;
    }

    public Cell peek() {
        if (isEmpty()) throw new IllegalStateException("Stack is empty.");
        return items[size - 1];
    }

    public boolean isEmpty() {
        return size == 0;
    }
}
