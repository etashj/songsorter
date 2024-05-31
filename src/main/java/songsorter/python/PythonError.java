package songsorter.python;

/**
 * Generic error type for all Python tasks
 */
public class PythonError extends Exception {
    
    /**
     * Constructs a {@code PythonError} with {@code null}
     * as its error message string.
     */
    public PythonError() {
        super(); 
    }

    /**
     * Constructs a {@code PythonError} with the specified message
     * as its error message string.
     * @param s the error message
     */
    public PythonError(String s) {
        super(s); 
    }

}
