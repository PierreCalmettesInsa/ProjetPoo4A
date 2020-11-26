
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AgentClient {
	
    private int userId;
	protected String address ;
	protected int port ;
	protected ArrayList<String> listOfPseudo ;
    private String pseudo;
    

	public AgentClient(int userId, String address, int port)
	{
		this.address = address ;
		this.port = port ;
		listOfPseudo = new ArrayList<String>() ;
		pseudo = "";
		
	}
	
	
	public ArrayList<String> getAllPseudos(){
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
	
	
	public static void main(String[] args) {
		

		int port = Integer.parseInt(args[0]);
		
		String address = args[1] ;
		
		
		
		//Choisir un pseudo, dans l'invite de commande demande au user de taper un nom
		
		System.out.println("Creation de l'agent");
		
		
		AgentClient client = new AgentClient(0,address,port);

		System.out.println("Agent creer avec comme adresse : " + address + " et comme port : " + port);
		
		Scanner scan = new Scanner(System.in);
		String pseudo = scan.next();
		scan.close();
		
		System.out.println("Pseudo choisit :" + pseudo + " , envoie aux autres users en cours ...");


		
		//Envoie un broadcast a tous les autres users si ce n'est pas le premier
		
		ExecutorService es = Executors.newCachedThreadPool();
		UDPClient clientUdp = new UDPClient(pseudo, client.getAllPseudos());


		
		for (int i = 1; i<3;i++) {
			es.execute(new ClientRunnable(address, port-i,clientUdp));
		}
		try {
			boolean finished = es.awaitTermination(10, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		

		client.listOfPseudo.add(pseudo);
		client.listOfPseudo.add(pseudo);

		
		
		System.out.println("Connected !");
		
		
		//when connected, create udp server
		UDPServer serverUdp = new UDPServer(port);
		Thread server = serverUdp.setServer();
		server.start();
		
		

		
		//Recoit la rep, soit il est connecte, soit il faut choisir un autre pseudo, s'il est connecte il recoit egalement la liste des pseudos de tous les users
		
		
		//Il peut choisir d'envoyer a un user ou d'attendre que qqun le contacte
		
		
		//Quand il attend des qu'il recoit un broadcast il repond
		
		




    }
	
	
	

}
