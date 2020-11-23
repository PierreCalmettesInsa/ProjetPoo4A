package chatSystem;

import java.net.Socket;
import java.util.Scanner;

public class AgentClient {
	
	protected String address ;
	protected int port ;
	
	
	public AgentClient(String address, int port)
	{
		this.address = address ;
		this.port = port ;
		
	}
	
	
	
	
	
	public static void main(String[] args) {
		

		int port = Integer.parseInt(args[0]);
		
		String address = args[1] ;
		
		
		
		//Choisir un pseudo, dans l'invite de commande demande au user de taper un nom
		
		System.out.println("Creation de l'agent");
		
		
		AgentClient client = new AgentClient(address,port);

		System.out.println("Agent creer avec comme adresse : " + address + " et comme port : " + port);
		
		Scanner scan = new Scanner(System.in);
		String pseudo = scan.next();
		scan.close();
		
		System.out.println("Pseudo choisit :" + pseudo + " , envoie aux autres users en cours ...");


		
		//Envoie un broadcast a tous les autres users si ce n'est pas le premier
		UDPClient clientUdp = new UDPClient(pseudo,port,address);
		
		Thread envoiePseudo = new Thread(clientUdp);
		envoiePseudo.start();
		
		
		
		
		
		
		
		//Recoit la rep, soit il est connecte, soit il faut choisir un autre pseudo, s'il est connecte il recoit egalement la liste des pseudos de tous les users
		
		
		//Il peut choisir d'envoyer a un user ou d'attendre que qqun le contacte
		
		
		//Quand il attend des qu'il recoit un broadcast il repond
		
		




    }
	
	
	

}
