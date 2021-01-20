package jar;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;




public class AgentController {


    private AgentModel agentClient ;
    private ChatWindow chatWindow ;
    private Thread serverUdp1 ; 
    private Thread serverUdp2 ;
    private Thread servTcp ;
    private boolean alreadyConnected ;


    public AgentController(AgentModel agent, ChatWindow window){
        agentClient = agent;
        chatWindow = window;
    }

    public AgentModel getAgent(){
        return this.agentClient ;
    }


    public void initConnectionController(){
        alreadyConnected = false ;
        chatWindow.getConnectionButton().addActionListener(e -> connection());
        chatWindow.getButtonChoose().addActionListener(e -> discussWith());

        chatWindow.getConnectionFrame().addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
                System.out.println("Closed");
                if (chatWindow.isOutdoorUser() && alreadyConnected){
                    System.out.println("offline");
                    agentClient.changeStatusServlet("offline", agentClient.getPseudo());
                }
		
				e.getWindow().dispose();
			}
		});
    }


    public void connection(){
        String pseudo = chatWindow.getPseudoTextField().getText();
        boolean connected = false ;


        if (!pseudo.equals("")){

            if (chatWindow.isOutdoorUser()){
                connected = agentClient.sendToServlet(pseudo);
            }
            else {

            //On appelle une fonction du modele pour la connection
            connected = agentClient.sendBroadCast(pseudo);
            }
        }
        if (connected && !alreadyConnected){
            afterConnection();
            chatWindow.getLabelPseudo().setText("Connected !");
            alreadyConnected = true ;
            Thread servletUpdater = new Thread(new ServletThread(agentClient,this));
            servletUpdater.start();

            chatWindow.getConnectionButton().setText("Changer de pseudo");

        } else if (connected && alreadyConnected){
            System.out.println("Changement pseudo");
            ArrayList<String> listOfPSeudos = agentClient.getAllPseudos().keySet().stream().collect(Collectors.toCollection(ArrayList::new)); 

            displayConnectedUser(listOfPSeudos);
        } else {
            chatWindow.getLabelPseudo().setText("Choisir un autre pseudo");
        }
        
    }


    public void afterConnection(){
        System.out.println("Connected !");
		System.out.println("Voici la liste des utilisateurs disponibles :");
        agentClient.displayList(agentClient.getAllPseudos());
        
                
        ArrayList<String> listOfPSeudos = agentClient.getAllPseudos().keySet().stream().collect(Collectors.toCollection(ArrayList::new)); 

        displayConnectedUser(listOfPSeudos);


    	
		//when connected, create udp server to send ip and udp server to send list
        UDPServer serverUdp = new UDPServer(25555, 25554, agentClient.getAllPseudos(), this);
        serverUdp1 = serverUdp.sendMyIp() ;
        serverUdp2 = serverUdp.setServer();
        serverUdp1.start();
		serverUdp2.start();

		//And create a tcp server
		TCPServer serverTcp = new TCPServer(agentClient.getIpAddr(),25556, chatWindow,agentClient.getAllPseudos());
		servTcp = new Thread(serverTcp);
        servTcp.start();

    }


    public void discussWith(){

        String autrePseudo = (String)chatWindow.getListPseudos().getSelectedItem() ;

        if (autrePseudo.equals(agentClient.getPseudo()) || autrePseudo.equals("")){
            System.out.println("Erreur, choisir une autre personne");
        } else {
            agentClient.connectToUser(autrePseudo, chatWindow);
        }

    }


    public void displayConnectedUser(ArrayList<String> allPseudos){

        chatWindow.updateConnectionFrame(allPseudos);

    }



}