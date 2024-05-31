package songsorter.queue;

// Etash Jhanji

import java.util.NoSuchElementException;

/**
 * Array-based/index-based implementation of the {@code QueueInterface}. 
 * @param <E> the object type of the {@code ArrayQueue}. 
 */
public class ArrayQueue<E> implements QueueInterface<E> {

    // Private data
    private final int size; 
    private int count, front, back; 
    private E[] arr; 

    /**
     * Initializes an {@code ArrayQueue} of a specified size, this cannot be resized. 
     * @param n the size of the queue
     */
    @SuppressWarnings("unchecked")
    public ArrayQueue(int n) {
        size = n; 
        arr = (E[]) new Object[size];
        front = 0; 
        back = size-1; 
        count = 0; 
    }

    public ArrayQueue(E[] a) {
        size = a.length; 
        arr = a; 
        front = 0; 
        back = a.length; 
        count = a.length; 
    }

    /**
     * Check the the number of elements in the queue
     * @return the number of elements in the queue
     */
    @Override
    public int size() {
        return count; 
    }

    /**
     * Check if the queue has no elements
     * @return if the queue is empty or not
     */
    @Override
    public boolean isEmpty() {
        return count == 0; 
    }

    /**
     * Check if the queue has reached maximum capacity
     * @return if the queue is full or not
     */
    public boolean isFull() {
        return count == size; 
    }

    /**
     * Check the next element to be dequeued without dequeuing it
     * @return the next element
     */
    @Override
    public E peek() throws NoSuchElementException {
        return arr[front]; 
    }

    /**
     * Adds/enqueues an element to the back of the stack
     * @param data the object to be added to the stack
     * @throws QueueOverflowException if the queue is already at maximum capacity
     */
    @Override
    public void enqueue(E data) throws QueueOverflowException {
        if (isFull()) {
            throw new QueueOverflowException(); 
        }
        back = (back+1) % size; 
        arr[back] = data; 
        count++; 
    }

    /**
     * Pops/dequeues an element to the back of the stack
     * @return the data that was popped from the queue
     * @throws NoSuchElementException if the queue is empty.
     */
    @Override
    public E dequeue() throws NoSuchElementException {
        if (isEmpty()) {
            throw new NoSuchElementException(); 
        }
        E qFront = arr[front]; 
        front = (front+1) % size; 
        count--; 
        return qFront; 
    }
    
}
