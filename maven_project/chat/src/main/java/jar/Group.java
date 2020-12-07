package jar;

import java.io.*;
import java.util.*;

public class Group implements Serializable {

    private static final long serialVersionUID = 1L;

    private int groupId;
    private String origin;
    private ArrayList<String> groupMembers;
    private boolean online;

    public Group(int groupId, String origin, ArrayList<String> groupMembers) {
        this.groupId = groupId;
        this.origin = origin;
        this.groupMembers = new ArrayList<String>();
        for(String m : groupMembers) {
            this.groupMembers.add(m);
        }
        this.online = true;
    }
    
    //Retourne l'id du groupe
    public int getGroupId() {
        return groupId;
    }

    //Changer l'id du groupe
    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    //Retourne l'utilisateur qui cree le groupe
    public String getOrigin() {
        return origin;
    }

    //Indique l'utilisateur qui initie la conversation
    public void setOrigin(String origin) {
        this.origin = origin;
    }

    //Retourne les membres du groupe
    public ArrayList<String> getGroupMembers() {
        return groupMembers;
    }

    //Retourne True si le groupe est en ligne
    public boolean isOnline() {
        return online;
    }

    //Indique si le groupe est en ligne
    public void setOnline(boolean online) {
        this.online = online;
    }

}
