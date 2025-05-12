package Util;

import java.sql.*;

public class JDBC {
    public static Connection getConnection() {
        Connection c = null;
        try {
            com.mysql.cj.jdbc.Driver driver = new com.mysql.cj.jdbc.Driver();
            DriverManager.registerDriver(driver);
            String url = "jdbc:mysql://localhost/dacs1?useUnicode=true&characterEncoding=UTF-8";
            String user = "root";
            String password = "";
            c = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return c;
    }
    public static void closeConnection(Connection c) {
        try{
            if(c!=null)
            {
                c.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
