package Dao.DangNhap;

import Util.JDBC;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ForgotPassDao {
    Connection con;
    public ForgotPassDao(){
        con= JDBC.getConnection();
    }
    public boolean searchAccount(String email){
        String query="select username from account where email=?";
        try{
            PreparedStatement stm=con.prepareStatement(query);
            stm.setString(1, email);
            ResultSet rs=stm.executeQuery();
            return rs.next();
        } catch (SQLException e) {
           e.printStackTrace();
        }
     return false;
    }
    public boolean updatePassWord(String email, String password){
        String query="update account set password=? where email=?";
        try{
            PreparedStatement stm=con.prepareStatement(query);
            stm.setString(1,password);
            stm.setString(2, email);
            int rs=stm.executeUpdate();
            return rs>0;
        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }
}
