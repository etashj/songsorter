package songsorter.music;

/**
 * Exception for if a {@code SpotifySong} has no preview avaible on the web API. 
 */
public class NoPreviewException  extends RuntimeException {
    
    /**
     * Constructs a {@code IllegalAudioLengthException} with {@code null}
     * as its error message string.
     */
    public NoPreviewException() {
        super(); 
    }

    /**
     * Constructs a {@code IllegalAudioLengthException} with the specified message
     * as its error message string.
     * @param s the error message
     */
    public NoPreviewException(String s) {
        super(s); 
    }

}
