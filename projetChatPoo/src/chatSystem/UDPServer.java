package chatSystem;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UDPServer {
	
	protected int port ;
	
	public UDPServer(int port)
	{
		this.port = port;
	}
	
	public Thread setServer() {
	 Thread t = new Thread(new Runnable(){
         public void run(){
            try {
            	
                DatagramSocket server = new DatagramSocket(port);

            	
            		
        		//creation du paquet
        		byte[] buffer = new byte[8192];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        		
                //attente de reception
                server.receive(packet);
                
                //Affiche le r�sultat
                String str = new String(packet.getData());

                System.out.println("Re�u de la part de " + packet.getAddress() 
                + " sur le port " + packet.getPort() + " : " + str);
                
                
                //Reset du paquet pour reutilisation
                packet.setLength(buffer.length);
                
                //reponse
                byte[] buffer2 = new String("Reponse du serveur � " + str + "! ").getBytes();
                DatagramPacket packet2 = new DatagramPacket(
                                     buffer2,             //Les donnees 
                                     buffer2.length,      //La taille des donn�es
                                     packet.getAddress(), //L'adresse de l'�metteur
                                     packet.getPort()     //Le port de l'�metteur
                                     
                );
                
                //Et on envoie vers l'�metteur du datagramme re�u pr�c�demment
                server.send(packet2);
                packet2.setLength(buffer2.length);
                

        	
        	
	        } catch (SocketException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
         }
         
	 });
	 
	 return t ;
	}
	
	

}
