
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
            try {
            	
                DatagramSocket server = new DatagramSocket(port);

            	
            		
        		//creation du paquet
        		byte[] buffer = new byte[8192];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        		
                //attente de reception
                server.receive(packet);
                
                //Affiche le resultat
                String str = new String(packet.getData());

                System.out.println("Recu de la part de " + packet.getAddress() 
                + " sur le port " + packet.getPort() + " : " + str);
                
                
                //Reset du paquet pour reutilisation
                packet.setLength(buffer.length);
                
                //reponse
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                ObjectOutputStream outputStream = new ObjectOutputStream(out);
                outputStream.writeObject(allPseudos);
                outputStream.close();
                byte[] buffer2 = out.toByteArray();
                DatagramPacket packet2 = new DatagramPacket(
                                     buffer2,             //Les donnees 
                                     buffer2.length,      //La taille des donnees
                                     packet.getAddress(), //L'adresse de l'emetteur
                                     packet.getPort()     //Le port de l'emetteur
                                     
                );
                
                //Et on envoie vers l'emetteur du datagramme recu precedemment
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
