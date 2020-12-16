package jar;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;


public class UDPClient {
	
	protected String pseudo ;

	protected HashMap<String, String> allPseudos ;
	protected boolean isConnected ;
	
	
	
	public UDPClient(String pseudo, HashMap<String, String> allPseudos)
	{
		this.pseudo = pseudo;
		this.allPseudos = allPseudos;
		isConnected = true ;
	}
	
	public HashMap<String, String> getList(){
		return this.allPseudos ;
	}
	
	public void send(DatagramSocket server, String msg, InetAddress address, int port) {
		byte[] buffer = (new String(msg).getBytes());
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, port);
		try {
			server.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public DatagramPacket receive(DatagramSocket server) {
		byte[] buffer = new byte[1024];
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		try {
			server.receive(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return packet ;
		
	}
	
	
	public Runnable send_pseudo(String address, int remotePort, int port){
            String packetPseudoToClient = pseudo.trim();
            
            try {
	              // System.out.println(packetPseudoToClient + " envoie : " + packetPseudoToClient + " to : " + address + " " + port);

	               DatagramSocket client = new DatagramSocket();
	               
	               InetAddress adresse = InetAddress.getByName(address);
	
	               send(client,packetPseudoToClient,adresse, remotePort);
	               
	
	               DatagramPacket packet2 = receive(client);
	               
	               send(client,Integer.toString(port), adresse,remotePort);
	               
	               packet2 = receive(client);
	               
	               
	               String paquetCourant = new String(packet2.getData()).trim();
	               
	               
	               
	               if (paquetCourant.equals("Refused")) {
	            	  // System.out.println("Choisir un nouveau pseudo");
	            	   isConnected = false ;
	               }
	               else {
		               while (!paquetCourant.equals("Finished"))
		               {
		            	  // System.out.println(packetPseudoToClient + " a recu une reponse du serveur, nom : " + paquetCourant);
		            	   
		            	   //On a recu un nom, on demande le port associe
		            	  
		            	   send(client,"Port now",adresse, remotePort);
	            	   
		            	   //On le recoit
		            	  
		            	   packet2 = receive(client);
		            	   
			               String otherPort = new String(packet2.getData()).trim();
			               
			               //Adding Pseudo and port to the HashMap
		            	   allPseudos.put(paquetCourant, otherPort);
		            	   
		            	   //Ask for the next pseudo
		            	
		            	   send(client, packetPseudoToClient, adresse, remotePort);
		            	   
		            	   //Reception of next pseudo
		            	   
		            	   packet2 = receive(client);
			               paquetCourant = new String(packet2.getData()).trim();
		            	   
		               }
	               }
	               client.close();

	               
            } catch (SocketException e) {
               e.printStackTrace();
            } catch (UnknownHostException e) {
               e.printStackTrace();
            }
   
			return null;
         }

}






class ClientRunnable implements Runnable {
	String address;
	int remotePort ;
	int port ;
	UDPClient client ;
	
   public ClientRunnable(String address, int remotePort, UDPClient client, int port) {
	   this.address = address ;
	   this.port = port;
	   this.remotePort = remotePort;
	   this.client = client;
   }

   public void run() {
	   client.send_pseudo(address, remotePort, port);
   }
}






















