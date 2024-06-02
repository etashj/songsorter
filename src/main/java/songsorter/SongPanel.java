package songsorter;

import javax.swing.JPanel;
import javax.swing.ImageIcon; 

import org.apache.hc.core5.http.ParseException;

import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import songsorter.music.SpotifySong;
import songsorter.python.PythonEnvHandler;
import songsorter.python.PythonError;

import java.awt.GridBagLayout;
import java.awt.Image;
import java.io.IOException;

import javax.swing.JLabel;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Font;

/**
 * Standardized panel class for the playlist output screen for each song to be added to a pnel and form a list. 
 */
public class SongPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private SpotifySong song; 
	private JLabel arousalLabel, valenceLabel;

	/**
	 * Updates the labels on each {@code SongPanel} after emotions are computed
	 * @param penv the {@code PythonEnvHandler} which handles emotion computation
	 * @throws PythonError If underlying Pythons service fails
	 * @throws IOException If reading/writing to CLI fails
     * @throws InterruptedException If underlying Python script fails mid-execution
	 */
	public void updateEmotions(PythonEnvHandler penv) throws PythonError, IOException, InterruptedException {
		song.updateEmotions(penv);
		arousalLabel.setText("Arousal: " + (int)(song.getAverageArousal()*10000)/10000.0);
		arousalLabel.paintImmediately(arousalLabel.getVisibleRect());
		valenceLabel.setText("Valence: " + (int)(song.getAverageArousal()*10000)/10000.0);
		arousalLabel.paintImmediately(arousalLabel.getVisibleRect());
	}
	
	/**
	 * Create the panel.
	 * @throws IOException 
	 * @throws SpotifyWebApiException 
	 * @throws ParseException 
	 * @throws PythonError If the underlying Python service fails
	 */
	public SongPanel(SpotifySong s) throws ParseException, SpotifyWebApiException, IOException, PythonError {
		this.setPreferredSize(new Dimension(200, 100));
		song = s; 
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] {0, 0};
		gridBagLayout.rowHeights = new int[] {0, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, 0.0};
		gridBagLayout.rowWeights = new double[]{0.0, 1.0, 1.0};
		setLayout(gridBagLayout);
		
		ImageIcon iI = new ImageIcon(s.getArt()); 
		Image img = iI.getImage(); 
		Image newimg = img.getScaledInstance(100, 100,  java.awt.Image.SCALE_SMOOTH);
		iI = new ImageIcon(newimg);  
		JLabel lblNewLabel = new JLabel(iI);
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 1;
		gbc_lblNewLabel.gridy = 0;
		add(lblNewLabel, gbc_lblNewLabel);
		
		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 0;
		add(panel, gbc_panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{73, 0, 0};
		gbl_panel.rowHeights = new int[] {0, 0};
		gbl_panel.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, 0.0};
		panel.setLayout(gbl_panel);
		
		JLabel lblTrack = new JLabel(s.getTitle());
		lblTrack.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		GridBagConstraints gbc_lblTrack = new GridBagConstraints();
		gbc_lblTrack.fill = GridBagConstraints.BOTH;
		gbc_lblTrack.insets = new Insets(0, 0, 5, 5);
		gbc_lblTrack.gridx = 0;
		gbc_lblTrack.gridy = 0;
		panel.add(lblTrack, gbc_lblTrack);
		
		try {
			arousalLabel = new JLabel("Arousal: " + (int)(s.getAverageArousal()*10000)/10000.0);
		} catch (NullPointerException e) {
			arousalLabel = new JLabel("...");
		}
		GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
		gbc_lblNewLabel_2.insets = new Insets(0, 0, 5, 0);
		gbc_lblNewLabel_2.gridx = 1;
		gbc_lblNewLabel_2.gridy = 0;
		panel.add(arousalLabel, gbc_lblNewLabel_2);
		
		JLabel lblArtist = new JLabel(s.getArtists());
		lblArtist.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		GridBagConstraints gbc_lblArtist = new GridBagConstraints();
		gbc_lblArtist.insets = new Insets(0, 0, 5, 5);
		gbc_lblArtist.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblArtist.gridx = 0;
		gbc_lblArtist.gridy = 1;
		panel.add(lblArtist, gbc_lblArtist);
		
		try {
			valenceLabel = new JLabel("Valence: " + (int)(s.getAverageArousal()*10000)/10000.0);
		} catch (NullPointerException e) {
			valenceLabel = new JLabel("...");
		}
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 0);
		gbc_lblNewLabel_1.gridx = 1;
		gbc_lblNewLabel_1.gridy = 1;
		panel.add(valenceLabel, gbc_lblNewLabel_1);

	}

}
