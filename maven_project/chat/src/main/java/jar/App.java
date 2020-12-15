package jar;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        int port = Integer.parseInt(args[0]);
		
		String address = args[1] ;
		
		
		//Choisir un pseudo, dans l'invite de commande demande au user de taper un nom
		
        AgentModel client = new AgentModel(0,address,port);
        DatabaseChat.checkDbExistsAndCreate(port + ".db");
        ChatWindow v = new ChatWindow();
        v.launchWindowConnection();
        AgentController c = new AgentController(client,v);
        c.initConnectionController();

        System.out.println("Agent creer avec comme adresse : " + address + " et comme port : " + port);
    }
}
