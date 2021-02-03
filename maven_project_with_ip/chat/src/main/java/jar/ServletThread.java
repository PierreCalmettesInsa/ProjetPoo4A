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


                Thread servletChatting = new Thread(new servletCommunication(agentClient, agentClient.getPseudo(),name, ip,chat));
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


class servletCommunication implements Runnable {

    protected ChatWindow chat;
    protected String myName ;
    protected String otherUserName ;
    protected MessageFrame msgFrame ;
    protected AgentModel agent ;
    protected String distantIpAddress ;



    public servletCommunication(AgentModel agent, String myName, String otherUserName, String distantIpAddress, ChatWindow chat) {
        this.myName = myName;
        this.otherUserName = otherUserName;
        this.chat = chat;
        this.agent = agent ;
        this.distantIpAddress = distantIpAddress ;

        // creat a new frame for the chat
        msgFrame = chat.launchWindowChat();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Add a listener to the button to send a message
        initListener();
    }

    public void initListener() {
		msgFrame.getButtonSend().addActionListener(e -> send());

		msgFrame.getFrame().addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.out.println("Closed");
				e.getWindow().dispose();
			}
		});
    }
    
    public void send() {
		String msgToSend = msgFrame.getMessageField().getText();
        msgFrame.getMessageArea().append(myName + " : " + msgToSend + "\n");

        agent.sendMsgToServlet(myName, otherUserName, msgToSend);
        


		DatabaseChat.addToHistory(agent.getIpAddr(), distantIpAddress , (myName + " : " + msgToSend));
    }
    
    public void receive(){

        String msgReceived = agent.getMsgFromServlet(myName, otherUserName);

        msgFrame.getMessageArea().append(otherUserName + " : " + msgReceived + "\n");


        DatabaseChat.addToHistory(agent.getIpAddr(), distantIpAddress , (myName + " : " + msgReceived));


    }

    public void run(){}



}
