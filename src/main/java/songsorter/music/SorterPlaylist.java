package songsorter.music;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.hc.core5.http.ParseException;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.Playlist;
import se.michaelthelin.spotify.model_objects.specification.PlaylistTrack;
import se.michaelthelin.spotify.model_objects.specification.Track;
import songsorter.python.PythonEnvHandler;
import songsorter.python.PythonError;
import songsorter.queue.ArrayQueue;

/**
 * A playlist object only for the SongSorter application, separate from {@code se.michaelthelin.spotify.model_objects.specification.Playlist}. 
 * Creates an object for public Spotify playlists. 
 * Enables nearest neighbour heuristic solution to TSP. 
 */
public class SorterPlaylist {
    private SpotifySong[] songs; 
    private String name; 

    /**
     * Constructs a {@code SorterPlaylist} from a url to a spotify song.
     * @param sapi the {@code se.michaelthelin.spotify.SpotifyApi} object with credentials that enable web API requests
     * @param url Spotify url to playlist
     * @throws ParseException If underlying service fails
     * @throws SpotifyWebApiException If an http error occurs int eh api request
     * @throws IOException If api output fails to be read
     * @throws URISyntaxException If URL is malformed
     */
    public SorterPlaylist(SpotifyApi sapi, String url) throws ParseException, SpotifyWebApiException, IOException, URISyntaxException {
        String id = getPlaylistID(url); 
        Playlist p = sapi.getPlaylist(id).build().execute(); 
        PlaylistTrack[] trackList = p.getTracks().getItems();
        songs = new SpotifySong[trackList.length]; 

        name = p.getName(); 
        
        int i = 0;
        for (PlaylistTrack t: trackList) {
            try {
                songs[i] = new SpotifySong((Track) t.getTrack()); 
            } catch (NoPreviewException e) {
                System.out.println("A song does not have a preview and will be excluded from results");
                i--; 
            }
            i++; 
        }
    }

    /**
     * Gets the playlist ID given a Spotify URL
     * @param url Playlist URL on spotify
     * @return base64 playlist ID
     */
    public static String getPlaylistID(String url) {
        // https://open.spotify.com/playlist/0Blpqc9RW7CpNGSIkVlTSb?si=8e38c77047ab4fbf
        String urlVerifier = "open.spotify.com/playlist/"; 
        int idx = url.indexOf(urlVerifier);
        if (idx == -1) {
            throw new IllegalArgumentException("Invalid URL"); 
        }
        String tempUrl = url.substring(idx+urlVerifier.length()); 
        String retId = tempUrl.split("\\?")[0]; 
        return retId; 
    }

    /**
     * Gets a specifc {@code SpotifySong} at an index
     * @param i Index
     * @return the {@code SpotifySong} at specified index
     */
    public SpotifySong get(int i) {
        return songs[i]; 
    }

    /**
     * Computes the emotions of each song in the playlist (time consuming process)
     * @param penv The {@code PythonEnvHandler} to pass each song to machine learning model
     * @throws PythonError If Python throws an error
     * @throws IOException If reading/writing to CLI fails
     * @throws InterruptedException If underlying Python script fails mid-execution
     */
    public void setEmotions(PythonEnvHandler penv) throws PythonError, IOException, InterruptedException {
        for (SpotifySong s: songs) {
            if (s!=null)
                s.setEmotions(penv);
        }
    }

    /**
     * Gets the average arousal of each song in an array with the same order as playlist
     * @return array of average arousals
     */
    public double[] avgArousals() {
        double[] arousals = new double[songs.length]; 
        for(int i=0; i<songs.length; i++) {
            if (songs[i] != null) {
                arousals[i] = songs[i].getAverageArousal(); 
            }
        }
        return arousals; 
    }

    /**
     * Gets the average valence of each song in an array with the same order as playlist
     * @return array of average valences
     */
    public double[] avgValences() {
        double[] valences = new double[songs.length]; 
        for(int i=0; i<songs.length; i++) {
            if (songs[i] != null) {
                valences[i] = songs[i].getAverageValence(); 
            }
        }
        return valences; 
    }

    /**
     * Gets the playlist as an array-bases Queue
     * @return an {@code ArrayQueue<SpotifySong>} in the same order as the palylist
     */
    public ArrayQueue<SpotifySong> asQueue() {
        return new ArrayQueue<SpotifySong>(songs); 
    }

    /**
     * Gets the number of songs in the playlist
     * @return length of the playlist
     */
    public int length() {
        return songs.length; 
    }
    
    /**
     * Gets the name of the playlist on Spotify
     * @return playlist name
     */
    public String name() {
    	return name; 
    }
    
    /**
     * Get the mean arousals and valences of each song as a JFree dataset for plotting
     * @return JFree dataset of timestamps vs arousals and valences for plotting
     */
    public XYSeriesCollection asDataset() {
    	double[] arousals = avgArousals();
		double[] valences = avgValences();
		
		XYSeriesCollection dataset = new XYSeriesCollection();
		XYSeries arousalSeries = new XYSeries("Arousal");
		XYSeries valenceSeries = new XYSeries("Valence");
		for (int i = 0; i<length(); i++) {
			arousalSeries.add(i+1, arousals[i]); 
			valenceSeries.add(i+1, valences[i]); 
		}
		dataset.addSeries(arousalSeries);
		dataset.addSeries(valenceSeries);
		
		return dataset; 
    }
    
    /**
     * Get a JFreeChart of the timestamps vs arousals and valences
     * @return JFreeChart of the timestamps vs arousals and valences
     */
    public JFreeChart asChart() {
		JFreeChart chart = ChartFactory.createXYLineChart(
                "Arousal/Valence over Songs", 
                "Song Number",       
                "Arousal/Valence",        
                asDataset(),          
                PlotOrientation.VERTICAL,
                true, true, false);
		
		return chart; 
    }
    
    /**
     * Checks if a url is a playlist url
     * @param s URL (or any String)
     * @return if the url is a playlist url
     */
    public static boolean isPlaylist(String s) {
    	return s.indexOf("open.spotify.com/playlist/") > 0; 
    }

    /**
     * Uses a nearest neighbour approach to "sort" the playlist and "solve" the travelling salesman problem via heuristic
     * <p>Does NOT chance the Spotify playlist via API</p>
     */
    public void sort() {
        SpotifySong[] newArr = new SpotifySong[songs.length]; 
        
        int randIdx; 
        do {
            randIdx = (int) (Math.random()*songs.length); 
        } while (songs[randIdx] == null); 
        newArr[0] = songs[randIdx]; 
        songs[randIdx] = songs[0]; 
        songs[0] = null; 

        for(int i=1; i<songs.length; i++) {
            SpotifySong closest = songs[i]; 
            int idx = i; 
            for (int j = i+1; j<songs.length; j++) {
                if (songs[j] != null && newArr[i-1].distance(closest) > newArr[i-1].distance(songs[j])) {
                    closest = songs[j]; 
                    idx = j; 
                }
            }
            newArr[i] = closest; 
            songs[idx] = songs[i];
            songs[i]=null;  
        }

        songs = newArr; 
    }
}
