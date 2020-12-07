package jar;

import java.util.ArrayList;

import javax.swing.JOptionPane;


public class AgentController {


    private AgentModel agentClient ;
    private ChatWindow chatWindow ;


    public AgentController(AgentModel agent, ChatWindow window){
        agentClient = agent;
        chatWindow = window;
    }


    public void initConnectionController(){
        chatWindow.getConnectionButton().addActionListener(e -> connection());
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

        		
		//when connected, create udp server
		UDPServer serverUdp = new UDPServer(agentClient.getPortNum(), agentClient.getAllPseudos());
		Thread server = serverUdp.setServer();
		server.start();

		//And create a tcp server
		TCPServer serverTcp = new TCPServer(agentClient.getPortNum());
		Thread servTcp = new Thread(serverTcp);
		servTcp.start();
		
		//Ask the user to choose a name and connect to it
		agentClient.connectToUser();

    }


    public void displayConnectedUser(ArrayList<String> allPseudos){
            
        for (String Pseudo : allpseudos){


            



        }





    }



























}