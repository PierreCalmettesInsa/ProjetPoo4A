package jar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
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
	
	
	public void send(DatagramSocket server, String msg, InetAddress address, int port) {
		byte[] buffer = (new String(msg).getBytes());
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, port);
		try {
			server.send(packet);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public DatagramPacket receive(DatagramSocket server) {
		byte[] buffer = new byte[1024];
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		try {
			server.receive(packet);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return packet ;
		
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

	                DatagramPacket packet  = receive(server);
	                
	                //Affiche le resultat
	                String newPseudo = new String(packet.getData());
	                newPseudo = newPseudo.trim();
	                
	                send(server, "Send your port", packet.getAddress(), packet.getPort());
	                packet = receive(server);
	                String newPort =String.valueOf(new String(packet.getData()).trim());

	
	                //System.out.println("Recu de la part de " + packet.getAddress() 
	                //+ " sur le port " + packet.getPort() + " :" + newPseudo);
	                
	               	                
	              
	                if (allPseudos.containsKey(newPseudo)){
	                	//System.out.println("Connexion refusee, pseudo deja utilise");
	                	
	                	send(server,"Refused",packet.getAddress(),packet.getPort());
		                
	                }else {
	                //reponse
	                for (Map.Entry<String, String> user : allPseudos.entrySet()) {
	                	String pseudo = user.getKey();
	                	//System.out.println("Envoie de : " + pseudo);
	                	
	                	
	                	send(server,pseudo,packet.getAddress(),packet.getPort());

		                
		                //On recoit la demande de port
	                
	                	packet = receive(server);
		                String reponsePourPort = new String(packet.getData()).trim();
		                
		                if (reponsePourPort.equals("Port now")) {
	                			
		                	send(server,user.getValue(),packet.getAddress(),packet.getPort());

		                	 
		                	packet = receive(server);
   
		                } else {
		                	System.out.println("Mauvais comportement utilisateur, demande de port incorrect");
		                }
	                }
	                
	                //On envoie Finished
	               
                	send(server,"Finished",packet.getAddress(),packet.getPort());

	                
	                //Ajout du nouveau client dans la liste
	                allPseudos.put(newPseudo, newPort);
	                }
	                
	   	       	 	server.close();
	   	       	 	


	        	
		        } catch (SocketException e) {
		            e.printStackTrace();
		        } 
	            
	       	 //System.out.println("Envoi termine");
        	 }
       	 
         }
  
	 });
	 
	 return t ;
	}
	
	

}