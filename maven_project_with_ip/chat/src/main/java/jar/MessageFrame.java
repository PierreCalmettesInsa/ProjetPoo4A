package jar;


import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class MessageFrame implements Runnable{


	// pour la chat frame
	private JFrame chatConnection;
	private JScrollPane chatScrollPane;
	private JTextArea chatbox;
	private JButton sendMessage ;
	private JButton sendFile ;
	private JTextField messageBox ;


	public void run() {
		// Create and set up the window.
		chatConnection = new JFrame("chatWindow");
		chatConnection.getContentPane().setLayout(new BorderLayout());
		chatConnection.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		chatConnection.setLocationRelativeTo(null);

		//ecrire et envoyer un message
		JPanel southPanel=new JPanel();

		//path to maven
		final URL url = Thread.currentThread().getContextClassLoader().getResource("iconSend.jpg");
        Image img = Toolkit.getDefaultToolkit().getImage(url);
		
		Icon icon = new ImageIcon(img);
		sendMessage=new JButton(icon);
		sendMessage.setPreferredSize(new Dimension(33, 20));
		sendFile = new JButton("send file") ;

		messageBox=new JTextField(30);
		southPanel.add(messageBox);
		southPanel.add(sendMessage);
		southPanel.add(sendFile) ;
		southPanel.setBorder(BorderFactory.createEmptyBorder(25, // top
		25, // left
		10, // bottom
		25) // right
		);


		//pour display le texte
		chatbox=new JTextArea();
		chatbox.setLineWrap(true);
		chatbox.setEditable(false);
		chatScrollPane = new JScrollPane(chatbox);
		chatScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		//layout
		chatConnection.add(chatScrollPane,BorderLayout.CENTER);
		chatConnection.add(southPanel,BorderLayout.SOUTH);
		chatConnection.setSize(500,500);
		chatConnection.setVisible(true);
		
		

		

	}

	public JButton getButtonSend(){
		return this.sendMessage ;
	}

	public JButton getButtonFile(){
		return this.sendFile ;
	}


	public JTextField getMessageField(){
		return this.messageBox ;
	}

	public JTextArea getMessageArea(){
		return this.chatbox;
	}

	public JFrame getFrame(){
		return this.chatConnection ;
	}


    
}
