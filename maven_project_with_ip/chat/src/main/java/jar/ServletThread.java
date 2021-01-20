package jar;

import java.util.ArrayList;
import java.util.stream.Collectors;


public class ServletThread implements Runnable {

    protected AgentModel agentClient;
    protected AgentController controller;

    public ServletThread(AgentModel agentClient, AgentController controller) {
        this.agentClient = agentClient;
        this.controller = controller;
    }

    public void run() {
        while (true) {
            agentClient.servletNotify();
            ArrayList<String> listOfPSeudos = agentClient.getAllPseudos().keySet().stream()
                    .collect(Collectors.toCollection(ArrayList::new));
            controller.displayConnectedUser(listOfPSeudos);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }
    
}
