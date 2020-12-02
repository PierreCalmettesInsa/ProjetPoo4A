
import java.net.InetAddress;
import java.net.Socket;
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

public class AgentClient {
	
    private int userId;
	protected String address ;
	protected int port ;
	protected HashMap<String, String> listOfPseudo ;
    private String pseudo;
    

	public AgentClient(int userId, String address, int port)
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
    
    public String askForInput(Scanner scan) {
		String pseudo = scan.next();
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
    
    
    public static void displayList(HashMap<String, String> list) {
    	
    	for (Map.Entry<String, String> user : list.entrySet()) {
    		System.out.print(user.getKey() + " ");
    		//System.out.print(user.getValue() + "  ");
    	}
    	System.out.println(" ");
    }
    
    
    
	
	
	public static void main(String[] args) {
		

		int port = Integer.parseInt(args[0]);
		
		String address = args[1] ;
		
		
		
		//Choisir un pseudo, dans l'invite de commande demande au user de taper un nom
		
		System.out.println("Creation de l'agent");
		
		
		AgentClient client = new AgentClient(0,address,port);

		System.out.println("Agent creer avec comme adresse : " + address + " et comme port : " + port);
		


		
		//Envoie un broadcast a tous les autres users si ce n'est pas le premier
		
		
		
		boolean connected = false ;
		while (!connected) {
			System.out.println("Choisir un nom :");
			client.setPseudo(client.askForInput(new Scanner(System.in)));
			System.out.println("Pseudo choisit :" + client.getPseudo() + " , envoie aux autres users en cours ...");
			UDPClient clientUdp = new UDPClient(client.getPseudo(), client.getAllPseudos());
			connected = client.sendBroadCastWithName(clientUdp);
			if (!connected) {
				System.out.println("Pseudo deja utilise");
			}else {
				client.listOfPseudo = clientUdp.getList();
			}
		}
		//Set<String> listWithoutDuplicate = new HashSet<String>(client.listOfPseudo);
		//ArrayList<String> listInter = new ArrayList<String>(listWithoutDuplicate);
		//client.listOfPseudo = listInter ;
		
		client.listOfPseudo.put(client.getPseudo(), Integer.toString(client.getPortNum()));

		
		System.out.println("Connected !");
		System.out.println("Voici la liste des utilisateurs disponibles :");
		displayList(client.getAllPseudos());
		
		
		//when connected, create udp server
		UDPServer serverUdp = new UDPServer(port, client.getAllPseudos());
		Thread server = serverUdp.setServer();
		server.start();
		
		
		
		
		
		//And create a tcp server
		TCPServer serverTcp = new TCPServer(client.port);
		Thread servTcp = new Thread(serverTcp);
		servTcp.start();
		
		
		
		//Ask the user to choose a name
		System.out.println("Entrez un pseudo pour discuter avec lui");
		String pseudoToContact = client.askForInput(new Scanner(System.in));
		
		if (client.listOfPseudo.containsKey(pseudoToContact)) {
			int portToContact = Integer.parseInt(client.listOfPseudo.get(pseudoToContact));
			System.out.println(portToContact);
			TCPClient clientTcp = new TCPClient(portToContact, "127.0.0.1");
			Thread clTcp = new Thread(clientTcp);
			clTcp.start();
		}
		
		
		//Recoit la rep, soit il est connecte, soit il faut choisir un autre pseudo, s'il est connecte il recoit egalement la liste des pseudos de tous les users
		
		
		//Il peut choisir d'envoyer a un user ou d'attendre que qqun le contacte
		
		
		//Quand il attend des qu'il recoit un broadcast il repond
		
		




    }
	
	
	

}
