package jar;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AgentModel {
	
    private int userId;
	protected String address ;
	protected int port ;
	protected HashMap<String, String> listOfPseudo ;
    private String pseudo;
    final Scanner scanner = new Scanner(System.in) ;
    private Socket clientSocket ;

    

	public AgentModel(int userId, String address, int port)
	{
		this.address = address ;
		this.port = port ;
		listOfPseudo = new HashMap<String, String>() ;
		pseudo = "";
	}
	
	
	public HashMap<String, String> getAllPseudos(){
		return this.listOfPseudo ;
	}
	
    //Retourne l'ID de l'Utilisateur
    public int getUserId() {
        return this.userId;
    }

    //Retourne le pseudo de l'Utilisateur
    public String getPseudo() {
        return this.pseudo;
    }

    //Modifie le pseudo de l'Utilisateur
    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    //Retourne le numéro de Port de l4utilisateur
    public int getPortNum() {
        return this.port;
    }

    //Retourne l'adresse IP de l'Utilisateur
    public String getIpAddr() {
        return this.address;
    }
    
    public String askForInput() {
		String pseudo = scanner.next();
		return pseudo ;
    }
    
    //return true if all other agent agreed the connection
    public boolean sendBroadCastWithName(UDPClient clientUdp) {
    	ExecutorService es = Executors.newCachedThreadPool();
    	
    	for (int i = 1; i<3;i++) {
			es.execute(new ClientRunnable(address, port-i,clientUdp, port));
		}
		try {
			boolean finished = es.awaitTermination(3, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return clientUdp.isConnected ;
    	
    }
    
    
    public void displayList(HashMap<String, String> list) {
    	
    	for (Map.Entry<String, String> user : list.entrySet()) {
    		System.out.print(user.getKey() + " ");
    		//System.out.print(user.getValue() + "  ");
    	}
    	System.out.println(" ");
    }
    
    public void sendBroadCast(String pseudoChoisi) {
    	
		boolean connected = false ;
		while (!connected) {
			System.out.println("Choisir un nom :");
			this.setPseudo(pseudoChoisi);
			System.out.println("Pseudo choisit :" + this.getPseudo() + " , envoie aux autres users en cours ...");
			UDPClient clientUdp = new UDPClient(this.getPseudo(), this.getAllPseudos());
			connected = this.sendBroadCastWithName(clientUdp);
			if (!connected) {
				System.out.println("Pseudo deja utilise");
			}else {
				this.listOfPseudo = clientUdp.getList();
			}
		}
		
		this.listOfPseudo.put(this.getPseudo(), Integer.toString(this.getPortNum()));


    }
    
    
    public void connectToUser(String pseudoToContact, ChatWindow chat) {
		//System.out.println("Entrez un pseudo pour discuter avec lui");
		//String pseudoToContact = this.askForInput();
		
		if (this.listOfPseudo.containsKey(pseudoToContact)) {
			int portToContact = Integer.parseInt(this.listOfPseudo.get(pseudoToContact));
			
			try {
				clientSocket = new Socket("127.0.0.1",portToContact);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			TCPClient clientTcp = new TCPClient(portToContact, "127.0.0.1", clientSocket, this.getPseudo(), pseudoToContact, chat);
			Thread clTcp = new Thread(clientTcp);
			clTcp.start();
		}
	}
	






    
    
    
    
    
    
	
	

	
	
	

}
