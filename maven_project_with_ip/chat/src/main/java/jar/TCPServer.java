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
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JFileChooser;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class TCPServer implements Runnable {

	protected int port;
	protected ChatWindow chat;
	protected String myAddress;
	protected HashMap<String, String> allPseudos;

	public TCPServer(String myAddress, int port, ChatWindow chat, HashMap<String, String> allPseudos) {
		this.myAddress = myAddress;
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
	protected String myAddress;
	protected BufferedReader in;
	protected PrintWriter out;
	protected ChatWindow chat;
	protected String myPseudo;
	protected String oPseudo;
	protected HashMap<String, String> allPseudos;
	protected MessageFrame msgFrame;
	Thread receive;

	private ReentrantLock mutex = new ReentrantLock();

	public AcceptedConnection(String myAddress, Socket link, ChatWindow chat, String myPseudo,
			HashMap<String, String> allPseudos) {
		this.myAddress = myAddress;
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

			// Add a listener to the button to send a message
			initListener();

		} catch (IOException e) {
			e.printStackTrace();
		}
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
					link.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
				e.getWindow().dispose();
			}
		});
	}

	public void send() {
		String msgToSend = msgFrame.getMessageField().getText();
		if (msgToSend != ""){
			SimpleDateFormat h = new SimpleDateFormat ("hh:mm");
			Date date = new Date();
			String time = h.format(date);
			msgFrame.getMessageArea().append(myPseudo + "at " + time  + " : " + msgToSend + "\n");
			msgFrame.getMessageField().setText("");
			out.println(msgToSend);
			out.flush();

			DatabaseChat.addToHistory(myAddress, link.getInetAddress().getHostAddress(), (myPseudo + "at " + time  + " : " + msgToSend));
		}
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

			String received;
			try {
				received = in.readLine();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			msgToSend = file.getName();

			out.println(msgToSend);
			out.flush();

			try {
				received = in.readLine();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			out.println(file.length());
			out.flush();

			try {
				received = in.readLine();
			} catch (IOException e1) {
				e1.printStackTrace();
			}


			byte[] buffer = new byte[(int) file.length()];
			try {
				FileInputStream fInput = new FileInputStream(file);
				BufferedInputStream bInput = new BufferedInputStream(fInput);
				bInput.read(buffer, 0, buffer.length);
				OutputStream os = link.getOutputStream();
				os.write(buffer,0,buffer.length);
				os.flush();
				bInput.close();
			
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}catch (IOException e) {
				e.printStackTrace();
			}

		}


		mutex.unlock();


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


			
			receive = new Thread(new Runnable() {
				String received = "" ;
				
				public void run() {
					try {
						
						while (received != null) {

							while (mutex.isLocked()) {
								try {
									Thread.sleep(500);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}

							received = in.readLine();
							//received.trim();

							if (received != null && received.startsWith("---Sending file--- code : 12976#")){
								System.out.println("Receiving file...");

								msgFrame.getButtonSend().setEnabled(false);


								out.println("Envoi de fichier");
								out.flush();

								out.println("Ok suis prêt");
								out.flush();

								String fileName = in.readLine();

								out.println("size");
								out.flush();

								received = in.readLine();
								int size = Integer.parseInt(received);

								out.println("ready");
								out.flush();

								byte [] mybytearray  = new byte [size];

								InputStream is = link.getInputStream();
								FileOutputStream fOutput = new FileOutputStream(fileName);
								BufferedOutputStream bOutput = new BufferedOutputStream(fOutput);
								int bytesRead = is.read(mybytearray,0,mybytearray.length);

								bOutput.write(mybytearray, 0 , bytesRead);
								bOutput.flush();
								System.out.println("File " + received+ " downloaded (" + bytesRead + " bytes read)");
								bOutput.close();

								msgFrame.getButtonSend().setEnabled(true);


							} else {
								if (received != ""){
								SimpleDateFormat h = new SimpleDateFormat ("hh:mm");
								Date date = new Date();
								String time = h.format(date);
								msgFrame.getMessageArea().append(oPseudo + " at " + time  +  " : " + received + "\n");
								DatabaseChat.addToHistory(myAddress,link.getInetAddress().getHostAddress(), (oPseudo + " at " + time  +  " : " + received));
								}
							}




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
