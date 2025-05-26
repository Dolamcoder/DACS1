package Dao.Employee;

import Model.Booking;
import Service.BookingSchedulerService;
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
        con=JDBC.getConnection();
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
        con=JDBC.getConnection();
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
        con = JDBC.getConnection();
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
                booking.setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());
                bookings.add(booking);
            }
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }
    public boolean updateBookingStatus(String bookingId, String newStatus) {
        con = JDBC.getConnection();
        String query = "UPDATE booking SET status = ? WHERE bookingId = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, newStatus);
            stmt.setString(2, bookingId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Helper method to get roomId from booking
   public  String getRoomIdFromBooking(String bookingId) {
        String query = "SELECT roomId FROM booking WHERE bookingId = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, bookingId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("roomId");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public Booking getBookingByCustomerId(String customerId) {
        con = JDBC.getConnection();
        String query = "SELECT * FROM booking WHERE customerId = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, customerId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Booking booking = new Booking(
                        rs.getString("bookingId"),
                        rs.getString("customerId"),
                        rs.getString("roomId"),
                        rs.getDate("checkInDate").toLocalDate(),
                        rs.getDate("checkOutDate").toLocalDate(),
                        rs.getString("status")
                );
                booking.setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());
                return booking;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public Booking getBookingById(String bookingId) {
        con = JDBC.getConnection();
        String query = "SELECT * FROM booking WHERE bookingId = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, bookingId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Booking booking = new Booking(
                        rs.getString("bookingId"),
                        rs.getString("customerId"),
                        rs.getString("roomId"),
                        rs.getDate("checkInDate").toLocalDate(),
                        rs.getDate("checkOutDate").toLocalDate(),
                        rs.getString("status")
                );
                booking.setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());
                return booking;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public String getCustomerID(String bookingId) {
        con = JDBC.getConnection();
        String query = "SELECT customerId FROM booking WHERE bookingId = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, bookingId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("customerId");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public Booking getBookingByRoomId(String roomId) {
        con = JDBC.getConnection();
        String query = "SELECT * FROM booking WHERE roomId = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, roomId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Booking booking = new Booking(
                        rs.getString("bookingId"),
                        rs.getString("customerId"),
                        rs.getString("roomId"),
                        rs.getDate("checkInDate").toLocalDate(),
                        rs.getDate("checkOutDate").toLocalDate(),
                        rs.getString("status")
                );
                booking.setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());
                return booking;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}