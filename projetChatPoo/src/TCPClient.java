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
	
	public TCPClient(int port, String address) {
		this.port = port ;
		this.address = address;
	}
	
	public void run() {
		try {
            Socket s = new Socket(address, port);
            PrintWriter out = new PrintWriter(s.getOutputStream());
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            
            
            Thread send = new Thread(new Runnable() {
            	String toSend;
            	
            	public void run() {
            		while(true) {
            			toSend = scan.nextLine();
            			out.println(toSend);
            			out.flush();
            		}
            	}
            });
            send.start();
            
            Thread receive = new Thread(new Runnable () {
            	String received ;
            	
            	public void run() {
            		try {
            			received = in.readLine();
            			
            			while(received != null) {
            				System.out.println("Serveur : "+ received);
            				received = in.readLine();
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
