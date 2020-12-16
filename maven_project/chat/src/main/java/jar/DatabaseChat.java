package jar;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class DatabaseChat {

    public DatabaseChat() {
        

         
    }

    public static void createNewDatabase(String file) {
        String url = "jdbc:sqlite:" + file;
        Connection connection = null;

        try {
            // Creation of database
            connection = DriverManager.getConnection(url);
            if (connection != null) {
                connection.getMetaData();
            }

            // Adding a table with 3 columns : user1 id , user2 id and a message
            String sql = "CREATE TABLE history (\n" 
                + "user1 integer,\n" 
                + "user2 integer,\n"
                + "message text\n" + ")";

            Statement statement = connection.createStatement();

            statement.execute(sql);

            connection.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void checkDbExistsAndCreate(String filename){

        try {
             Class.forName("org.sqlite.JDBC") ; 
        } catch (Exception e){
            System.out.println(e.getMessage());
            System.out.println("marche pas");
        }

        String pathOfsqlite = System.getProperty("user.dir");
        String path = pathOfsqlite + "\\" + filename ;
        System.out.println(pathOfsqlite);
        File f = new File(path);
        if (!f.exists()){
            createNewDatabase(path);
        }

    }






    public static ArrayList<String> getHistory(int user1, int user2){
        String pathOfsqlite = System.getProperty("user.dir");
        String path = pathOfsqlite + "\\" + user1 + ".db" ;

        ArrayList<String> messages = new ArrayList<String>();

        String sql = "SELECT * FROM history where user1=" + user1 + " AND user2=" + user2 ;

        try {
            Connection connection = DriverManager.getConnection("jdbc:sqlite:" + path);

            Statement stment = connection.createStatement();

            ResultSet res = stment.executeQuery(sql);

            while (res.next()){
                String msg = res.getString("message") ;
                messages.add(msg);
            }



        } catch (SQLException e){
            System.out.println(e.getMessage());
        }



        return messages;

    }




    public static void addToHistory(int user1, int user2, String message){
        String pathOfsqlite = System.getProperty("user.dir");
        String path = pathOfsqlite + "\\" + user1 + ".db" ;

        String sql = "INSERT INTO history(user1,user2,message) VALUES(?,?,?)" ;

        try {
            Connection connection = DriverManager.getConnection("jdbc:sqlite:" + path);

            PreparedStatement prestment = connection.prepareStatement(sql);

            prestment.setInt(1, user1);
            prestment.setInt(2, user2);
            prestment.setString(3, message);

            prestment.executeUpdate();


        } catch (SQLException e){
            System.out.println(e.getMessage());
        }


    }







}
