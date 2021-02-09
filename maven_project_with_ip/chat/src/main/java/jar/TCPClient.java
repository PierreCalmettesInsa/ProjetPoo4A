package jar;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JFileChooser;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class TCPClient implements Runnable {

	protected String addressDist;
	protected String myAddress;
	final Scanner scan = new Scanner(System.in);
	private Socket s;
	private String myPseudo;
	private String oPseudo;
	protected ChatWindow chat;
	protected PrintWriter out;
	protected BufferedReader in;
	protected MessageFrame msgFrame;
	Thread receive;

	private ReentrantLock mutex = new ReentrantLock();

	public TCPClient(String myAddress, String addressDist, Socket s, String myPseudo, String oPseudo, ChatWindow chat) {
		this.myAddress = myAddress;
		this.addressDist = addressDist;
		this.s = s;
		this.myPseudo = myPseudo;
		this.oPseudo = oPseudo;
		this.chat = chat;

		try {
			out = new PrintWriter(s.getOutputStream());
			in = new BufferedReader(new InputStreamReader(s.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}

		// creat a new frame for the chat
		msgFrame = chat.launchWindowChat();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Look for history, to be replace by ip address
		ArrayList<String> allMessagesHisto = DatabaseChat.getHistory(myAddress, addressDist);

		for (String msg : allMessagesHisto) {
			msgFrame.getMessageArea().append(msg + "\n");
		}

		// Add a listener to the button to send a message
		initListener();
	}

	public void initListener() {
		msgFrame.getButtonSend().addActionListener(e -> send());
		msgFrame.getButtonFile().addActionListener(e -> sendFile());
		msgFrame.getFrame().addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.out.println("Closed");
				out.close();
				try {
					s.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
				e.getWindow().dispose();
			}
		});
	}

	public void send() {
		String msgToSend = msgFrame.getMessageField().getText();
		msgFrame.getMessageArea().append(myPseudo + " : " + msgToSend + "\n");
		out.println(msgToSend);
		out.flush();

		DatabaseChat.addToHistory(this.myAddress, this.addressDist, (myPseudo + " : " + msgToSend));
	}

	public void sendFile() {

		mutex.lock();

		String msgToSend = "---Sending file--- code : 12976#";
		out.println(msgToSend);
		out.flush();

		JFileChooser fChooser = new JFileChooser();
		fChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		int fileChosen = fChooser.showOpenDialog(msgFrame.getFrame());

		if (fileChosen == JFileChooser.APPROVE_OPTION) {
			File file = fChooser.getSelectedFile();

			System.out.println("sending...");
			String received;

			try {
				received = in.readLine();
				System.out.println(received);

			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			msgToSend = file.getName();
			System.out.println(msgToSend);
			out.println(msgToSend);
			out.flush();

			try {
				received = in.readLine();
				System.out.println(received);

			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}


			out.println(file.length());
			out.flush();

			try {
				received = in.readLine();
				System.out.println(received);

			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			byte[] buffer = new byte[(int) file.length()];
			try {
				FileInputStream fInput = new FileInputStream(file);
				BufferedInputStream bInput = new BufferedInputStream(fInput);
				bInput.read(buffer, 0, buffer.length);
				OutputStream os = s.getOutputStream();
				os.write(buffer, 0, buffer.length);
				os.flush();
				bInput.close();
				// os.close();

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		mutex.unlock();

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

			receive = new Thread(new Runnable() {
				String received = "";

				public void run() {
					try {
						while (received != null) {

							while (mutex.isLocked()) {
								try {
									Thread.sleep(500);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}

							received = in.readLine();

							if (received != null && received.startsWith("---Sending file--- code : 12976#")) {
								System.out.println("Receiving file...");

								out.println("Envoie de fichier");
								out.flush();

								
								out.println("Ok suis prêt");
								out.flush();

								String fileName = in.readLine();
								System.out.println(fileName);

								out.println("size");
								out.flush();

								received = in.readLine();
								System.out.println(received);
								int size = Integer.parseInt(received);

								out.println("ready");
								out.flush();

								byte [] mybytearray  = new byte [size];

								InputStream is = s.getInputStream();
								FileOutputStream fOutput = new FileOutputStream(fileName);
								BufferedOutputStream bOutput = new BufferedOutputStream(fOutput);
								int bytesRead = is.read(mybytearray, 0, mybytearray.length);

								bOutput.write(mybytearray, 0, bytesRead);
								bOutput.flush();
								System.out.println("File " + received + " downloaded (" + bytesRead + " bytes read)");
								bOutput.close();

							} else {
								if (received != ""){
									msgFrame.getMessageArea().append(oPseudo + " : " + received + "\n");
									DatabaseChat.addToHistory(myAddress, addressDist, (oPseudo + " : " + received));
								}
							}

			

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
