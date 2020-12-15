package jar;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
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
                DatabaseMetaData meta = connection.getMetaData();
            }

            // Adding a table with 3 columns : user1 id , user2 id and a message
            String sql = "CREATE TABLE history (\n" 
                + "user1 integer PRIMARY KEY,\n" 
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






    public ArrayList<String> getHistory(){
        



        return null;

    }






}
