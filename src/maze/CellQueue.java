package maze;

/** Array-based queue: first in, first out. Replace with the lab queue if required. */
public class CellQueue {
    private Cell[] items;
    private int front = 0;
    private int rear = 0;
    private int size = 0;

    public CellQueue(int capacity) {
        if (capacity < 1) throw new IllegalArgumentException("Capacity must be positive.");
        items = new Cell[capacity];
    }

    public void enqueue(Cell cell) {
        if (size == items.length) throw new IllegalStateException("Queue is full.");
        items[rear] = cell;
        rear++;
        // Wrap to the start to reuse spaces left by removed cells.
        if (rear == items.length) rear = 0;
        size++;
    }

    public Cell dequeue() {
        if (isEmpty()) throw new IllegalStateException("Queue is empty.");
        Cell cell = items[front];
        items[front] = null;
        front++;
        if (front == items.length) front = 0;
        size--;
        return cell;
    }

    public boolean isEmpty() {
        return size == 0;
    }
}
