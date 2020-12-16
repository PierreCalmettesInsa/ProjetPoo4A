package jar;

/*
 * SwingApplication.java is a 1.4 example that requires
 * no other files.
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.File;


public class ChatWindow {

	// Specify the look and feel to use. Valid values:
	// null (use the default), "Metal", "System", "Motif", "GTK+"
	final static String LOOKANDFEEL = "System";

	// pour connection frame
	private JFrame frameConnection;
	private JButton buttonConnection;
	private JTextField fieldPseudo;

	private JComboBox<String> listPseudos;
	private JButton choosePseudo;
	private JPanel panelChoix;



	private static void initLookAndFeel() {

		// Swing allows you to specify which look and feel your program uses--Java,
		// GTK+, Windows, and so on as shown below.
		String lookAndFeel = null;

		if (LOOKANDFEEL != null) {
			if (LOOKANDFEEL.equals("Metal")) {
				lookAndFeel = UIManager.getCrossPlatformLookAndFeelClassName();
			} else if (LOOKANDFEEL.equals("System")) {
				lookAndFeel = UIManager.getSystemLookAndFeelClassName();
			} else if (LOOKANDFEEL.equals("Motif")) {
				lookAndFeel = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
			} else if (LOOKANDFEEL.equals("GTK+")) { // new in 1.4.2
				lookAndFeel = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";

			} else {
				System.err.println("Unexpected value of LOOKANDFEEL specified: " + LOOKANDFEEL);
				lookAndFeel = UIManager.getCrossPlatformLookAndFeelClassName();
			}

			try {
				UIManager.setLookAndFeel(lookAndFeel);
			} catch (ClassNotFoundException e) {
				System.err.println("Couldnt find class for specified look and feel:" + lookAndFeel);
				System.err.println("Did you include the L&F library in the class path?");
				System.err.println("Using the default look and feel.");
			} catch (UnsupportedLookAndFeelException e) {
				System.err.println("Can't use the specified look and feel (" + lookAndFeel + ") on this platform.");
				System.err.println("Using the default look and feel.");
			} catch (Exception e) {
				System.err.println("Couldn't get specified look and feel (" + lookAndFeel + "), for some reason.");
				System.err.println("Using the default look and feel.");
				e.printStackTrace();
			}
		}
	}

	public void connectionFrame() {

		// Set the look and feel.
		initLookAndFeel();

		// Make sure we have nice window decorations.
		JFrame.setDefaultLookAndFeelDecorated(true);

		// Create and set up the window.
		frameConnection = new JFrame("connectionWindow");
		frameConnection.getContentPane().setLayout(new BorderLayout());
		frameConnection.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frameConnection.setLocationRelativeTo(null);

		fieldPseudo = new JTextField();

		buttonConnection = new JButton("Se connecter");
		buttonConnection.setMnemonic(KeyEvent.VK_I);

		/*
		 * An easy way to put space between a top-level container and its contents is to
		 * put the contents in a JPanel that has an "empty" border.
		 */
		JPanel pane = new JPanel(new GridLayout(0, 1));
		pane.add(fieldPseudo);
		pane.add(buttonConnection);
		pane.setBorder(BorderFactory.createEmptyBorder(50, // top
				50, // left
				20, // bottom
				50) // right
		);

		frameConnection.getContentPane().add(pane, BorderLayout.CENTER);

		listPseudos = new JComboBox<String>();

		choosePseudo = new JButton("Discuter avec");
		choosePseudo.setMnemonic(KeyEvent.VK_I);

		panelChoix = new JPanel(new GridLayout(0, 1));
		panelChoix.add(listPseudos);
		panelChoix.add(choosePseudo);
		panelChoix.setBorder(BorderFactory.createEmptyBorder(50, // top
				50, // left
				20, // bottom
				50) // right
		);

		frameConnection.getContentPane().add(panelChoix, BorderLayout.SOUTH);

		frameConnection.pack();
		frameConnection.setVisible(true);
	}



	public void updateConnectionFrame(ArrayList<String> allPseudos){
		
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run(){
				frameConnection.setVisible(false);
				listPseudos.removeAllItems();
				for (String pseudo : allPseudos){
					listPseudos.addItem(pseudo);
		
				}
				frameConnection.setVisible(true);
			}
		});


	}

	
	
	public MessageFrame launchWindowChat(){

		MessageFrame msgFrame = new MessageFrame();
		new Thread(msgFrame).start();

		return msgFrame ;
	}



	public void launchWindowConnection() {
		// Schedule a job for the event-dispatching thread:
		// creating and showing this applications GUI.

		try {
			javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					connectionFrame();
				}
			});
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}


	}


	public JFrame getConnectionFrame() {
		return this.frameConnection;
	}

	public JPanel getPanel() {
		return this.panelChoix;
	}




	public JComboBox<String> getListPseudos() {
		return this.listPseudos;
	}

	public JButton getButtonChoose() {
		return this.choosePseudo;
	}

	
	public void setListPseudos(JComboBox<String> listPseudos){
		this.listPseudos = listPseudos ;
	}

	
	public void setButtonChoose(JButton choosePseudo){
		this.choosePseudo = choosePseudo ;
	}


	public void setConnectionFrame(JFrame frame){
		this.frameConnection = frame ;
	}

	public JTextField getPseudoTextField(){
		return this.fieldPseudo ;
	}

	public JButton getConnectionButton(){
		return this.buttonConnection ;
	}

}