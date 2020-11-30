
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;

public class UDPServer {
	
	protected int port ;
	protected ArrayList<String> allPseudos ;
	
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
		            	allPseudos.add("Georges");
		            	allPseudos.add("Test");
		            	allPseudos.add("Test1");
	                }
	
	            		
	        		//creation du paquet
	        		byte[] buffer = new byte[8192];
	                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
	        		
	                //attente de reception
	              	 System.out.println("En attente");
	
	                server.receive(packet);
	                
	                //Affiche le resultat
	                String newPseudo = new String(packet.getData());
	
	                System.out.println("Recu de la part de " + packet.getAddress() 
	                + " sur le port " + packet.getPort() + " : " + newPseudo);
	                
	                
	                //Reset du paquet pour reutilisation
	                packet.setLength(buffer.length);
	                
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
	                
	                //Ajout du nouveau client dans la liste
	                allPseudos.add(newPseudo);
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
