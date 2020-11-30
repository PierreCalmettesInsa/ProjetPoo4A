
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class UDPServer {
	
	protected int port ;
	public ArrayList<String> allPseudos ;

	
	public UDPServer(int port,  ArrayList<String> allPseudos)
	{
		this.port = port;
		this.allPseudos = allPseudos ;
	}
	

	
	public Thread setServer() {
	 Thread t = new Thread(new Runnable(){
         public void run(){
        	 
        	 while(true) {
        		 
	            try {
	            	
	                DatagramSocket server = new DatagramSocket(port);
	                
	                //Seulement pour les tests
	                if (allPseudos.size() < 2) {
		            	allPseudos.add("Test");
	                }
	
	            		
	        		//creation du paquet
	        		byte[] buffer = new byte[8192];
	                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
	        		
	                //attente de reception
	              	 System.out.println("En attente");
	
	                server.receive(packet);
	                
	                //Affiche le resultat
	                String newPseudo = new String(packet.getData(), StandardCharsets.UTF_8);


	
	                System.out.println("Recu de la part de " + packet.getAddress() 
	                + " sur le port " + packet.getPort() + " :" + newPseudo + ".");
	                
	                
	                System.out.println(allPseudos.contains(newPseudo));
	                
	              
	                if (allPseudos.contains(newPseudo)){
	                	System.out.println("Connexion refusee, pseudo deja utilise");
	                	byte[] buffer2 = (new String("Refused")).getBytes();
		                DatagramPacket packet2 = new DatagramPacket(
		                                     buffer2,             //Les donnees 
		                                     buffer2.length,      //La taille des donnees
		                                     packet.getAddress(), //L'adresse de l'emetteur
		                                     packet.getPort()     //Le port de l'emetteur
		                                     
		                );
		                
		                //Et on envoie vers l'emetteur du datagramme recu precedemment
		                server.send(packet2);
		                
	                }else {
	                //reponse
	                for (String pseudo : allPseudos) {
	                	System.out.println("Envoie de : " + pseudo);
		                byte[] buffer2 = pseudo.getBytes();
		                DatagramPacket packet2 = new DatagramPacket(
		                                     buffer2,             //Les donnees 
		                                     buffer2.length,      //La taille des donnees
		                                     packet.getAddress(), //L'adresse de l'emetteur
		                                     packet.getPort()     //Le port de l'emetteur
		                                     
		                );
		                
		                //Et on envoie vers l'emetteur du datagramme recu precedemment
		                server.send(packet2);
		                packet2.setLength(buffer2.length);
		                packet.setData(buffer);
		                server.receive(packet);
	                }
	                
	                //On envoie Finished
	                byte[] buffer2 = (new String("Finished")).getBytes();
	                DatagramPacket packet2 = new DatagramPacket(
	                                     buffer2,             //Les donnees 
	                                     buffer2.length,      //La taille des donnees
	                                     packet.getAddress(), //L'adresse de l'emetteur
	                                     packet.getPort()     //Le port de l'emetteur
	                                     
	                );
	                server.send(packet2);
	                
	                //Ajout du nouveau client dans la liste
	                allPseudos.add(newPseudo);
	                }
	                
	   	       	 	server.close();
	   	       	 	


	        	
		        } catch (SocketException e) {
		            e.printStackTrace();
		        } catch (IOException e) {
		            // TODO Auto-generated catch block
		            e.printStackTrace();
		        }
	            
	       	 System.out.println("Envoi termine");
        	 }
       	 
         }
  
	 });
	 
	 return t ;
	}
	
	

}
