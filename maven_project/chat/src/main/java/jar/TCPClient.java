package jar;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
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

	
	public TCPClient(int port, String address, Socket s, String myPseudo, String oPseudo, ChatWindow chat) {
		this.port = port ;
		this.address = address;
		this.s = s;
		this.myPseudo = myPseudo ;
		this.oPseudo = oPseudo;
		this.chat = chat ;

		try {
			out = new PrintWriter(s.getOutputStream());
			in = new BufferedReader(new InputStreamReader(s.getInputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//creat a new frame for the chat
		chat.launchWindowChat();

		//Add a listener to the button to send a message
		initListener() ;
	}

	public void initListener(){
		chat.getButtonSend().addActionListener(e -> send());
	}

	public void send(){
		String msgToSend = chat.getMessageField().getText();
		chat.getMessageArea().append(myPseudo + " : " + msgToSend + "\n");
		out.println(msgToSend);
		out.flush();
	}
	
	public void run() {
		try {

			String firstReceived;
			try {
				firstReceived = in.readLine();
				chat.getMessageArea().append("Vous êtes en discussion avec : " + firstReceived + "\n");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			out.println("Mettre pseudo");
			out.flush();
	
	

            
            Thread receive = new Thread(new Runnable () {
            	String received = "";
            	
            	public void run() {
            		try {            			
            			while(received != null) {
							received = in.readLine();
							chat.getMessageArea().append(oPseudo + " : " + received + "\n");
            			}
            			
            			//Server disconnected
            			System.out.println("Serveur deconnecte");
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
