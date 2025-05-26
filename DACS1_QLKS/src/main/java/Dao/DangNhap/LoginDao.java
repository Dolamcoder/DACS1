package Dao.DangNhap;
import Model.Account;
import Util.JDBC;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginDao {
    private Connection con;

    public LoginDao() {
        con = JDBC.getConnection();
    }

    public Account checkAccount(String user, String pass) {
        String query = "select * from account where username = ? and password=?";
        try {
            PreparedStatement stm = con.prepareStatement(query);
            stm.setString(1, user);
            stm.setString(2, pass);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) { // Kiểm tra xem có dữ liệu không
                Account account = new Account(user, pass, rs.getString("email"), rs.getString("name"), rs.getInt("role"));
                return account;
            }
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean insertAccount(Account account) {
        String query = "INSERT INTO account (username, password, email, name, path , role) VALUES (?, ?, ?,  ?, ?, ?)";
        try {
            PreparedStatement stm = con.prepareStatement(query);
            stm.setString(1, account.getName());
            stm.setString(2, account.getPassword());
            stm.setString(3, account.getEmail());
            stm.setString(4, account.getName());
            stm.setInt(5, account.getRole());
            stm.setString(6, account.getPath());
            int rowsInserted = stm.executeUpdate();
            con.close();
            return rowsInserted > 0; // Trả về true nếu có ít nhất một dòng được chèn
        } catch (SQLException e) {
            e.printStackTrace();

        }
        return false;
    }
}
