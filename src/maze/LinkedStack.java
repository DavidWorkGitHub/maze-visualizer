package maze;

import java.util.NoSuchElementException;

/** A minimal linked LIFO stack; push and pop take constant time. */
public final class LinkedStack<T> {
    private static final class Node<T> {
        final T value;
        final Node<T> next;
        Node(T value, Node<T> next) { this.value = value; this.next = next; }
    }
    private Node<T> top;
    private int size;
    public void push(T value) { top = new Node<>(value, top); size++; }
    public T pop() {
        T value = peek();
        top = top.next;
        size--;
        return value;
    }
    public T peek() {
        if (isEmpty()) throw new NoSuchElementException("Stack is empty");
        return top.value;
    }
    public boolean isEmpty() { return top == null; }
    public int size() { return size; }
}
