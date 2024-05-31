package songsorter.queue;

// Etash Jhanji

/**
 * Exception to be thrown whenever a queue, typically array-based, 
 * runs out of spots for a new object. 
 */
public class QueueOverflowException extends RuntimeException {
    
    /**
     * Constructs a {@code QueueOverflowException} with {@code null}
     * as its error message string.
     */
    public QueueOverflowException() {
        super(); 
    }

    /**
     * Constructs a {@code QueueOverflowException} with the specified message
     * as its error message string.
     * @param s the error message
     */
    public QueueOverflowException(String s) {
        super(s); 
    }

}
