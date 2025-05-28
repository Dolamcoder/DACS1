package Dao.DangNhap;

import Model.Account;
import Util.JDBC;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LoginDao {
    private Connection con;

    public LoginDao() {
        con = JDBC.getConnection();
    }

    public Account checkAccount(String user, String pass) {
        con = JDBC.getConnection();
        String query = "SELECT * FROM account WHERE username = ? AND password = ?";
        try {
            PreparedStatement stm = con.prepareStatement(query);
            stm.setString(1, user);
            stm.setString(2, pass);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
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
        con = JDBC.getConnection();
        String query = "INSERT INTO account (username, password, email, name, path, role, EmployeeID) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement stm = con.prepareStatement(query);
            stm.setString(1, account.getUserName());
            stm.setString(2, account.getPassword());
            stm.setString(3, account.getEmail());
            stm.setString(4, account.getName());
            stm.setString(5, account.getPath());
            stm.setInt(6, account.getRole());
            stm.setString(7, account.getEmployeeId());
            int rowsInserted = stm.executeUpdate();
            con.close();
            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Account> getAllAccounts() {
        con = JDBC.getConnection();
        List<Account> accounts = new ArrayList<>();
        String query = "SELECT * FROM account";
        try {
            PreparedStatement stm = con.prepareStatement(query);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                Account account = new Account();
                account.setUserName(rs.getString("username"));
                account.setPassword(rs.getString("password"));
                account.setEmail(rs.getString("email"));
                account.setName(rs.getString("name"));
                account.setRole(rs.getInt("role"));
                account.setStt(rs.getInt("accountID"));
                account.setPath(rs.getString("path"));
                account.setEmployeeId(rs.getString("EmployeeID"));
                account.setChucVu();
                accounts.add(account);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return accounts;
    }
    public boolean updateAccount(Account account) {
        con = JDBC.getConnection();
        String query = "UPDATE account SET username=?,  password = ?, email = ?, name = ?,  role = ?, path=? WHERE accountID= ?";
        try {
            PreparedStatement stm = con.prepareStatement(query);
            stm.setString(1, account.getUserName());
            stm.setString(2, account.getPassword());
            stm.setString(3, account.getEmail());
            stm.setString(4, account.getName());
            stm.setInt(5, account.getRole());
            stm.setString(6, account.getPath());
            stm.setInt(7, account.getStt());
            int rowsUpdated = stm.executeUpdate();
            con.close();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean deleteAccount(int stt) {
        String query = "DELETE FROM account WHERE accountID = ?";
        try {
            PreparedStatement stm = con.prepareStatement(query);
            stm.setInt(1, stt);
            int rowsDeleted = stm.executeUpdate();
            con.close();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public Account findAccountByEmployeeId(String employeeId) {
        con = JDBC.getConnection();
        String query = "SELECT * FROM account WHERE EmployeeID = ?";
        try {
            PreparedStatement stm = con.prepareStatement(query);
            stm.setString(1, employeeId);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                Account account = new Account();
                account.setUserName(rs.getString("username"));
                account.setPassword(rs.getString("password"));
                account.setEmail(rs.getString("email"));
                account.setName(rs.getString("name"));
                account.setRole(rs.getInt("role"));
                account.setStt(rs.getInt("accountID"));
                account.setPath(rs.getString("path"));
                account.setEmployeeId(rs.getString("EmployeeID"));
                return account;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}