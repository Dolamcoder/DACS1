package Dao.Employee;

import Dao.Employee.DaoInterface;
import Model.StayHistory;
import Util.JDBC;

import java.sql.*;
import java.util.ArrayList;

public class StayHistoryDao implements DaoInterface<StayHistory> {

    @Override
    public boolean insert(StayHistory sh) {
        String sql = "INSERT INTO stay_history (customerID, roomID, checkIn, checkOut, note) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = JDBC.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, sh.getCustomerID());
            ps.setString(2, sh.getRoomID());
            ps.setDate(3, java.sql.Date.valueOf(sh.getCheckIn()));
            ps.setDate(4, java.sql.Date.valueOf(sh.getCheckOut()));
            ps.setString(5, sh.getNote());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(StayHistory sh) {
        String sql = "UPDATE stay_history SET customerID = ?, roomID = ?, checkIn = ?, checkOut = ?, note = ? WHERE stt = ?";
        try (Connection con = JDBC.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, sh.getCustomerID());
            ps.setString(2, sh.getRoomID());
            ps.setDate(3, java.sql.Date.valueOf(sh.getCheckIn()));
            ps.setDate(4, java.sql.Date.valueOf(sh.getCheckOut()));
            ps.setString(5, sh.getNote());
            ps.setInt(6, sh.getStt());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(String id) {
        String sql = "DELETE FROM stay_history WHERE stt = ?";
        try (Connection con = JDBC.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, Integer.parseInt(id));
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public ArrayList<StayHistory> selectAll() {
        ArrayList<StayHistory> list = new ArrayList<>();
        String sql = "SELECT * FROM stay_history";
        try (Connection con = JDBC.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new StayHistory(
                        rs.getInt("stt"),
                        rs.getString("customerID"),
                        rs.getString("roomID"),
                        rs.getDate("checkIn").toLocalDate(),
                        rs.getDate("checkOut").toLocalDate(),
                        rs.getString("note")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
