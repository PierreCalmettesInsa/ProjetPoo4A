package jar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class TCPClient implements Runnable {
	
	protected int port;
	protected String address ;
	final Scanner scan = new Scanner(System.in);
	private Socket s;
	private String myPseudo ;
	private String oPseudo ;
	protected ChatWindow chat ;
	protected PrintWriter out ;
	protected BufferedReader in ;
	protected int myPort ;
	protected MessageFrame msgFrame ;

	
	public TCPClient(int myPort, int port, String address, Socket s, String myPseudo, String oPseudo, ChatWindow chat) {
		this.port = port ;
		this.address = address;
		this.s = s;
		this.myPseudo = myPseudo ;
		this.oPseudo = oPseudo;
		this.chat = chat ;
		this.myPort = myPort ;

		try {
			out = new PrintWriter(s.getOutputStream());
			in = new BufferedReader(new InputStreamReader(s.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}

		//creat a new frame for the chat
		msgFrame = chat.launchWindowChat();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		//Look for history, to be replace by ip address
		ArrayList<String> allMessagesHisto = DatabaseChat.getHistory(myPort, port);

		for (String msg : allMessagesHisto){
			msgFrame.getMessageArea().append(msg + "\n");
		}




		//Add a listener to the button to send a message
		initListener() ;
	}

	public void initListener(){
		msgFrame.getButtonSend().addActionListener(e -> send());
	}

	public void send(){
		String msgToSend = msgFrame.getMessageField().getText();
		msgFrame.getMessageArea().append(myPseudo + " : " + msgToSend + "\n");
		out.println(msgToSend);
		out.flush();

		DatabaseChat.addToHistory(this.myPort, port,(myPseudo + " : " + msgToSend));
	}
	
	public void run() {
		try {

			String firstReceived;
			try {
				firstReceived = in.readLine();
				msgFrame.getMessageArea().append("Vous êtes en discussion avec : " + firstReceived + "\n");
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			out.println(myPseudo);
			out.flush();
	
	

            
            Thread receive = new Thread(new Runnable () {
            	String received = "";
            	
            	public void run() {
            		try {            			
            			while(received != null) {
							received = in.readLine();
							msgFrame.getMessageArea().append(oPseudo + " : " + received + "\n");
							DatabaseChat.addToHistory(myPort, port, (oPseudo + " : " + received) );

            			}
            			
						//Server disconnected
						msgFrame.getMessageArea().append("L'utiisateur s'est deconnecte"  + "\n");
            			out.close();
            			s.close();
            		} catch (IOException e) {
            			e.printStackTrace();
            		}
            	}
            });
            receive.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	
	

}