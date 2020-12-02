
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UDPServer {
	
	protected int port ;
	public HashMap<String, String> allPseudos ;

	
	public UDPServer(int port,  HashMap<String, String> allPseudos)
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
		            	allPseudos.put("Test", "1234");
	                }
	
	            		
	        		//creation du paquet
	        		byte[] buffer = new byte[1024];
	                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
	        		
	                //attente de reception
	              	 System.out.println("En attente");
	
	                server.receive(packet);
	                
	                //Affiche le resultat
	                String newPseudo = new String(packet.getData());
	                newPseudo = newPseudo.trim();
	                String newPort = Integer.toString(packet.getPort());

	
	                System.out.println("Recu de la part de " + packet.getAddress() 
	                + " sur le port " + packet.getPort() + " :" + newPseudo);
	                
	               	                
	              
	                if (allPseudos.containsKey(newPseudo)){
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
	                for (Map.Entry<String, String> user : allPseudos.entrySet()) {
	                	String pseudo = user.getKey();
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
		                
		                //On recoit la demande de port
		                packet.setData(buffer);
		                server.receive(packet);
		                String reponsePourPort = new String(packet.getData()).trim();
		                
		                if (reponsePourPort.equals("Port now")) {
		                	   buffer2 = user.getValue().getBytes() ;
		                	   packet2.setData(buffer2);
		                	   server.send(packet2);
		                	   
		                	   byte[] bufNextUser = new byte[1024];
		                	   packet.setData(bufNextUser);
		                	   server.receive(packet);
   
		                } else {
		                	System.out.println("Mauvais comportement utilisateur, demande de port incorrecte");
		                }
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
	                allPseudos.put(newPseudo, newPort);
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
