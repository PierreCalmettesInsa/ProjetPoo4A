
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


public class UDPClient {
	
	protected String pseudo ;

	protected ArrayList<String> allPseudos ;
	
	
	
	public UDPClient(String pseudo, ArrayList<String> allPseudos)
	{
		this.pseudo = pseudo;
		this.allPseudos = allPseudos;
	}
	
	
		 
	public Runnable send_pseudo(String address, int port){
            String packetPseudoToClient = pseudo;
            byte[] bufEnvoie = packetPseudoToClient.getBytes();
            
            try {
	               System.out.println(packetPseudoToClient + " envoie : " + packetPseudoToClient + "to : " + address + " " + port);

	               DatagramSocket client = new DatagramSocket();
	               
	               InetAddress adresse = InetAddress.getByName(address);
	               
	               DatagramPacket packet = new DatagramPacket(bufEnvoie, bufEnvoie.length, adresse, port);
	               
	               packet.setData(bufEnvoie);
	               
	               client.send(packet);
	               
	               byte[] bufRecept = new byte[8196];
	               DatagramPacket packet2 = new DatagramPacket(bufRecept, bufRecept.length, adresse, port);
           
	               client.receive(packet2);
	               ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(packet2.getData()));
	               
	               try {
					allPseudos = (ArrayList<String>) inputStream.readObject();
	               } catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
	               }
	               
	               System.out.println(packetPseudoToClient + " a recu une reponse du serveur : " + (new String(packet2.getData())));
	               
               
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






















