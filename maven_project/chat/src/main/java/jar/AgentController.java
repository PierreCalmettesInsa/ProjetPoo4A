package jar;

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;



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
		UDPServer serverUdp = new UDPServer(agentClient.getPortNum(), agentClient.getAllPseudos());
		Thread server = serverUdp.setServer();
		server.start();

		//And create a tcp server
		TCPServer serverTcp = new TCPServer(agentClient.getPortNum(), chatWindow);
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

        /*
        JComboBox<String> combo = chatWindow.getListPseudos();

        for (String pseudo : allPseudos){
            combo.addItem(pseudo);

        }

        chatWindow.setListPseudos(combo);
        JFrame connectionFrame = chatWindow.getConnectionFrame();

        SwingUtilities.updateComponentTreeUI(connectionFrame);
        connectionFrame.invalidate();
        connectionFrame.validate();
        connectionFrame.repaint();
        */

        chatWindow.updateConnectionFrame(allPseudos);

        //JPanel panelChoix = chatWindow.getPanel() ;

      //  JFrame frameToChange =  chatWindow.getConnectionFrame();

       // frameToChange.getContentPane().add(panelChoix , BorderLayout.SOUTH);
        
       // frameToChange.pack();
       // frameToChange.setVisible(true);
        
       // chatWindow.setConnectionFrame(frameToChange);

    }



























}