package maze;

import java.util.NoSuchElementException;

/** A minimal linked FIFO queue with constant-time enqueue and dequeue. */
public final class LinkedQueue<T> {
    private static final class Node<T> {
        final T value;
        Node<T> next;
        Node(T value) { this.value = value; }
    }
    private Node<T> head, tail;
    private int size;
    public void enqueue(T value) {
        Node<T> node = new Node<>(value);
        if (tail == null) head = node;
        else tail.next = node;
        tail = node;
        size++;
    }
    public T dequeue() {
        if (isEmpty()) throw new NoSuchElementException("Queue is empty");
        T value = head.value;
        head = head.next;
        if (head == null) tail = null;
        size--;
        return value;
    }
    public boolean isEmpty() { return head == null; }
    public int size() { return size; }
}
