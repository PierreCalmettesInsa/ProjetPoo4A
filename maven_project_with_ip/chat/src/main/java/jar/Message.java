package jar;
import java.util.*;

public class Message {

    private String msg;
    private AgentModel sender;
    private Date dateMsg;
    private int type;

    //Type du message
    public static final int TEXT_TYPE = 0;
    public static final int IMAGE_TYPE = 1;
    public static final int FILE_TYPE = 2;

    //Crée un message
    public Message(String msg, AgentModel sender, Date dateMsg, int type) {
        this.msg = msg;
        this.sender = sender;
        this.dateMsg = dateMsg;
        this.type = type;
    }

    //Retourne le contenu du message
    public String getMsg() {
        return this.msg;
    }

    //Retourne l'envoyeur du message
    public AgentModel getSender() {
        return this.sender;
    }

    //Retourne la date du message
    public Date getDateMsg() {
        return this.dateMsg;
    }

    //Retourne le type de message
    public int getType() {
        return this.type;
    }
    
}