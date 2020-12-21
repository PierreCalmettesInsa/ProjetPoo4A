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
        try {
        InetAddress inetAddress = InetAddress.getLocalHost() ;
        address = inetAddress.getHostAddress();
        
        if (address.equals("192.168.56.1")){
            address = "192.168.1.25";
        }
        else if (address.equals("127.0.1.1")){
            address = "192.168.1.27";
        }


        }
        catch (UnknownHostException e ){
            e.printStackTrace();
        }

        System.out.println(address);
		
		
		//Choisir un pseudo, dans l'invite de commande demande au user de taper un nom
		
        AgentModel client = new AgentModel(0,address);
        DatabaseChat.checkDbExistsAndCreate(address + ".db");
        ChatWindow v = new ChatWindow();
        v.launchWindowConnection();
        AgentController c = new AgentController(client,v);
        c.initConnectionController();

        System.out.println("Agent creer avec comme adresse : " + address + " et comme port : " + port);
    }
}
