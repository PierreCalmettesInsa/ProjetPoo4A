package jar;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AgentModel {
	
    private int userId;
	protected String address ;
	protected HashMap<String, String> listOfPseudo ;
    private String pseudo;
    final Scanner scanner = new Scanner(System.in) ;
    private Socket clientSocket ;

    

	public AgentModel(int userId, String address)
	{
		this.address = address ;
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

    //Retourne l'adresse IP de l'Utilisateur
    public String getIpAddr() {
        return this.address;
    }
    
    public String askForInput() {
		String pseudo = scanner.next();
		return pseudo ;
	}
	




	public InetAddress getBroadcast(){
		Enumeration<NetworkInterface> interfaces;
		InetAddress broadcast = null ;
		try {
			interfaces = NetworkInterface.getNetworkInterfaces();

			while (interfaces.hasMoreElements()) 
			{
				NetworkInterface networkInterface = interfaces.nextElement();
				if (networkInterface.isLoopback())
					continue;    
				for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) 
				{
					broadcast = interfaceAddress.getBroadcast();
					if (broadcast != null){
						return broadcast ;
					}
				}
			}	
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return broadcast ;
	}








    
    //return true if all other agent agreed the connection
    public boolean sendBroadCastWithName(UDPClient clientUdp) {
		ExecutorService es = Executors.newCachedThreadPool();
		InetAddress broadCastIp = getBroadcast();

		es.execute(new ClientRunnable(broadCastIp,25555,25554,clientUdp));

		try {
			es.awaitTermination(4, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
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
    
    public boolean sendBroadCast(String pseudoChoisi) {
    	
		boolean connected = false ;
		System.out.println("Choisir un nom :");
		this.setPseudo(pseudoChoisi);
		System.out.println("Pseudo choisit :" + this.getPseudo() + " , envoie aux autres users en cours ...");
		UDPClient clientUdp = new UDPClient(this.getPseudo(), this.getAllPseudos());
		connected = this.sendBroadCastWithName(clientUdp);
		if (connected) {
			this.listOfPseudo = clientUdp.getList();
			this.listOfPseudo.put(this.getPseudo(), this.getIpAddr());
		}else {
			System.out.println("Pseudo deja utilise");
		}
		
		return connected ;

    }
    
    
    public void connectToUser(String pseudoToContact, ChatWindow chat) {
		//System.out.println("Entrez un pseudo pour discuter avec lui");
		//String pseudoToContact = this.askForInput();
		
		if (this.listOfPseudo.containsKey(pseudoToContact)) {
			String addressToContact = this.listOfPseudo.get(pseudoToContact);
			
			try {
				clientSocket = new Socket(addressToContact,25556);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			TCPClient clientTcp = new TCPClient(this.getIpAddr(), addressToContact, clientSocket, this.getPseudo(), pseudoToContact, chat);
			Thread clTcp = new Thread(clientTcp);
			clTcp.start();
		}
	}
	






    
    
    
    
    
    
	
	

	
	
	

}
