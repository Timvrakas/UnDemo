import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class GUI extends JFrame {

	private static final long serialVersionUID = 1L;

	// Define initial size of window
	private static final int WIDTH = 250;
	private static final int HEIGHT = 100;

	// Define GUI Elements
	private JLabel infoLabel;
	private JLabel versionLabel;
	private JLabel responseMessage;
	private JTextField usernameField;
	private JButton unDemoButton;
	private ButtonListener buttonHandler;
	private JComboBox<?> installedVersion;

	public GUI() {

		// Setup window
		setSize(WIDTH, HEIGHT);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle("Minecraft UnDemo v0.2");

		// Setup layout
		Container contentPane = getContentPane();
		contentPane.setLayout(new GridLayout(3, 2));

		// Initialize elements
		infoLabel = new JLabel("Enter Username:", SwingConstants.RIGHT);
		versionLabel = new JLabel("Select Version:", SwingConstants.RIGHT);
		responseMessage = new JLabel("");
		usernameField = new JTextField(10);
		installedVersion = new JComboBox<Object>(Main.getVersions());
		unDemoButton = new JButton("UnDemo!");

		// Setup button handler
		buttonHandler = new ButtonListener();
		unDemoButton.addActionListener(buttonHandler);

		// Add elements to contentPane
		contentPane.add(infoLabel);
		contentPane.add(usernameField);
		contentPane.add(versionLabel);
		contentPane.add(installedVersion);
		contentPane.add(responseMessage);
		contentPane.add(unDemoButton);

		// Activate GUI
		setVisible(true);
	}

	private class ButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// Retrieve data from form
			String username = usernameField.getText();
			String version = (String) installedVersion.getSelectedItem();
			// Check data for validity
			if (Main.checkInput(version, username)) {
				// Try to patch and copy jar
				try {
					Main.patchJSON(version, username);
				} catch (IOException e1) {
					e1.printStackTrace();
					responseMessage.setText("Error IOException");
				}
			} else {
				responseMessage.setText("Error Invalid");
			}
			responseMessage.setText("Done!");

		}
	}
}
