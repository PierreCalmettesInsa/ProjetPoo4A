package jar;

import java.util.ArrayList;
import java.util.stream.Collectors;




public class AgentController {


    private AgentModel agentClient ;
    private ChatWindow chatWindow ;
    private Thread server ; 
    private Thread servTcp ;
    private boolean alreadyConnected ;


    public AgentController(AgentModel agent, ChatWindow window){
        agentClient = agent;
        chatWindow = window;
    }


    public void initConnectionController(){
        alreadyConnected = false ;
        chatWindow.getConnectionButton().addActionListener(e -> connection());
        chatWindow.getButtonChoose().addActionListener(e -> discussWith());
    }


    public void connection(){
        String pseudo = chatWindow.getPseudoTextField().getText();
        boolean connected = false ;

        if (!pseudo.equals("")){
            //On appelle une fonction du modele pour la connection
            connected = agentClient.sendBroadCast(pseudo);
        }
        if (connected && !alreadyConnected){
            afterConnection();
            chatWindow.getLabelPseudo().setText("Connected !");
            alreadyConnected = true ;
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


    	
		//when connected, create udp server
		UDPServer serverUdp = new UDPServer(agentClient.getPortNum(), agentClient.getAllPseudos(), this);
		server = serverUdp.setServer();
		server.start();

		//And create a tcp server
		TCPServer serverTcp = new TCPServer(agentClient.getPortNum(), chatWindow,agentClient.getAllPseudos());
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