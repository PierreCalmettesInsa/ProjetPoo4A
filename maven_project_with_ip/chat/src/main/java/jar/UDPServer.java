package jar;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class UDPServer {

	protected int port1;
	protected int port2;

	public HashMap<String, String> allPseudos;
	protected AgentController agent;

	public UDPServer(int port1, int port2, HashMap<String, String> allPseudos, AgentController agent) {
		this.port1 = port1;
		this.port2 = port2;
		this.allPseudos = allPseudos;
		this.agent = agent;
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
		return packet;

	}

	public Thread sendMyIp() {
		Thread t = new Thread(new Runnable() {
			public void run() {

				while (true) {

					try {

						DatagramSocket server;
						server = new DatagramSocket(port2, InetAddress.getByName("0.0.0.0"));
				
						server.setBroadcast(true);



						DatagramPacket packet = receive(server);


						try {
							send(server, InetAddress.getLocalHost().getHostAddress(), packet.getAddress(),
									packet.getPort());
						} catch (UnknownHostException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					   server.close();

					} catch (SocketException e) {
						e.printStackTrace();
					} catch (UnknownHostException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		return t ;

	}
	
	public Thread setServer() {
	 Thread t = new Thread(new Runnable(){
         public void run(){
        	 
        	 while(true) {
        		 
	            try {
	            	
	                DatagramSocket server = new DatagramSocket(port1);
	                

	                DatagramPacket packet  = receive(server);
	                
	                //Affiche le resultat
	                String newPseudo = new String(packet.getData());
	                newPseudo = newPseudo.trim();
	                
	                send(server, "Send your port", packet.getAddress(), packet.getPort());
	                packet = receive(server);
	                String newPort =String.valueOf(new String(packet.getData()).trim());

	
	                //System.out.println("Recu de la part de " + packet.getAddress() 
	                //+ " sur le port " + packet.getPort() + " :" + newPseudo);
	                
	               	if (allPseudos.containsValue(newPort)){
						//This port is already in the list
						System.out.println("Removing from list");
						allPseudos.values().remove(newPort);
					}                
	              
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
					ArrayList<String> listOfPSeudos = allPseudos.keySet().stream().collect(Collectors.toCollection(ArrayList::new)); 
					agent.displayConnectedUser(listOfPSeudos);


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
