package chatSystem;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class UDPClient  implements Runnable{
	
	protected String pseudo ;
	protected String address;
	protected int port ;
	protected ArrayList<String> allPseudos ;
	
	
	
	public UDPClient(String pseudo, int port,String address, ArrayList<String> allPseudos)
	{
		this.port = port; 
		this.address = address ;
		this.pseudo = pseudo;
		this.allPseudos = allPseudos;
	}
	
	
	 public void run(){
		 
            String packetPseudoToClient = pseudo;
            byte[] bufEnvoie = packetPseudoToClient.getBytes();
            
            try {
            	
	               DatagramSocket client = new DatagramSocket();
	               
	               InetAddress adresse = InetAddress.getByName(address);
	               
	               DatagramPacket packet = new DatagramPacket(bufEnvoie, bufEnvoie.length, adresse, port);
	               
	               packet.setData(bufEnvoie);
	               
	               client.send(packet);
	               
	               byte[] bufRecept = new byte[8196];
	               DatagramPacket packet2 = new DatagramPacket(bufRecept, bufRecept.length, adresse, port);
	               client.receive(packet2);
	               String pseudo = new String(packet2.getData());
	               System.out.println(packetPseudoToClient + " a recu une reponse du serveur : " + (new String(packet2.getData())));
	               allPseudos.add(pseudo);
            	

               
            } catch (SocketException e) {
               e.printStackTrace();
            } catch (UnknownHostException e) {
               e.printStackTrace();
            } catch (IOException e) {
               e.printStackTrace();
            }
         }

}



