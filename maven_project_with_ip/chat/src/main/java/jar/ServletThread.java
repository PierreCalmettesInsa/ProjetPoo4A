package jar;

import java.util.ArrayList;
import java.util.stream.Collectors;


public class ServletThread implements Runnable {

    protected AgentModel agentClient;
    protected AgentController controller;
    protected ArrayList<String> lastList ;

    public ServletThread(AgentModel agentClient, AgentController controller) {
        this.agentClient = agentClient;
        this.controller = controller;
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

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }
    
}
