package Dao.Employee;

import Dao.Employee.DaoInterface;
import Model.ServiceBooking;
import Util.JDBC;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceBookingDao implements DaoInterface<ServiceBooking> {

    private Connection connection;
    @Override
    public boolean insert(ServiceBooking obj) {
        connection= JDBC.getConnection();
        String sql = "INSERT INTO servicebooking(serviceBookingID, roomID, quantity, totalAmount) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, obj.getServiceBookingID());
            stmt.setString(2, obj.getRoomID());
            stmt.setInt(3, obj.getQuantity());
            stmt.setDouble(4, obj.getTotalAmount());
            int ans= stmt.executeUpdate();
            connection.close();
            return ans > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(ServiceBooking obj) {
        connection=JDBC.getConnection();
        String sql = "UPDATE servicebooking SET roomID = ?, quantity = ?, totalAmount = ? WHERE serviceBookingID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, obj.getRoomID());
            stmt.setInt(2, obj.getQuantity());
            stmt.setDouble(3, obj.getTotalAmount());
            stmt.setString(4, obj.getServiceBookingID());
            int ans=stmt.executeUpdate();
            connection.close();
            return ans> 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(String id) {
        connection=JDBC.getConnection();
        String sql = "DELETE FROM servicebooking WHERE serviceBookingID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            int ans=stmt.executeUpdate();
            connection.close();
            return ans > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public ServiceBooking getById(String id) {
        connection=JDBC.getConnection();
        String sql = "SELECT * FROM servicebooking WHERE serviceBookingID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new ServiceBooking(
                        rs.getString("serviceBookingID"),
                        rs.getString("roomID"),
                        rs.getInt("quantity"),
                        rs.getDouble("totalAmount")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ArrayList<ServiceBooking> selectAll()  {
        connection=JDBC.getConnection();
        ArrayList<ServiceBooking> list = new ArrayList<>();
        String sql = "SELECT * FROM servicebooking";
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                ServiceBooking sb = new ServiceBooking(
                        rs.getString("serviceBookingID"),
                        rs.getString("roomID"),
                        rs.getInt("quantity"),
                        rs.getDouble("totalAmount")
                );
                list.add(sb);
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    public String  getByRoomId(String roomId) {
        connection = JDBC.getConnection();
        String sql = "SELECT serviceBookingID FROM servicebooking WHERE roomID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, roomId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String serviceBookingID = rs.getString("serviceBookingID");
                connection.close();
                return serviceBookingID;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
   public ServiceBooking searchById(String serviceBookingId) {
        connection = JDBC.getConnection();
        String sql = "SELECT * FROM servicebooking WHERE  serviceBookingID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, serviceBookingId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                ServiceBooking sb = new ServiceBooking(
                        rs.getString("serviceBookingID"),
                        rs.getString("roomID"),
                        rs.getInt("quantity"),
                        rs.getDouble("totalAmount")
                );
                connection.close();
                return sb;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
