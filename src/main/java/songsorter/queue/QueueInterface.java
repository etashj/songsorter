package songsorter.queue;

// Etash Jhanji

import java.util.NoSuchElementException;

/**
 * The interface for a Queue. 
 * @param <E> type of the objects in the queue
 */
public interface QueueInterface<E> {
    /**
     * Tells queue size
     * @return Size of the queue
     */
    public int size(); 

    /**
     * Tells if queue is empty
     * @return emptiness as a boolean
     */
    public boolean isEmpty(); 
    
    /**
     * Gets the next element in the queue to be dequeued, 
     * @return the the "first out" element of the stack
     */
    public E peek() throws NoSuchElementException; 

    /**
     * Queues a new object to the queue. 
     * @param data the object to be added to the back
     */
    public void enqueue(E data); 

    /**
     * Pops the top element (at the front) of the queue and returns it
     * @return the next element in the queue that was deleted
     */
    public E dequeue() throws NoSuchElementException; 
}
