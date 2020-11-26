import io.*;
import java.util.*;
import java.net.*;

public class User {

    private int userId;
    private String pseudo;
    private int portNum;
    private InetAddress ipAddr;

    //Crée un Utilisateur
    public User(int userId, String pseudo, InetAddress ipAddr) {
        this.userId = userId;
        this.pseudo = pseudo;
        this.ipAddr = ipAddr;
    }

    //Retourne l'ID de l'Utilisateur
    public int getUserId() {
        return this.userId;
    }

    //Retourne le pseudo de l'Utilisateur
    public String getPseudo() {
        return this.pseudo
    }

    //Modifie le pseudo de l'Utilisateur
    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    //Retourne le numéro de Port de l4utilisateur
    public int getPortNum() {
        return this.portNum;
    }

    //Retourne l'adresse IP de l'Utilisateur
    public InetAddress getIpAddr() {
        return this.IpAddr;
    }
}