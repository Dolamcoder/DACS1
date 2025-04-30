package Dao.Employee;

import Model.Booking;
import Util.JDBC;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class RoomBookingDao implements DaoInterface<Booking> {
    Connection con;

    public RoomBookingDao() {
        con = JDBC.getConnection();
    }

    @Override
    public boolean insert(Booking booking) {
        String query = "INSERT INTO booking (bookingId, customerId, roomId, checkInDate, checkOutDate, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, booking.getBookingId());
            stmt.setString(2, booking.getCustomerId());
            stmt.setString(3, booking.getRoomId());
            stmt.setDate(4, Date.valueOf(booking.getCheckInDate()));
            stmt.setDate(5, Date.valueOf(booking.getCheckOutDate()));
            stmt.setString(6, booking.getStatus());
            int rowsAffected = stmt.executeUpdate();
            con.close();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Booking booking) {
        String query = "UPDATE booking SET customerId = ?, roomId = ?, checkInDate = ?, checkOutDate = ?, status = ? WHERE bookingId = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, booking.getCustomerId());
            stmt.setString(2, booking.getRoomId());
            stmt.setDate(3, Date.valueOf(booking.getCheckInDate()));
            stmt.setDate(4, Date.valueOf(booking.getCheckOutDate()));
            stmt.setString(5, booking.getStatus());
            stmt.setString(6, booking.getBookingId());
            int rowsAffected = stmt.executeUpdate();
            con.close();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(String id) {
        String query = "DELETE FROM booking WHERE bookingId = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, id);
            int rowsAffected = stmt.executeUpdate();
            con.close();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public ArrayList<Booking> selectAll() {
        ArrayList<Booking> bookings = new ArrayList<>();
        String query = "SELECT * FROM booking";
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Booking booking = new Booking(
                        rs.getString("bookingId"),
                        rs.getString("customerId"),
                        rs.getString("roomId"),
                        rs.getDate("checkInDate").toLocalDate(),
                        rs.getDate("checkOutDate").toLocalDate(),
                        rs.getString("status")
                );
                bookings.add(booking);
            }
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }
}