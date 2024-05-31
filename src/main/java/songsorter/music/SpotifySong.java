package songsorter.music;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import org.apache.hc.core5.http.ParseException;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.PlaylistTrack;
import se.michaelthelin.spotify.model_objects.specification.Track;
import songsorter.python.PythonEnvHandler;
import songsorter.python.PythonError;

/**
 * Subclass of {@code Song} made for Spotify Tracks pulled from the web API. 
 */
public class SpotifySong extends Song{
    private String url, previewURL, album, id, artists; 
    private Track spotTrack; 
    private BufferedImage art; 

    /** Directory path of the song cache */
    public static final String OUTPUT_PATH = System.getProperty("user.home") + "/Library/Caches/" + "io.github.etashj.songsorter/"; 

    /**
     * Constructor that creates an object, caches the clip, and computes the emotions of the clip
     * @param sapi the {@code se.michaelthelin.spotify.SpotifyApi} object with credentials that enable web API requests
     * @param spotifyURL the URL to the song
     * @param penv The {@code PythonEnvHandler} to pass each song to machine learning model
     * @throws URISyntaxException If the URL is malformed
     * @throws IOException If the file writing/download fails or reading/writing to CLI fails
     * @throws MalformedURLException  If the URL is not a spotify URL
     * @throws SpotifyWebApiException 
     * @throws PythonError If underlying Python service fails
     * @throws ParseException 
     * @throws InterruptedException If underlying Python script fails mid-execution
     */
    public SpotifySong(SpotifyApi sapi, String spotifyURL, PythonEnvHandler penv) throws MalformedURLException, IOException, URISyntaxException, SpotifyWebApiException, PythonError, ParseException, InterruptedException {
        url = spotifyURL; 
        id = getSongID(spotifyURL); 
        spotTrack = sapi.getTrack(id).build().execute();
        previewURL = spotTrack.getPreviewUrl(); 
        album = spotTrack.getAlbum().getName(); 
        id = spotTrack.getId(); 
        
        StringBuilder artistSB = new StringBuilder(); 
        for (ArtistSimplified as: spotTrack.getArtists()) {
        	artistSB.append(as.getName()).append(","); 
        }
        artists = artistSB.toString(); 
        artists = artists.substring(0, artists.length()-1); 

        setTitle(spotTrack.getName());
        
        art = ImageIO.read(new URI(spotTrack.getAlbum().getImages()[0].getUrl()).toURL());
        
        cacheSongClip();

		super.setEmotions(penv);
    }
    
    /**
     * Constructor that creates an object and caches the clip. It does NOT compute emotions. 
     * @param sapi the {@code se.michaelthelin.spotify.SpotifyApi} object with credentials that enable web API requests
     * @param spotifyURL the URL to the song
     * @throws URISyntaxException If the URL is malformed
     * @throws IOException If the file writing/download fails
     * @throws MalformedURLException  If the URL is not a spotify URL
     * @throws SpotifyWebApiException 
     * @throws ParseException 
     */
    public SpotifySong(SpotifyApi sapi, String spotifyURL) throws MalformedURLException, IOException, URISyntaxException, SpotifyWebApiException, ParseException {
        url = spotifyURL; 
        id = getSongID(spotifyURL); 
        spotTrack = sapi.getTrack(id).build().execute();
        previewURL = spotTrack.getPreviewUrl(); 
        album = spotTrack.getAlbum().getName(); 
        id = spotTrack.getId(); 
        
        StringBuilder artistSB = new StringBuilder(); 
        for (ArtistSimplified as: spotTrack.getArtists()) {
        	artistSB.append(as.getName()).append(","); 
        }
        artists = artistSB.toString(); 
        artists = artists.substring(0, artists.length()-1); 

        setTitle(spotTrack.getName());
        
        art = ImageIO.read(new URI(spotTrack.getAlbum().getImages()[0].getUrl()).toURL());
        
        cacheSongClip(); 
    }
    
    /**
     * Constructor that creates an object, caches the clip, and computes emotions.
     * <p>Essentially SpotifySong(Track track, PythonEnvHandler penv) with typecasting from PlaylistTrack to Track. </p>
     * @param track the {@code se.michaelthelin.spotify.specification.PlaylistTrack} object representing the song
     * @param penv penv The {@code PythonEnvHandler} to pass each song to machine learning model
     * @throws URISyntaxException If the URL is malformed
     * @throws IOException If the file writing/download fails or reading/writing to CLI fails
     * @throws MalformedURLException  If the URL is not a spotify URL
     * @throws SpotifyWebApiException 
     * @throws PythonError If underlying Python service fails
     * @throws InterruptedException If underlying Python script fails mid-execution
     */
    public SpotifySong(PlaylistTrack track, PythonEnvHandler penv) throws MalformedURLException, SpotifyWebApiException, IOException, URISyntaxException, PythonError, InterruptedException {
        this((Track) track.getTrack(), penv); 
    }
    
    /**
     * Constructor that creates an object, caches the clip, and computes emotions.
     * @param track the {@code se.michaelthelin.spotify.specification.Track} object representing the song
     * @param penv penv The {@code PythonEnvHandler} to pass each song to machine learning model
     * @throws URISyntaxException If the URL is malformed
     * @throws IOException If the file writing/download fails or reading/writing to CLI fails
     * @throws MalformedURLException  If the URL is not a spotify URL
     * @throws SpotifyWebApiException 
     * @throws PythonError If underlying Python service fails
     * @throws InterruptedException If underlying Python script fails mid-execution
     */
    public SpotifySong(Track track, PythonEnvHandler penv) throws MalformedURLException, IOException, URISyntaxException, SpotifyWebApiException, PythonError, InterruptedException {
        super(); 
        url = track.getHref(); 
        previewURL = track.getPreviewUrl(); 
        album = track.getAlbum().getName(); 
        id = track.getId(); 
        
        StringBuilder artistSB = new StringBuilder(); 
        for (ArtistSimplified as: spotTrack.getArtists()) {
        	artistSB.append(as.getName()).append(", "); 
        }
        artists = artistSB.toString();
        artists = artists.substring(0, artists.length()-2); 
        
        spotTrack = track; 
        setTitle(track.getName());
        
        art = ImageIO.read(new URI(spotTrack.getAlbum().getImages()[0].getUrl()).toURL());

        cacheSongClip();

        super.setEmotions(penv);
    }

    /**
     * Constructor that creates an object, caches the clip, without computing emotions.
     * @param track the {@code se.michaelthelin.spotify.specification.Track} object representing the song
     * @throws URISyntaxException If the URL is malformed
     * @throws IOException If the file writing/download fails
     * @throws MalformedURLException  If the URL is not a spotify URL
     * @throws SpotifyWebApiException 
     */
    public SpotifySong(Track track) throws MalformedURLException, IOException, URISyntaxException, SpotifyWebApiException {
        super(); 
        spotTrack = track; 
        url = track.getHref(); 
        previewURL = track.getPreviewUrl(); 
        album = track.getAlbum().getName(); 
        id = track.getId(); 
        
        StringBuilder artistSB = new StringBuilder(); 
        for (ArtistSimplified as: track.getArtists()) {
        	artistSB.append(as.getName()).append(", "); 
        }
        artists = artistSB.toString();
        artists = artists.substring(0, artists.length()-2); 
        
        setTitle(track.getName());
        
        art = ImageIO.read(new URI(spotTrack.getAlbum().getImages()[0].getUrl()).toURL());
        
        cacheSongClip();
    }

    /**
     * Gets the ID of a song fromn the song's URL
     * @param url String song URL
     * @return base64 ID
     */
    public static String getSongID(String url) {
        // https://open.spotify.com/track/6AI3ezQ4o3HUoP6Dhudph3?si=b49e8515825e4723
        String urlVerifier = "open.spotify.com/track/"; 
        int idx = url.indexOf(urlVerifier);
        if (idx == -1) {
            throw new IllegalArgumentException("Invalid URL"); 
        }
        String tempUrl = url.substring(idx+urlVerifier.length()); 
        String retId = tempUrl.split("\\?")[0]; 
        return retId; 
    }

    /**
     * Caches a song clip to the cache directory
     * @return the filepath of the cached song
     * @throws SpotifyWebApiException If spotify API fails
     * @throws IOException If file download fails
     * @throws URISyntaxException If preview URI is malformed
     */
    private String cacheSongClip() throws SpotifyWebApiException, IOException, URISyntaxException, NoPreviewException {
        if (previewURL==null) {
            throw new NoPreviewException(); 
        }

        String fullOutFP = OUTPUT_PATH + id + ".mp3"; 
        
        Path outputPath = Paths.get(fullOutFP);
        super.setFilePath(fullOutFP);
        if (!Files.exists(outputPath)) {
            URLConnection conn = new URI(previewURL).toURL().openConnection();
            InputStream is = conn.getInputStream();
            OutputStream outstream; 

            try {
                outstream = new FileOutputStream(new File(fullOutFP));
            } catch(FileNotFoundException e) {
                new File(OUTPUT_PATH).mkdirs();
                outstream = new FileOutputStream(new File(fullOutFP));
            }

            byte[] buffer = new byte[4096];
            int len;
            while ((len = is.read(buffer)) > 0) {
                outstream.write(buffer, 0, len);
            }
            outstream.close();

            setFilePath(fullOutFP); 
        }

        return fullOutFP; 
    }

    /**
     * Gets the song's url
     * @return songs URL
     */
    public String getUrl() {
        return this.url;
    }

    /**
     * Gets the song's 30-second preview url
     * @return songs URL
     */
    public String getPreviewURL() {
        return this.previewURL;
    }

    /**
     * Gets the song's album
     * @return album name
     */
    public String getAlbum() {
        return this.album;
    }

    /**
     * Gets the base64 ID of song
     * @return song ID
     */
    public String getId() {
        return this.id;
    }

    /**
     * Gets a String list of artists of a song
     * @return songs artists in format [artist1], [artist2], ..., [artistn]. 
     */
    public String getArtists() {
        return this.artists;
    }

    /**
     * Gets the {@code se.michaelthelin.spotify.specification.Track} version of the Song object
     * @return the song as a {@code Track}
     */
    public Track getSpotTrack() {
        return this.spotTrack;
    }
    
    /**
     * Gets song's album art
     * @return {@code BufferedImage} of the song's album art
     */
    public BufferedImage getArt() {
    	return this.art; 
    }
    
    /**
     * Updates emotions if not set in the constructor or recomputes them. 
     * @param penv The {@code PythonEnvHandler} to pass each song to machine learning model
     * @throws IOException If reading/writing to CLI fails
     * @throws InterruptedException If underlying Python script fails mid-execution
     */
    public void updateEmotions(PythonEnvHandler penv) throws PythonError, IOException, InterruptedException {
        super.setEmotions(penv);
    }
    
    /**
     * Checks if a URL is a song
     * @param s the String to be checked
     * @return if the String is a valid track URL with spotify
     */
    public static boolean isSong(String s) { 
    	return s.indexOf("open.spotify.com/track/") > 0; 
    }


    @Override
    public String toString() {
        return "{" +
            " url='" + getUrl() + "'" +
            ", previewURL='" + getPreviewURL() + "'" +
            ", album='" + getAlbum() + "'" +
            ", id='" + getId() + "'" +
            ", artists='" + getArtists() + "'" +
            "}\n" + super.toString();
    }

    /**
     * Finds the distance between two {@code SpotifySong}'s (arousal, valence) coordinates on a 
     * Cartesian plane via the distance formula (Pythagorean theorem)
     * @param s the other {@code SpotifySong}
     * @return the distance between two points. 
     */
    public double distance(SpotifySong s) {
        double x = Math.abs(getAverageArousal() - s.getAverageArousal());
        double y = Math.abs(getAverageValence() - s.getAverageValence());
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }
}
