package jar;

import java.util.ArrayList;

import javax.swing.*;
import java.awt.*;

public class MessageFrame implements Runnable{


	// pour la chat frame
	private ArrayList<String> messages;
	private JFrame chatConnection;
	private JScrollPane chatScrollPane;
	private JTextArea chatbox;
	private JButton sendMessage ;
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
		String pathOfProject = System.getProperty("user.dir");
		String path = pathOfProject + "\\" + "chat\\res\\" + "iconSend.jpg" ;
		System.out.println(path);
		Icon icon = new ImageIcon(path);
		sendMessage=new JButton(icon);
		sendMessage.setPreferredSize(new Dimension(33, 20));

		messageBox=new JTextField(30);
		southPanel.add(messageBox);
		southPanel.add(sendMessage);
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
