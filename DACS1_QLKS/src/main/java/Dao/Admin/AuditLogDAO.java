package Dao.Admin;
import Dao.Employee.DaoInterface;
import Model.AuditLog;
import Util.JDBC;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuditLogDAO implements DaoInterface<AuditLog> {
    private Connection conn;

    public AuditLogDAO() {
        this.conn = JDBC.getConnection();
    }

    @Override
    public ArrayList<AuditLog> selectAll() {
        conn=JDBC.getConnection();
        ArrayList<AuditLog> list = new ArrayList<>();
        String sql = "SELECT * FROM audit_log";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                AuditLog log = new AuditLog();
                log.setLogID(rs.getInt("logID"));
                log.setTableName(rs.getString("tableName"));
                log.setRecordID(rs.getString("recordID"));
                log.setAction(rs.getString("action"));
                log.setActionBy(rs.getString("actionBy"));
                log.setActionAt(rs.getTimestamp("actionAt"));
                list.add(log);
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }


    public AuditLog getByID(int id) {
        conn=JDBC.getConnection();
        String sql = "SELECT * FROM audit_log WHERE logID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new AuditLog(
                        rs.getInt("logID"),
                        rs.getString("tableName"),
                        rs.getString("recordID"),
                        rs.getString("action"),
                        rs.getString("actionBy"),
                        rs.getTimestamp("actionAt")
                );
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean insert(AuditLog log) {
        String sql = "INSERT INTO audit_log (tableName, recordID, action, actionBy) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, log.getTableName());
            stmt.setString(2, log.getRecordID());
            stmt.setString(3, log.getAction());
            stmt.setString(4, log.getActionBy());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean update(AuditLog log) {
        conn=JDBC.getConnection();
        String sql = "UPDATE audit_log SET tableName=?, recordID=?, action=?, actionBy=? WHERE logID=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, log.getTableName());
            stmt.setString(2, log.getRecordID());
            stmt.setString(3, log.getAction());
            stmt.setString(4, log.getActionBy());
            stmt.setInt(5, log.getLogID());
            int answers = stmt.executeUpdate();
            conn.close();
            return answers > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(String recordID ) {
        conn=JDBC.getConnection();
        String sql = "DELETE FROM audit_log WHERE recordID  = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, recordID);
            int rowsAffected = stmt.executeUpdate();
            conn.close();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public AuditLog getByRecordID(String recordID) {
        String sql = "SELECT * FROM audit_log WHERE recordID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, recordID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new AuditLog(
                        rs.getInt("logID"),
                        rs.getString("tableName"),
                        rs.getString("recordID"),
                        rs.getString("action"),
                        rs.getString("actionBy"),
                        rs.getTimestamp("actionAt")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public AuditLog getById(int id) {
        String sql = "SELECT * FROM audit_log WHERE logID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new AuditLog(
                        rs.getInt("logID"),
                        rs.getString("tableName"),
                        rs.getString("recordID"),
                        rs.getString("action"),
                        rs.getString("actionBy"),
                        rs.getTimestamp("actionAt")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
