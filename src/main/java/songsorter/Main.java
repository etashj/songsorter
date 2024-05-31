package songsorter;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.hc.core5.http.ParseException;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYLineAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.ClientCredentials;
import se.michaelthelin.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;
import songsorter.music.NoPreviewException;
import songsorter.music.Song;
import songsorter.music.SorterPlaylist;
import songsorter.music.SpotifySong;
import songsorter.python.PythonEnvHandler;
import songsorter.python.PythonError;
import songsorter.queue.ArrayQueue;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * GUI Class which handles all panels that make up the app with one single JFrame. 
 */
public class Main {

	private JFrame frame;
	private JTextField textField; 
	private JPanel introPanel = new JPanel();
	private JPanel singleOutput = new JPanel();
	private JPanel playlistOutput = new JPanel();
	
	private static SongPanel[] songPanels; 
	private static JPanel panel;
	private static ChartPanel graph;  
	private static SorterPlaylist playlist; 
	
	private static final PythonEnvHandler penv = makePythonEnv();
	private static SpotifyApi sapi; 

	
	/**
	 * Method to create and error handle the private {@code PythonEnvHandler}
	 * @return
	 */
	private static PythonEnvHandler makePythonEnv() {
		try {
			return new PythonEnvHandler(); 
		} catch (PythonError e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main window = new Main();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Main() {
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream("src/main/resources/keys.properties"));
		} catch (IOException e) {
			JOptionPane.showMessageDialog(frame, "Failed to read keys while authenticating the Spotify Client. The program's Spotify processes will not function. ", "Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}


		String clientId = properties.getProperty("clientId");
		String clientSecret = properties.getProperty("clientSecret");
		
		sapi = new SpotifyApi.Builder()
		.setClientId(clientId)
		.setClientSecret(clientSecret)
		.build();
		ClientCredentialsRequest clientCredentialsRequest = sapi.clientCredentials()
		.build();

		ClientCredentials clientCredentials;
		try {
			clientCredentials = clientCredentialsRequest.execute();
			sapi.setAccessToken(clientCredentials.getAccessToken());
		} catch (ParseException e) {
			JOptionPane.showMessageDialog(frame, "A ParseException has occured while authenticating the Spotify Client. The program's Spotify processes will not function. ", "Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		} catch (SpotifyWebApiException e) {
			JOptionPane.showMessageDialog(frame, "A SpotifyWebApiException has occured while authenticating the Spotify Client. The program's Spotify processes will not function. ", "Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(frame, "A IOException has occured while authenticating the Spotify Client. The program's Spotify processes will not function. ", "Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
		
		initialize();
	}

	private void addIntro(JPanel mainInput) {
		mainInput.setAutoscrolls(true);
		
		GridBagLayout gbl_mainInput = new GridBagLayout();
		gbl_mainInput.columnWidths = new int[] {0};
		gbl_mainInput.rowHeights = new int[] {80, 30, 80, 0};
		gbl_mainInput.columnWeights = new double[]{0.0};
		gbl_mainInput.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0};
		mainInput.setLayout(gbl_mainInput);
		
		JPanel urlIn = new JPanel();
		GridBagConstraints gbc_urlIn = new GridBagConstraints();
		gbc_urlIn.anchor = GridBagConstraints.SOUTH;
		gbc_urlIn.insets = new Insets(0, 0, 5, 0);
		gbc_urlIn.gridx = 0;
		gbc_urlIn.gridy = 0;
		mainInput.add(urlIn, gbc_urlIn);
		GridBagLayout gbl_urlIn = new GridBagLayout();
		gbl_urlIn.columnWidths = new int[] {250, 0};
		gbl_urlIn.rowHeights = new int[] {26, 26, 26, 0};
		gbl_urlIn.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gbl_urlIn.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		urlIn.setLayout(gbl_urlIn);
		
		JLabel lblNewLabel_1 = new JLabel("Input a Spotify URL to a playlist or song");
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.fill = GridBagConstraints.BOTH;
		gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 0);
		gbc_lblNewLabel_1.gridx = 0;
		gbc_lblNewLabel_1.gridy = 0;
		urlIn.add(lblNewLabel_1, gbc_lblNewLabel_1);
		
		textField = new JTextField();
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.insets = new Insets(0, 0, 5, 0);
		gbc_textField.fill = GridBagConstraints.BOTH;
		gbc_textField.gridx = 0;
		gbc_textField.gridy = 1;
		urlIn.add(textField, gbc_textField);
		textField.setColumns(10);
		
		JButton goButton = new JButton("Go");
		goButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!textField.getText().equals("")) {
					if (SorterPlaylist.isPlaylist(textField.getText())) {
							try {
								playlist = new SorterPlaylist(sapi, textField.getText());
							} catch (ParseException e1) {
								JOptionPane.showMessageDialog(frame, "A ParseException has occured while getting the Spotify playlist. ", "Error", JOptionPane.ERROR_MESSAGE);
								e1.printStackTrace();
							} catch (SpotifyWebApiException e1) {
								JOptionPane.showMessageDialog(frame, "A SpotifyWebApiException has occured while getting the Spotify playlist. ", "Error", JOptionPane.ERROR_MESSAGE);
								e1.printStackTrace();
							} catch (IOException e1) {
								JOptionPane.showMessageDialog(frame, "An IOException has occured while getting the Spotify playlist. ", "Error", JOptionPane.ERROR_MESSAGE);
								e1.printStackTrace();
							} catch (URISyntaxException e1) {
								JOptionPane.showMessageDialog(frame, "An URISyntaxException has occured while getting the Spotify playlist. ", "Error", JOptionPane.ERROR_MESSAGE);
								e1.printStackTrace();
							}
						mainInput.setVisible(false);
						addMultiOut(playlistOutput);
					} else if (SpotifySong.isSong(textField.getText())) {
							try {
								mainInput.setVisible(false);
								SpotifySong s;
								s = new SpotifySong(sapi, textField.getText());
								addSingleOut(singleOutput, s);
							} catch (NoPreviewException e1) {
								JOptionPane.showMessageDialog(frame, "A Spotify song does not have a public preview, it may be exlcuded from results. ", "Error", JOptionPane.ERROR_MESSAGE);
								e1.printStackTrace();
							}
							catch (MalformedURLException e1) {
								JOptionPane.showMessageDialog(frame, "A MalformedURLException has occured while getting the Spotify song. ", "Error", JOptionPane.ERROR_MESSAGE);
								e1.printStackTrace();
							} catch (SpotifyWebApiException e1) {
								JOptionPane.showMessageDialog(frame, "A SpotifyWebApiException has occured while getting the Spotify song. ", "Error", JOptionPane.ERROR_MESSAGE);
								e1.printStackTrace();
							} catch (IOException e1) {
								JOptionPane.showMessageDialog(frame, "An IOException has occured while getting the Spotify song. ", "Error", JOptionPane.ERROR_MESSAGE);
								e1.printStackTrace();
							} catch (URISyntaxException e1) {
								JOptionPane.showMessageDialog(frame, "A URISyntaxException has occured while getting the Spotify song. ", "Error", JOptionPane.ERROR_MESSAGE);
								e1.printStackTrace();
							} catch (ParseException e1) {
								JOptionPane.showMessageDialog(frame, "A ParseException has occured while getting the Spotify song. ", "Error", JOptionPane.ERROR_MESSAGE);
								e1.printStackTrace();
							} 
					}
					else {
						JOptionPane.showMessageDialog(frame, "The entered URL is invalid. ", "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		GridBagConstraints gbc_goButton = new GridBagConstraints();
		gbc_goButton.gridx = 0;
		gbc_goButton.gridy = 2;
		urlIn.add(goButton, gbc_goButton);
		
		JLabel lblNewLabel_2 = new JLabel("OR");
		lblNewLabel_2.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
		gbc_lblNewLabel_2.insets = new Insets(0, 0, 5, 0);
		gbc_lblNewLabel_2.gridx = 0;
		gbc_lblNewLabel_2.gridy = 1;
		mainInput.add(lblNewLabel_2, gbc_lblNewLabel_2);
		
		JPanel fileIn = new JPanel();
		GridBagConstraints gbc_fileIn = new GridBagConstraints();
		gbc_fileIn.anchor = GridBagConstraints.NORTH;
		gbc_fileIn.fill = GridBagConstraints.HORIZONTAL;
		gbc_fileIn.insets = new Insets(0, 0, 5, 0);
		gbc_fileIn.gridx = 0;
		gbc_fileIn.gridy = 2;
		mainInput.add(fileIn, gbc_fileIn);
		GridBagLayout gbl_fileIn = new GridBagLayout();
		gbl_fileIn.columnWidths = new int[]{130, 0};
		gbl_fileIn.rowHeights = new int[]{26, 26, 0};
		gbl_fileIn.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gbl_fileIn.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		fileIn.setLayout(gbl_fileIn);
		
		JLabel lblImportAFile = new JLabel("Import a file");
		GridBagConstraints gbc_lblImportAFile = new GridBagConstraints();
		gbc_lblImportAFile.fill = GridBagConstraints.VERTICAL;
		gbc_lblImportAFile.insets = new Insets(0, 0, 5, 0);
		gbc_lblImportAFile.gridx = 0;
		gbc_lblImportAFile.gridy = 0;
		fileIn.add(lblImportAFile, gbc_lblImportAFile);
		
		JFileChooser fileChooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
		        "MP3 Files", "mp3");
		fileChooser.setFileFilter(filter);
		fileChooser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (JFileChooser.APPROVE_SELECTION.equals(e.getActionCommand())) {
                    File selectedFile = fileChooser.getSelectedFile();
                    if (selectedFile != null) {
                        mainInput.setVisible(false);
                        try {
							addSingleOut(singleOutput, new Song(selectedFile.getAbsolutePath()));
						} catch (UnsupportedAudioFileException e1) {
							JOptionPane.showMessageDialog(frame, "This file is not supported. ", "Error", JOptionPane.ERROR_MESSAGE);
							e1.printStackTrace();
						}
                    }
                } else if (JFileChooser.CANCEL_SELECTION.equals(e.getActionCommand())) {
                    // Handle cancel action if needed
                }
            }
        });
		GridBagConstraints gbc_fileChooser = new GridBagConstraints();
		gbc_fileChooser.gridx = 0;
		gbc_fileChooser.gridy = 1;
		fileChooser.setSize(new Dimension(10, 10));
		fileIn.add(fileChooser, gbc_fileChooser);
		
		JButton cacheBtn = new JButton("Clear Caches");
		cacheBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				PythonEnvHandler.clearCache();
			}
		});
		GridBagConstraints gbc_cacheBtn = new GridBagConstraints();
		gbc_cacheBtn.gridx = 0;
		gbc_cacheBtn.gridy = 3;
		mainInput.add(cacheBtn, gbc_cacheBtn);
	}
	
	public void addSingleOut(JPanel out, Song so) {
		out.setVisible(true);
		out.removeAll();

		SingleEmotionComputation emoTask = new SingleEmotionComputation(frame, so); 
		emoTask.execute();
		while (!emoTask.isDone()) {
			continue; 
		}

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{900, 0};
		gridBagLayout.rowHeights = new int[] {50, 150, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 1.0, 0.0};
		out.setLayout(gridBagLayout);
		
		JLabel lblNewLabel = new JLabel("Results for " + so.getTitle());
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 0);
		gbc_lblNewLabel.fill = GridBagConstraints.VERTICAL;
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		out.add(lblNewLabel, gbc_lblNewLabel);
		
		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 1;
		out.add(panel, gbc_panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] {200, 650, 0};
		gbl_panel.rowHeights = new int[] {0};
		gbl_panel.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{1.0};
		panel.setLayout(gbl_panel);
		
		double[] arousals = so.getArousalAsList(); 
		double[] valences = so.getValenceAsList(); 
		double[] timestamps = IntStream.range(0, arousals.length).asDoubleStream().map(num -> 0.5*num).toArray(); 
		double avgTimestamp = (double) (DoubleStream.of(timestamps).sum())/timestamps.length; 
		
		// Lin Reg Arousal
		double timestampSxx = 0;
		
		double arousalSyy = 0;
		double arousalSxy = 0;

		double valenceSyy = 0;
		double valenceSxy = 0;

		for (int i = 0; i<timestamps.length; i++) {
			timestampSxx += Math.pow(timestamps[i] - avgTimestamp, 2); 

			arousalSyy += Math.pow((arousals[i]-so.getAverageArousal()), 2); 
			valenceSyy += Math.pow((valences[i]-so.getAverageValence()), 2); 
			
			arousalSxy += (arousals[i]-so.getAverageArousal())*(timestamps[i] - avgTimestamp);
			valenceSxy += (valences[i]-so.getAverageValence())*(timestamps[i] - avgTimestamp);
		}

		// CAlculate Pearson's coefficent of correlation
		double arousalR = arousalSxy/Math.pow(timestampSxx*arousalSyy, 0.5); 
		double valenceR = valenceSxy/Math.pow(timestampSxx*valenceSyy, 0.5); 

		// Calculate standard devation of each variable
		double arousalSD = Math.pow(arousalSyy/arousals.length, 0.5);
		double valenceSD = Math.pow(valenceSyy/valences.length, 0.5);
		double timestampSD = Math.pow(timestampSxx/timestamps.length, 0.5);

		// Calulate the slope of the line (b in y-hat = a + bx)
		double arousalM = arousalR * (arousalSD/timestampSD); 
		double valenceM = valenceR * (valenceSD/timestampSD); 

		// Since the line is guanranteed to pass trough (x-bar, y-bar) it will follow the form: 
		//     y - y-bar = m(x - x-bar)
		//     y = m*x - m*avgTimestamp + y-bar
		// Thus the value of a = m*avgTimestamp + y-bar
		// Drawing a line from two points is trivial beyond that point
		double arousalA = arousalM*avgTimestamp + so.getAverageArousal();
		double valenceA = valenceM*avgTimestamp + so.getAverageValence(); 



		JTextPane txtpnLineline = new JTextPane();
		txtpnLineline.setBackground(UIManager.getColor("Button.background"));
		txtpnLineline.setEditable(false);
		txtpnLineline.setText("  Average Arousal:  " + (int)(so.getAverageArousal()*10000)/10000.0 + "\n  Average Valence:  " + (int)(so.getAverageValence()*10000)/10000.0 + 
														"\n\n  ΔArousal:  " + (int)(arousalM*10000)/10000.0 + "\n  ΔValence:  " + (int)(valenceM*10000)/10000.0 + 
														"\n\n  A = " + (int)(arousalM*10000)/10000.0 + "t + " + (int)(arousalA*10000)/10000.0 + 
														"\n  V = " + (int)(valenceM*10000)/10000.0 + "t + " + (int)(valenceA*10000)/10000.0 + 
														"\n\n  " + so.asEmotion()); 
		GridBagConstraints gbc_txtpnLineline = new GridBagConstraints();
		gbc_txtpnLineline.insets = new Insets(0, 0, 0, 5);
		gbc_txtpnLineline.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtpnLineline.gridx = 0;
		gbc_txtpnLineline.gridy = 0;
		panel.add(txtpnLineline, gbc_txtpnLineline);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.BOTTOM);
		GridBagConstraints gbc_tabbedPane = new GridBagConstraints();
		gbc_tabbedPane.fill = GridBagConstraints.BOTH;
		gbc_tabbedPane.gridx = 1;
		gbc_tabbedPane.gridy = 0;
		panel.add(tabbedPane, gbc_tabbedPane);
		
		// START BOTH
		XYSeriesCollection dataset = new XYSeriesCollection();
		XYSeries arousalSeries = new XYSeries("Arousal");
		XYSeries valenceSeries = new XYSeries("Valence");
		for (int i = 0; i<timestamps.length; i++) {
			arousalSeries.add(timestamps[i], arousals[i]); 
			valenceSeries.add(timestamps[i], valences[i]); 
		}
		dataset.addSeries(arousalSeries);
		dataset.addSeries(valenceSeries);
		JFreeChart chart = ChartFactory.createXYLineChart(
                "Arousal/Valence vs Time", 
                "Time (s)",       
                "Arousal/Valence",        
                dataset,          
                PlotOrientation.VERTICAL,
                true, true, false);

        XYPlot plot = chart.getXYPlot();
		plot.addAnnotation(new XYLineAnnotation(0, arousalA, timestamps.length/2, arousalM*(timestamps.length/2)+arousalA));
		plot.addAnnotation(new XYLineAnnotation(0, valenceA, timestamps.length/2, valenceM*(timestamps.length/2)+valenceA));
        NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
        yAxis.setRange(-1.0, 1.0); // Set Y-axis bounds

		ChartPanel both_panel = new ChartPanel(chart);
		tabbedPane.addTab("Both", null, both_panel, null);
		
		// START AROUSAL
		dataset = new XYSeriesCollection();
		dataset.addSeries(arousalSeries);
		JFreeChart aChart = ChartFactory.createXYLineChart(
                "Arousal vs Time", 
                "Time (s)",       
                "Arousal",        
                dataset,          
                PlotOrientation.VERTICAL,
                true, true, false);

        plot = aChart.getXYPlot();
		plot.addAnnotation(new XYLineAnnotation(0, arousalA, timestamps.length/2, arousalM*(timestamps.length/2)+arousalA));
		yAxis = (NumberAxis) plot.getRangeAxis();
        yAxis.setRange(-1.0, 1.0); // Set Y-axis bounds

        // Enable zoom and cursor (crosshair)
        ChartPanel arousal_panel = new ChartPanel(aChart);
        arousal_panel.setHorizontalAxisTrace(true);
        arousal_panel.setVerticalAxisTrace(true);

		tabbedPane.addTab("Arousal", null, arousal_panel, null);

		// START Valence

		dataset = new XYSeriesCollection();
		dataset.addSeries(valenceSeries);
		JFreeChart vChart = ChartFactory.createXYLineChart(
                "Valence vs Time", 
                "Time (s)",       
                "Valence",        
                dataset,          
                PlotOrientation.VERTICAL,
                true, true, false);

        plot = vChart.getXYPlot();
		plot.addAnnotation(new XYLineAnnotation(0, valenceA, timestamps.length/2, valenceM*(timestamps.length/2)+valenceA));
		yAxis = (NumberAxis) plot.getRangeAxis();
        yAxis.setRange(-1.0, 1.0);
        
        ChartPanel valence_panel = new ChartPanel(vChart);
        arousal_panel.setHorizontalAxisTrace(true);
        arousal_panel.setVerticalAxisTrace(true);
		tabbedPane.addTab("Valence", null, valence_panel, null);
		
		JButton btnNewButton = new JButton("Back");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				singleOutput.setVisible(false); 
				introPanel.setVisible(true); 
			}
		});
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.insets = new Insets(0, 0, 5, 0);
		gbc_btnNewButton.gridx = 0;
		gbc_btnNewButton.gridy = 2;
		out.add(btnNewButton, gbc_btnNewButton);
		out.revalidate(); 
		out.repaint();
	}
	
	public void addMultiOut(JPanel out) {
		out.setVisible(true); 
		out.removeAll();
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] {0, 500, 0};
		gridBagLayout.rowHeights = new int[] {30, 250, 0, 30};
		gridBagLayout.columnWeights = new double[]{1.0};
		gridBagLayout.rowWeights = new double[]{0.0, 1.0, 0.0, 0.0};
		out.setLayout(gridBagLayout);
		
		JButton btnNewButton = new JButton("Back");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				playlistOutput.setVisible(false); 
				introPanel.setVisible(true); 
			}
		});
		
		JLabel lblNewLabel = new JLabel("Results for " + playlist.name());
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 0);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		out.add(lblNewLabel, gbc_lblNewLabel);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		GridBagConstraints gbc_tabbedPane = new GridBagConstraints();
		gbc_tabbedPane.insets = new Insets(0, 0, 5, 0);
		gbc_tabbedPane.fill = GridBagConstraints.BOTH;
		gbc_tabbedPane.gridx = 0;
		gbc_tabbedPane.gridy = 1;
		out.add(tabbedPane, gbc_tabbedPane);
		
		JScrollPane scrollPane = new JScrollPane();
		tabbedPane.addTab("Playlist", null, scrollPane, null);
		
		panel = new JPanel();
		scrollPane.setViewportView(panel);
		panel.setLayout(new GridLayout(0, 1, 0, 0));
		
		graph = new ChartPanel(playlist.asChart()); 
		tabbedPane.addTab("Graph", null, graph, null);

		ArrayQueue<SpotifySong> arrQSS = playlist.asQueue(); 
		songPanels = new SongPanel[playlist.length()]; 
		int i = 0; 
		while (!arrQSS.isEmpty()) {
			SongPanel songPanel;
			try {
				songPanel = new SongPanel(arrQSS.dequeue());
				songPanels[i] = songPanel; 
				panel.add(songPanel);
			} catch (ParseException e1) {
				JOptionPane.showMessageDialog(frame, "A ParseException occured while creating the Playlist Output, some songs may be missing. ", "Error", JOptionPane.ERROR_MESSAGE);
				e1.printStackTrace();
			} catch (SpotifyWebApiException e1) {
				JOptionPane.showMessageDialog(frame, "A SpotifyWebApiException occured while creating the Playlist Output, some songs may be missing. ", "Error", JOptionPane.ERROR_MESSAGE);
				e1.printStackTrace();
			} catch (NoSuchElementException e1) {
				JOptionPane.showMessageDialog(frame, "A NoSuchElementException occured while creating the Playlist Output, some songs may be missing. ", "Error", JOptionPane.ERROR_MESSAGE);
				e1.printStackTrace();
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(frame, "An IOException occured while creating the Playlist Output, some songs may be missing. ", "Error", JOptionPane.ERROR_MESSAGE);
				e1.printStackTrace();
			} catch (PythonError e1) {
				JOptionPane.showMessageDialog(frame, "A PythonError occured while creating the Playlist Output, some songs may be missing. ", "Error", JOptionPane.ERROR_MESSAGE);
				e1.printStackTrace();
			}
			i++; 
		}
		
		JButton sortBtn = new JButton("Sort");
		GridBagConstraints gbc_sortBtn = new GridBagConstraints();
		gbc_sortBtn.insets = new Insets(0, 0, 5, 0);
		gbc_sortBtn.gridx = 0;
		gbc_sortBtn.gridy = 2;
		out.add(sortBtn, gbc_sortBtn);
		sortBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EmotionSort sorterTask = new EmotionSort(frame, playlist); 
				sorterTask.execute();
				playlist = sorterTask.getPlaylist(); 
				addMultiOut(out);
			}
		});
		
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.gridx = 0;
		gbc_btnNewButton.gridy = 3;
		out.add(btnNewButton, gbc_btnNewButton);

		EmotionComputation task = new EmotionComputation(frame, songPanels);
		task.execute();
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	
	private void initialize() {
		frame = new JFrame();
		frame.setTitle("Songsorter");
		frame.setBounds(100, 100, 900, 625);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		if (penv == null) {
			JOptionPane.showMessageDialog(frame, "An error has occured while creating the Python Client. The program will now exit. ", "Fatal Error", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
		
		frame.getContentPane().add(introPanel, BorderLayout.NORTH);
		addIntro(introPanel); 
		
		
		frame.getContentPane().add(singleOutput, BorderLayout.SOUTH);
		singleOutput.setVisible(false);
		
		
		frame.getContentPane().add(playlistOutput, BorderLayout.WEST);
		playlistOutput.setVisible(false);
		
		
	}

	/**
	 * A {@code SwingWorker} class which allows for emotion computation on each song of a playlist and 
	 * updating it without blocking the GUI on the EDT
	 */
	class EmotionComputation extends SwingWorker<Integer, Integer> {
		
		private SongPanel[] songPanels; 
		private JFrame frame; 

		/**
		 * Constructor for the task
		 * @param f JFrame that has the cursor to be updated
		 * @param sp The array of {@code SongPanel}s  which are to be updated
		 */
		EmotionComputation(JFrame f, SongPanel[] sp) {
			frame = f; 
			frame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
			songPanels = sp; 
		}

		@Override
		public Integer doInBackground() {
			ArrayQueue<SongPanel> panelQueue = new ArrayQueue<SongPanel>(songPanels); 
			while (!panelQueue.isEmpty()) {
				SongPanel sp = panelQueue.dequeue(); 
				try {
					sp.updateEmotions(penv);
					sp.repaint();
				} catch (PythonError | IOException | InterruptedException e) {
					JOptionPane.showMessageDialog(frame, "An error occured while computing emotion, some songs's information may be missing. ", "Error", JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
				} 
			} 
			graph.getChart().getXYPlot().setDataset(playlist.asDataset());
			graph.repaint();
			graph.revalidate();
			return 0; 
     	}

		@Override
		public void done() {
			frame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}
 	}

	/**
	 * A {@code SwingWorker} class which allows for emotion computation on one song updating it without 
	 * blocking the GUI on the EDT. within the project however, it is made to block the GUI and is primarily
	 * used to display a loading cursor. 
	 */
	 class SingleEmotionComputation extends SwingWorker<Integer, Integer> {

		private Song song; 
		private JFrame frame; 

		/**
		 * Constructor for the task
		 * @param f JFrame that has the cursor to be updated
		 * @param s The {@code Song} which needs emotion to be computed for
		 */
		SingleEmotionComputation(JFrame f, Song s) {
			frame = f; 
			frame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
			song = s; 
		}

		@Override
		public Integer doInBackground() {
			try {
				song.setEmotions(penv);
			} catch (PythonError | IOException | InterruptedException e) {
				JOptionPane.showMessageDialog(frame, "A error occured while computing emotion, some songs's information may be missing. ", "Error", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			} 
			return 0; 
     	}

		@Override
		public void done() {
			frame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}
 	}

	/**
	 * A {@code SwingWorker} class which allows for the nearest neighbor alogrithm for TSP on the 
	 * {@code SorterPlaylist} to create a better sorted playlist. 
	 */
	class EmotionSort extends SwingWorker<SorterPlaylist, Integer> {
		private SorterPlaylist playlist; 
		private JFrame frame; 

		/**
		 * Constructor for the task
		 * @param f JFrame that has the cursor to be updated
		 * @param p the {@code SorterPlaylist} object
		 */
		EmotionSort(JFrame f, SorterPlaylist p) {
			frame = f; 
			frame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
			playlist = p; 
		}

		public SorterPlaylist getPlaylist() { return playlist; }

		@Override
		public SorterPlaylist doInBackground() {
			playlist.sort(); 
			return playlist;  
     	}
		
		@Override
		 public void done() {
			 frame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		 }
 	}

}