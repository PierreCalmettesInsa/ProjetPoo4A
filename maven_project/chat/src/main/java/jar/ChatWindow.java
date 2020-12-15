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

	// pour la chat frame
	private ArrayList<String> messages;
	private JFrame chatConnection;
	private JScrollPane chatScrollPane;
	private JTextArea chatbox;
	private JButton sendMessage ;
	private JTextField messageBox ;

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
				for (String pseudo : allPseudos){
					listPseudos.addItem(pseudo);
		
				}
				frameConnection.setVisible(true);
			}
		});


	}

	public void ChatFrame() {
		// Create and set up the window.
		chatConnection = new JFrame("chatWindow");
		chatConnection.getContentPane().setLayout(new BorderLayout());
		chatConnection.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		chatConnection.setLocationRelativeTo(null);

		//ecrire et envoyer un message
		JPanel southPanel=new JPanel();

		//path to maven
		String pathOfProject = System.getProperty("user.dir");
		String path = pathOfProject + "\\" + "chat\\" + "iconSend.jpg" ;
		Icon icon = new ImageIcon(path);
		sendMessage=new JButton(icon);

		messageBox=new JTextField(30);
		southPanel.add(messageBox);
		southPanel.add(sendMessage);

		//Icon icon = new ImageIcon("path/to/iconSend.jpg");
		//sendMessage.setIconImage(icon);
		//(Toolkit.getDefaultToolkit().getImage(getClass().getResource("path/to/iconSend.jpg")));
		//f.setIconImage(ImageIO.read("res/iconSend.jpg"));


		//pour display le texte
		
		chatbox=new JTextArea();
		//chatbox.append("Pierre : coucou\n Thomas : Yo\n");
		chatbox.setLineWrap(true);
		chatbox.setEditable(false);
		chatScrollPane = new JScrollPane(chatbox);
		chatScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		//layout
		chatConnection.add(chatScrollPane,BorderLayout.CENTER);
		chatConnection.add(southPanel,BorderLayout.SOUTH);
		chatConnection.setSize(100,100);
		chatConnection.setVisible(true);
		
		

		

	}
	
	public void launchWindowChat(){
		//javax.swing.SwingUtilities.invokeLater(new Runnable() {
		//	public void run() {
			this.ChatFrame();
		//	}
		//});

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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}


	public JFrame getConnectionFrame() {
		return this.frameConnection;
	}

	public JPanel getPanel() {
		return this.panelChoix;
	}

	public JButton getButtonSend(){
		return this.sendMessage ;
	}

	public JTextField getMessageField(){
		return this.messageBox ;
	}

	public JTextArea getMessageArea(){
		return this.chatbox;
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