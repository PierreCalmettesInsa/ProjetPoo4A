package jar;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        int port = 25555;
        String address = "";

        //Get the ip address of the host
        try {
            InetAddress inetAddress = InetAddress.getLocalHost() ;
            address = inetAddress.getHostAddress();
        }
        catch (UnknownHostException e ){
            e.printStackTrace();
        }

        System.out.println(address);
		
		
        AgentModel client = new AgentModel(0,address);
        DatabaseChat.checkDbExistsAndCreate(address + ".db");
        ChatWindow v = new ChatWindow();
        v.launchWindowConnection();
        AgentController c = new AgentController(client,v);
        c.initConnectionController();

        //System.out.println("Agent creer avec comme adresse : " + address + " et comme port : " + port);
    }
}
