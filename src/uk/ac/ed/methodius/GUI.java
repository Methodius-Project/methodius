package uk.ac.ed.methodius;

/**
 * Swing interface for Methodius.
 * Very simple, four components: text box to enter id in, describe button
 * to press to get description, img area to display the exhibit image and
 * a text area to put the description.
 * Gets the image from a specified directory. Assumed to be in a file called
 * the exhibit id + ".jpg" e.g. exhibit23.jpg or whatever. Gets the description
 * from the publisher object and displays both.
 *
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


@SuppressWarnings("unchecked")

public class GUI implements ActionListener {

	/* the window components */
	private JFrame mainFrame;
	private JPanel mainPanel;
	private JTextField exhibitIdField;
	private JTextArea exhibitDesc;
	private JButton describeButton;
	private JLabel imgLabel;  // the exhibit image

	private Log log = null;
	private Publisher publisher;
	private Configuration config;
	private static String configFile;
	private DataStoreRead dataStore;
	private String imgDir;

	private static String filesep = "/";

	/**
	 * GUI object creates the interface and the underlying Methodius Publisher.
	 */
	public GUI() {

		/* first set up Methodius */

		log = new Log(System.out);
		Util.setLog(log);
		log.setNoOutput();
		try {
			log.output("Using configuration: " + configFile );
			config = new Configuration(configFile, true);
			config.setLog(log);

			/* for GUI we always want read only */
			dataStore = new DataStoreRead(config, true);
			dataStore.init();

			SignificanceHandler sh = new SignificanceHandler(dataStore, config);
			config.setSignificanceHandler(sh);
			PredicateHandler ph = new PredicateHandler(dataStore, config);
            config.setPredicateHandler(ph);
			GenericFactHandler gfh = new GenericFactHandler(dataStore,config);
			config.setGenericFactHandler(gfh);
			AdverbHandler ah = new AdverbHandler(config);
			config.setAdverbHandler(ah);
			TypeHandler th = new TypeHandler(dataStore, config, log);
            config.setTypeHandler(th);
            
			log.output("Create user model");
			UserModel um = dataStore.getUserModel("adult");
			config.setUserModel(um);
			um.setSearchWidth(config.getMaxCPDepth());
			int nFacts = um.getFactsPerPage();
			um.setTargetSize(nFacts);

			imgDir = config.getImagesDir();

			MethodiusRealizer mr = new MethodiusRealizer(config);
			config.setMethodiusRealizer(mr);

			publisher = new Publisher(dataStore, config);
		} catch(Exception e) {
			System.out.println("Exception thrown\n" + e);
			e.printStackTrace(log.getPrintStream());
		}

		//Create and set up the window.
		mainFrame = new JFrame("Methodius");
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setSize(new Dimension(1200, 500));

		//Create and set up the panel.
		mainPanel = new JPanel(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		//Add the widgets.
		addWidgets(mainFrame.getContentPane());

		//Set the default button.
		mainFrame.getRootPane().setDefaultButton(describeButton);

		//Display the window.
		mainFrame.pack();
		mainFrame.setVisible(true);
	}

	public void finalize() throws Throwable {
		if( dataStore != null) {
			dataStore.close();
		}
		super.finalize();
	}

	/**
	 * Create and add the widgets. These are a labelled text field
	 * for entering the exhibit id with a button to it's right to
	 * press to retrieve the description. Then, vertically stacked
	 * underneath, an image area for the exhibit image and a text
	 * area for the description generated.
	 */
	private void addWidgets(Container pane) {
		pane.setLayout(new BoxLayout(pane, BoxLayout.PAGE_AXIS));

		/*
		 * Creates two panes. One for the top, input area and
		 * one for the bottom, output area. The top pane is
		 * stacked horizontally, the bottom one vertically.
		 */

		/* topPane is the label, textfield and button*/
		JPanel topPane = new JPanel();
		topPane.setLayout(new BoxLayout(topPane, BoxLayout.LINE_AXIS));

		JLabel efLabel = new JLabel("Enter object name   ");
		topPane.add(efLabel);

		exhibitIdField = new JTextField(20);
		exhibitIdField.setAlignmentX(Component.LEFT_ALIGNMENT);
		exhibitIdField.setMinimumSize(new Dimension(100,20));
		exhibitIdField.setMaximumSize(new Dimension(100,20));
		topPane.add(exhibitIdField);

		describeButton = new JButton("Describe");
		describeButton.setAlignmentX(Component.LEFT_ALIGNMENT);
		describeButton.addActionListener(this);
		topPane.add(describeButton);

		/*
		 * exhibitPane is the bottom pane containing the exhibit
		 * image and the exhibit description.
		 */
		JPanel exhibitPane = new JPanel();
		exhibitPane.setLayout(new BoxLayout(exhibitPane, BoxLayout.PAGE_AXIS));
		ImageIcon img = new ImageIcon(imgDir + filesep + "methodius.jpg");
		imgLabel = new JLabel("", img, JLabel.CENTER);
		imgLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		exhibitPane.add(imgLabel);

		exhibitDesc = new JTextArea(10,30);
		exhibitDesc.setLineWrap(true);
		exhibitDesc.setWrapStyleWord(true);
		exhibitPane.add(exhibitDesc);

		/* add the two subpanes to the main pane */
		pane.add(topPane, BorderLayout.CENTER);
		pane.add(exhibitPane, BorderLayout.PAGE_END);
	}


	/* what to do when the button gets pressed */
	public void actionPerformed(ActionEvent event) {
		Exception exc = null;
		String desc = null;

		/* get the exhibitId entered */

		String exhibitId = exhibitIdField.getText();

		/* find the appropriate image */

		String imgFile = imgDir + filesep + exhibitId + ".jpg";
		imgLabel.setIcon(new ImageIcon(imgFile));

		/* get the description and display it */
		String[] sentences = null;
		try {
			sentences = publisher.describe(exhibitId);
		} catch (Exception e) {
			e.printStackTrace();
			exc = e;
		}

		if(sentences == null) {
			desc = "Failed to generate a description because: " + exc;
		} else {
			for(int i = 0; i < sentences.length && sentences[i] != null; i++) {
				log.output(sentences[i]);
				if(desc == null) {
					desc = sentences[i];
				} else {
					desc = desc + " " + sentences[i];
				}
			}
		}
		exhibitDesc.setText(desc);
	}

	/**
	 * Create the GUI and show it.  For thread safety,
	 * this method should be invoked from the
	 * event-dispatching thread.
	 */
	private static void createAndShowGUI() {
		GUI converter = new GUI();
	}

	public static void main(String[] args) {
		filesep = System.getProperty("file.separator");
		configFile = args[0];

		//Schedule a job for the event-dispatching thread:
		//creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}
}

