package jar;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

public class ServletCommunication implements Runnable {

    protected ChatWindow chat;
    protected String myName;
    protected String otherUserName;
    protected MessageFrame msgFrame;
    protected AgentModel agent;
    protected String distantIpAddress;

    public ServletCommunication(AgentModel agent, String myName, String otherUserName, String distantIpAddress,
            ChatWindow chat) {
        this.myName = myName;
        this.otherUserName = otherUserName;
        this.chat = chat;
        this.agent = agent;
        this.distantIpAddress = distantIpAddress;

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

        if (msgToSend  != "" ){
            msgFrame.getMessageArea().append(myName + " : " + msgToSend + "\n");

            msgFrame.getMessageField().setText("");

            agent.sendMsgToServlet(myName, otherUserName, msgToSend);

            DatabaseChat.addToHistory(agent.getIpAddr(), distantIpAddress, (myName + " : " + msgToSend));
        }

        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void receive() {

        String msgReceived = agent.getMsgFromServlet(myName, otherUserName);

        if (msgReceived != "") {
            msgFrame.getMessageArea().append(otherUserName + " : " + msgReceived + "\n");
            DatabaseChat.addToHistory(agent.getIpAddr(), distantIpAddress, (otherUserName + " : " + msgReceived));
        }
    }

    public void run() {


        //Look for history, to be replace by ip address
        ArrayList<String> allMessagesHisto = DatabaseChat.getHistory(agent.getIpAddr(), distantIpAddress);

        for (String msg : allMessagesHisto){
            msgFrame.getMessageArea().append(msg + "\n");
        }


        msgFrame.getMessageArea().append("Vous êtes en discussion avec : " + otherUserName + "\n");


        while (true) {
            receive();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }



}
