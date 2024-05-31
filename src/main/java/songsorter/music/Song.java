package songsorter.music;

import javax.sound.sampled.UnsupportedAudioFileException;

import songsorter.python.PythonEnvHandler;
import songsorter.python.PythonError;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.DoubleStream; 

/**
 * Class to represent any generic song, file based or spotify based. Primarily used for file based songs. 
 */
public class Song {
    // Private data
    private String filePath, title; 
    private EmotionPoint[] emotions; 

    /**
     * Default constructor for subclasses
     */
    public Song(){}

    /**
     * Constrctor that creates the Song object without calculating emotions
     * @param fp Filepath of the mp3 to be analyzed
     * @throws UnsupportedAudioFileException If the file is not an mp3
     */
    public Song (String fp) throws UnsupportedAudioFileException {
        filePath = fp; 

        String[] splitted = fp.split("/"); 
        title = splitted[splitted.length-1]; 
    }

    /**
     * Constrctor that creates the Song object and calculates emotions
     * @param fp Filepath of the mp3 to be analyzed
     * @param penv the {@code PythonEnvHandler} that will be used for emotion computation
     * @throws UnsupportedAudioFileException If the file is not an mp3
     * @throws PythonError if Python service fails
     * @throws InterruptedException 
     * @throws IOException 
     */
    public Song (String fp, PythonEnvHandler penv) throws UnsupportedAudioFileException, PythonError, IOException, InterruptedException {
        filePath = fp; 

        String[] splitted = fp.split("/"); 
        title = splitted[splitted.length-1]; 
        
        setEmotions(penv); 
    }

    /**
     * Gets the filepath/location of the song file
     * @return filepath of the mp3
     */
    public String getFilePath() {
        return this.filePath;
    }


    /**
     * Change the Song's filepath
     * @param filePath filepath of the mp3
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Get's the song's title, by default the Spotify track title or file name
     * @return the song's title
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Changes the song's title
     * @param title the new song title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the array of {@code EmotionPoint}s which represent annotatiosn every 0.5 seconds of the mp3
     * @return array of {@code EmotionPoint}s
     */
    public EmotionPoint[] getEmotions() {
        return this.emotions;
    }

    /**
     * (Re)compute the emotions of the Song object
     * @param penv the {@code PythonEnvHandler} that will be used for emotion computation
     * @throws PythonError if underlying Python service fails
     * @throws IOException If reading/writing to CLI fails
     * @throws InterruptedException If underlying Python script fails mid-execution
     */
    public void setEmotions(PythonEnvHandler penv) throws PythonError, IOException, InterruptedException {
    	if (emotions == null) {
    		emotions = PythonEnvHandler.parseOut(penv.runCommand(filePath));
    	}
    }
    
    /**
     * Get only the arousals of the song as an array
     * @return An array of double's representing only the {@code getArousal()}s of each {@code EmotionPoint}
     */
    public double[] getArousalAsList() {
    	return Arrays.stream(emotions).flatMapToDouble(point -> DoubleStream.of(point.getArousal())).toArray(); 
    }
    
    /**
     * Get only the valences of the song as an array
     * @return An array of double's representing only the {@code getValences()}s of each {@code EmotionPoint}
     */
    public double[] getValenceAsList() {
    	return Arrays.stream(emotions).flatMapToDouble(point -> DoubleStream.of(point.getValence())).toArray(); 
    }
    
    /**
     * Works by compptuing the average of {@code getArousalAsList()}
     * @return the average arousal of the song
     */
    public double getAverageArousal() {
    	return (double) (DoubleStream.of(getArousalAsList()).sum())/emotions.length; 
    }
    
    /**
     * Works by compptuing the average of {@code getValenceAsList()}
     * @return the average valence of the song
     */
    public double getAverageValence() {
    	return (double) (DoubleStream.of(getValenceAsList()).sum())/emotions.length; 
    } 

    /**
     * Method to get the avarage emotion coordinates from a {@code Song} as an 
     * interpretable emotion as per Figure 1 from Yik, M., Russell, J.A., & 
     * Steiger, J.H. (2011). A 12-Point Circumplex Structure of Core Affect. Emotion, 11 4, 705-31. 
     * @return String emotion
     */
    public String asEmotion() {
        return new EmotionPoint(getAverageValence(), getAverageArousal()).asEmotion(); 
    }
}
