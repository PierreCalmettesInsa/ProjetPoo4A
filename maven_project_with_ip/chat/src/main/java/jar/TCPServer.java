package jar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Map.Entry;
import java.awt.event.WindowAdapter ;
import java.awt.event.WindowEvent ;


public class TCPServer implements Runnable {

	protected int port;
	protected ChatWindow chat;
	protected String myAddress ;
	protected HashMap<String, String> allPseudos;

	public TCPServer(String myAddress, int port, ChatWindow chat, HashMap<String, String> allPseudos) {
		this.myAddress = myAddress ;
		this.port = port;
		this.chat = chat;
		this.allPseudos = allPseudos;
	}

	@Override
	public void run() {
		try {
			ServerSocket socket = new ServerSocket(port);

			while (true) {
				Socket link = socket.accept();
				String myPseudo = getPseudoInList(allPseudos, myAddress);
				System.out.println(myAddress);
				new Thread(new AcceptedConnection(myAddress, link, chat, myPseudo, allPseudos)).start();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getPseudoInList(HashMap<String, String> all, String address) {
		for (Entry<String, String> entry : all.entrySet()) {
			if (address.equals(entry.getValue())) {
				return entry.getKey();
			}
		}
		return null;
	}

}

class AcceptedConnection implements Runnable {
	final Socket link;
	protected String myAddress ;
	protected BufferedReader in;
	protected PrintWriter out;
	protected ChatWindow chat;
	protected String myPseudo;
	protected String oPseudo;
	protected HashMap<String, String> allPseudos;
	protected MessageFrame msgFrame;

	public AcceptedConnection(String myAddress, Socket link, ChatWindow chat, String myPseudo, HashMap<String, String> allPseudos) {
		this.myAddress = myAddress ;
		this.link = link;
		this.chat = chat;
		this.myPseudo = myPseudo;
		this.allPseudos = allPseudos;

		try {
			// create inputs and outputs
			this.in = new BufferedReader(new InputStreamReader(link.getInputStream()));
			this.out = new PrintWriter(link.getOutputStream());

			// creat a new frame for the chat
			msgFrame = chat.launchWindowChat();
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}


			//Add a listener to the button to send a message
			initListener() ;


		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void initListener(){
		msgFrame.getButtonSend().addActionListener(e -> send());
		msgFrame.getFrame().addWindowListener(new WindowAdapter()
			{
				@Override
				public void windowClosing(WindowEvent e)
				{
					System.out.println("Closed");
					out.close();
					try {
						link.close();
					} catch (IOException ex) {
						ex.printStackTrace();
					}
					e.getWindow().dispose();
				}
			});
	}

	public void send(){
		String msgToSend = msgFrame.getMessageField().getText();
		msgFrame.getMessageArea().append(myPseudo + " : " + msgToSend + "\n");
		out.println(msgToSend);
		out.flush();

		DatabaseChat.addToHistory(myAddress,link.getInetAddress().getHostAddress(), (myPseudo + " : " + msgToSend));
	}


	
	@Override
	public void run() {
		oPseudo = "" ;
		out.println(myPseudo);
		out.flush();

		String firstReceived;
		try {
			firstReceived = in.readLine();
			oPseudo = firstReceived ;

	

			//Look for history, to be replace by ip address
			ArrayList<String> allMessagesHisto = DatabaseChat.getHistory(myAddress, link.getInetAddress().getHostAddress());

			for (String msg : allMessagesHisto){
				msgFrame.getMessageArea().append(msg + "\n");
			}


			msgFrame.getMessageArea().append("Vous êtes en discussion avec : " + firstReceived + "\n");
		} catch (IOException e1) {
			e1.printStackTrace();
		}


			
			Thread receive = new Thread(new Runnable() {
				String received = "" ;
				
				public void run() {
					try {
						
						while (received != null) {
							received = in.readLine();
							msgFrame.getMessageArea().append(oPseudo + " : " + received + "\n");
							DatabaseChat.addToHistory(myAddress,link.getInetAddress().getHostAddress(), (oPseudo + " : " + received ));

						}
						
						//Client disconnected
						msgFrame.getMessageArea().append("L'utiisateur s'est deconnecte"  + "\n");
						out.close();
						link.close();

					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
			receive.start();

		}
	
}
