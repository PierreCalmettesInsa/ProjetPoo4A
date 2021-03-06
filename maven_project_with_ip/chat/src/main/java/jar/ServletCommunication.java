package jar;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JFileChooser;

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
        msgFrame.getButtonFile().addActionListener(e -> sendFile());

        msgFrame.getFrame().addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                agent.sendMsgToServlet(myName, otherUserName, "// Disconnected \\ code 12548trfsg58K");
                System.out.println("Closed");
                e.getWindow().dispose();
            }
        });
    }

    public void send() {
        String msgToSend = msgFrame.getMessageField().getText();

        if (msgToSend  != "" ){
            SimpleDateFormat h = new SimpleDateFormat ("hh:mm");
			Date date = new Date();
			String time = h.format(date);
            msgFrame.getMessageArea().append(myName + " at " + time + " : " + msgToSend + "\n");

            msgFrame.getMessageField().setText("");

            agent.sendMsgToServlet(myName, otherUserName, msgToSend);

            DatabaseChat.addToHistory(agent.getIpAddr(), distantIpAddress, (myName + " at " + time + " : " + msgToSend));
        }

        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


    public void sendFile(){

        JFileChooser fChooser = new JFileChooser();
		fChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		int fileChosen = fChooser.showOpenDialog(msgFrame.getFrame());

		if (fileChosen == JFileChooser.APPROVE_OPTION) {
			File file = fChooser.getSelectedFile();

            agent.sendFileToServlet(myName, otherUserName, file);

            SimpleDateFormat h = new SimpleDateFormat ("hh:mm");
			Date date = new Date();
			String time = h.format(date);
            msgFrame.getMessageArea().append(myName + " at " + time + " : " + "Fichier envoyé" + "\n");

            DatabaseChat.addToHistory(agent.getIpAddr(), distantIpAddress, (myName + " at " + time + " : " + "Fichier envoyé\n"));

        }


    }

    public boolean receive() {
        String msgReceived = agent.getMsgFromServlet(myName, otherUserName);
        if (msgReceived.trim().equals("// Disconnected \\ code 12548trfsg58K")){
            msgFrame.getMessageArea().append("User disconnected\n");
            return false ;
        } else if (msgReceived != "") {
            SimpleDateFormat h = new SimpleDateFormat ("hh:mm");
			Date date = new Date();
			String time = h.format(date);
            msgFrame.getMessageArea().append(otherUserName + " at " + time + " : " + msgReceived + "\n");
            DatabaseChat.addToHistory(agent.getIpAddr(), distantIpAddress, (otherUserName + " at " + time + " : " + msgReceived));
        }
        return true ;
    }

    public void receiveFile() {
        String msgReceived = agent.getFileFromServlet(myName, otherUserName);

        if (msgReceived != ""){
            SimpleDateFormat h = new SimpleDateFormat ("hh:mm");
            Date date = new Date();
            String time = h.format(date);
            msgFrame.getMessageArea().append(otherUserName + " at " + time + " : " + msgReceived + "\n");
            DatabaseChat.addToHistory(agent.getIpAddr(), distantIpAddress, (otherUserName + " at " + time + " : " + msgReceived));
        }
    }

    public void run() {


        //Look for history, to be replace by ip address
        ArrayList<String> allMessagesHisto = DatabaseChat.getHistory(agent.getIpAddr(), distantIpAddress);

        for (String msg : allMessagesHisto){
            msgFrame.getMessageArea().append(msg + "\n");
        }


        msgFrame.getMessageArea().append("Vous êtes en discussion avec : " + otherUserName + "\n");

        boolean stillConnected = true ;
        while (stillConnected) {
            stillConnected = receive();
            receiveFile() ;
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }



}
