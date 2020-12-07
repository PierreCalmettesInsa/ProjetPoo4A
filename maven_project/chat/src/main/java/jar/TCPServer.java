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
	
	public TCPServer(int port){
		this.port = port ;
	}
	
	@Override
	public void run() {
		try {
			ServerSocket socket = new ServerSocket(port);
			
			while(true) {
				Socket link = socket.accept();
				new Thread(new AcceptedConnection(link)).start();
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
	final Scanner scan = new Scanner(System.in);
	
	public AcceptedConnection(Socket link){
		this.link=link;
		
		try {
			this.in = new BufferedReader(new InputStreamReader(link.getInputStream()));
			this.out = new PrintWriter(link.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		
		System.out.println("Ouverture d'un chat avec un autre utilisateur");
			//Creation of two thread, one to send and one to receive
			
			Thread send = new Thread(new Runnable() {
				String toSend ;
				
				public void run() {
					while(true) {
						System.out.print("Client :");
						toSend = scan.nextLine();
						out.println(toSend);
						out.flush();
					}
				}
			});
			send.start();
			
			Thread receive = new Thread(new Runnable() {
				String received ;
				
				public void run() {
					try {
						received = in.readLine();
						
						while (received != null) {
							System.out.println("Client: " + received);
							received = in.readLine();
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
