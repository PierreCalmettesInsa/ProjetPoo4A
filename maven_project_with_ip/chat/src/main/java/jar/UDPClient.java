package jar;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;


public class UDPClient {
	
	protected String pseudo ;
	protected String myAddress ;

	protected HashMap<String, String> allPseudos ;
	protected boolean isConnected ;
	protected InetAddress[] allLocalIp ;
	
	
	
	
	public UDPClient(String pseudo, String myAddress, HashMap<String, String> allPseudos)
	{
		this.pseudo = pseudo;
		this.myAddress = myAddress;
		this.allPseudos = allPseudos;
		isConnected = true ;
	}
	
	public HashMap<String, String> getList(){
		return this.allPseudos ;
	}
	
	//Send a message through UDP
	public void send(DatagramSocket server, String msg, InetAddress address, int port) {
		byte[] buffer = (new String(msg).getBytes());
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, port);
		try {
			server.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	//Receive a message through UDP
	public DatagramPacket receive(DatagramSocket server) {
		byte[] buffer = new byte[1024];
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		try {
			server.setSoTimeout(500);
			server.receive(packet);
		} catch (IOException e) {
			server.close();
			return null ;
		} 	
		return packet ;
		
	}
	
	
	public Runnable send_pseudo(String address, int remotePort){
            String packetPseudoToClient = pseudo.trim();
            
            try {

	               DatagramSocket client = new DatagramSocket();
	               
	               InetAddress adresse = InetAddress.getByName(address);
	
	               send(client,packetPseudoToClient,adresse, remotePort);
	               
	
	               DatagramPacket packet2 = receive(client);
	               
				   send(client,myAddress, adresse,remotePort);
				   System.out.println(myAddress);

	               
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
		            	   
		            	   //On a recu un nom, on demande l'adresse
		            	  
		            	   send(client,"Address now",adresse, remotePort);
	            	   
		            	   //On le recoit
		            	  
		            	   packet2 = receive(client);
		            	   
			               String otherAddress = new String(packet2.getData()).trim();
			               
			               //Adding Pseudo and address to the HashMap
		            	   allPseudos.put(paquetCourant, otherAddress);
		            	   
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
		 



		public ArrayList<String> sendBroadcast(InetAddress adresse, int remotePort){
			ArrayList<String> allIps = new ArrayList<String>();
			try {
				// System.out.println(packetPseudoToClient + " envoie : " + packetPseudoToClient + " to : " + address + " " + port);

				DatagramSocket client = new DatagramSocket();
				client.setBroadcast(true);

				//System.out.println(adresse   + "   " + remotePort);

				send(client, "Send your ip", adresse, remotePort);
		

				DatagramPacket packet2 = receive(client);
				while (packet2 != null){
					String paquetCourant = new String(packet2.getData()).trim();
					//System.out.println(paquetCourant);

					allIps.add(paquetCourant);
					packet2 = receive(client);

				}
				//System.out.println("fini");

			} catch (SocketException e) {
				e.printStackTrace();
			}
			return allIps ;
		}

}






class ClientRunnable implements Runnable {
	InetAddress address;
	int remotePort1 ;
	int remotePort2 ;

	UDPClient client ;
	
   public ClientRunnable(InetAddress address, int remotePort1, 	int remotePort2, UDPClient client) {
	   this.address = address ;
	   this.remotePort1 = remotePort1;
	   this.remotePort2 = remotePort2;
	   this.client = client;
   }

   public void run() {
	   	ArrayList<String> allIps  = client.sendBroadcast(address, remotePort2);
	   	if (allIps != null){
			for (String ip : allIps){
				//System.out.println(ip);
					client.send_pseudo(ip, remotePort1);
			}
		}
   }
}






















