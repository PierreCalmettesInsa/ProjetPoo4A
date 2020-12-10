package jar;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TCPServer implements Runnable{
	
	protected int port ;
	protected ChatWindow chat ;
	
	public TCPServer(int port, ChatWindow chat){
		this.port = port ;
		this.chat = chat ;
	}
	
	@Override
	public void run() {
		try {
			ServerSocket socket = new ServerSocket(port);
			
			while(true) {
				Socket link = socket.accept();
				new Thread(new AcceptedConnection(link, chat)).start();
			}
			
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}



class AcceptedConnection implements Runnable {
	final Socket link;
	protected BufferedReader in ;
	protected PrintWriter out ;
	protected ChatWindow chat ;
	final Scanner scan = new Scanner(System.in);
	
	public AcceptedConnection(Socket link, ChatWindow chat){
		this.link=link;
		this.chat = chat ;
		
		try {
			//create inputs and outputs
			this.in = new BufferedReader(new InputStreamReader(link.getInputStream()));
			this.out = new PrintWriter(link.getOutputStream());

			//creat a new frame for the chat
			chat.launchWindowChat();

			//Add a listener to the button to send a message
			initListener() ;


		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void initListener(){
		chat.getButtonSend().addActionListener(e -> send());
	}

	public void send(){
		String msgToSend = chat.getMessageField().getText();
		chat.getMessageArea().append("AddPseudoInTcpServer : " + msgToSend + "\n");
		out.println(msgToSend);
		out.flush();
	}
	
	@Override
	public void run() {
		
		out.println("Mettre pseudo");
		out.flush();

		String firstReceived;
		try {
			firstReceived = in.readLine();
			chat.getMessageArea().append("Vous êtes en discussion avec : " + firstReceived + "\n");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}


			
			Thread receive = new Thread(new Runnable() {
				String received = "" ;
				
				public void run() {
					try {
						
						while (received != null) {
							received = in.readLine();
							chat.getMessageArea().append("ClientAddPseudoInTcpServer : " + received + "\n");
						}
						
						//Client disconnected
						System.out.println("Client deconnecte");
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
