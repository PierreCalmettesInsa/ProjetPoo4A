package jar;

import java.util.ArrayList;
import java.util.stream.Collectors;




public class AgentController {


    private AgentModel agentClient ;
    private ChatWindow chatWindow ;


    public AgentController(AgentModel agent, ChatWindow window){
        agentClient = agent;
        chatWindow = window;
    }


    public void initConnectionController(){
        chatWindow.getConnectionButton().addActionListener(e -> connection());
        chatWindow.getButtonChoose().addActionListener(e -> discussWith());
    }


    public void connection(){
        String pseudo = chatWindow.getPseudoTextField().getText();

        if (!pseudo.equals("")){
            //On appelle une fonction du modele pour la connection
            agentClient.sendBroadCast(pseudo);
        }
        System.out.println("Connected !");
		System.out.println("Voici la liste des utilisateurs disponibles :");
        agentClient.displayList(agentClient.getAllPseudos());
        
                
        ArrayList<String> listOfPSeudos = agentClient.getAllPseudos().keySet().stream().collect(Collectors.toCollection(ArrayList::new)); 

        displayConnectedUser(listOfPSeudos);


    	
		//when connected, create udp server
		UDPServer serverUdp = new UDPServer(agentClient.getPortNum(), agentClient.getAllPseudos(), this);
		Thread server = serverUdp.setServer();
		server.start();

		//And create a tcp server
		TCPServer serverTcp = new TCPServer(agentClient.getPortNum(), chatWindow,agentClient.getAllPseudos());
		Thread servTcp = new Thread(serverTcp);
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