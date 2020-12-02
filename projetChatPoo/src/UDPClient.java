
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
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
	
		 
	public Runnable send_pseudo(String address, int port){
            String packetPseudoToClient = pseudo.trim();
            byte[] bufEnvoie = packetPseudoToClient.getBytes();
            
            try {
	               System.out.println(packetPseudoToClient + " envoie : " + packetPseudoToClient + " to : " + address + " " + port);

	               DatagramSocket client = new DatagramSocket();
	               
	               InetAddress adresse = InetAddress.getByName(address);
	               
	               DatagramPacket packet = new DatagramPacket(bufEnvoie, bufEnvoie.length, adresse, port);
	               
	               packet.setData(bufEnvoie);
	               
	               client.send(packet);
	               
	               byte[] bufRecept = new byte[1024];
	               DatagramPacket packet2 = new DatagramPacket(bufRecept, bufRecept.length, adresse, port);
	               client.receive(packet2);
	               String paquetCourant = new String(packet2.getData()).trim();
	               
	               if (paquetCourant.equals("Refused")) {
	            	   System.out.println("Choisir un nouveau pseudo");
	            	   isConnected = false ;
	               }
	               else {
		               while (!paquetCourant.equals("Finished"))
		               {
		            	   System.out.println(packetPseudoToClient + " a recu une reponse du serveur, nom : " + paquetCourant);
		            	   
		            	   //On a recu un nom, on demande le port associe
		            	   packet.setData(new String("Port now").getBytes());
		            	   client.send(packet);
	            	   
		            	   //On le recoit
		            	   byte[] BufForPort = new byte[1024];
		            	   packet2.setData(BufForPort);
			               client.receive(packet2);
			               String remotePort = new String(packet2.getData()).trim();
			               
			               //Adding Pseudo and port to the HashMap
		            	   allPseudos.put(paquetCourant, remotePort);
		            	   
		            	   //Ask for the next pseudo
		            	   packet.setData(bufEnvoie);
		            	   client.send(packet);
		            	   
		            	   //Reception of next pseudo
			               byte[] newBufRecept = new byte[1024];
		            	   packet2.setData(newBufRecept);
			               client.receive(packet2);
			               paquetCourant = new String(packet2.getData()).trim();
		            	   
		               }
	               }
	               client.close();

	               
            } catch (SocketException e) {
               e.printStackTrace();
            } catch (UnknownHostException e) {
               e.printStackTrace();
            } catch (IOException e) {
               e.printStackTrace();
            }
			return null;
         }

}






class ClientRunnable implements Runnable {
	String address;
	int port ;
	UDPClient client ;
	
   public ClientRunnable(String address, int port, UDPClient client) {
	   this.address = address ;
	   this.port = port;
	   this.client = client;
   }

   public void run() {
	   client.send_pseudo(address, port);
   }
}






















