package jar;

import java.util.ArrayList;
import java.util.stream.Collectors;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;




public class ServletThread implements Runnable {

    protected AgentModel agentClient;
    protected AgentController controller;
    protected ArrayList<String> lastList ;
    protected ChatWindow chat;


    public ServletThread(AgentModel agentClient, AgentController controller, ChatWindow chat) {
        this.agentClient = agentClient;
        this.controller = controller;
        this.chat = chat;
    }

    public void run() {

        agentClient.servletNotify();
        lastList = agentClient.getAllPseudos().keySet().stream().collect(Collectors.toCollection(ArrayList::new));
        controller.displayConnectedUser(lastList);
        while (true) {
            agentClient.servletNotify();
            ArrayList<String> listofPseudos = agentClient.getAllPseudos().keySet().stream().collect(Collectors.toCollection(ArrayList::new));

            if (!listofPseudos.equals(lastList)){
                controller.displayConnectedUser(listofPseudos);
                lastList = listofPseudos ;
            }

            //check if someone wants to talk with me through the servlet, if yest it gives the name of the person
            ArrayList<String> isThereAConnection = agentClient.checkForConnection() ;
        

            if (isThereAConnection != null){
                String name = isThereAConnection.get(0);
                String ip = isThereAConnection.get(1);


                Thread servletChatting = new Thread(new ServletCommunication(agentClient, agentClient.getPseudo(),name, ip,chat));
                servletChatting.start();
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }
    
}



