package Dao.Employee;

import Model.Service;
import Model.ServiceBookingDetail;
import Util.JDBC;

import java.sql.*;
import java.util.ArrayList;

public class ServiceBookingDetailDao implements DaoInterface<ServiceBookingDetail> {
    @Override
    public boolean insert(ServiceBookingDetail detail) {
        String sql = "INSERT INTO ServiceBookingDetail (ServiceBookingID, ServiceID) VALUES (?, ?)";
        try (Connection conn = JDBC.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, detail.getServiceBookingID());
            ps.setInt(2, detail.getServiceID());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean update(ServiceBookingDetail detail) {
        // Không cần thiết nếu bạn không muốn cho phép sửa
        return false;
    }

    @Override
    public boolean delete(String id) {
        // Xoá theo serviceBookingID
        String sql = "DELETE FROM ServiceBookingDetail WHERE ServiceBookingID = ?";
        try (Connection conn = JDBC.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public ArrayList<ServiceBookingDetail> selectAll() {
        ArrayList<ServiceBookingDetail> list = new ArrayList<>();
        String sql = "SELECT * FROM ServiceBookingDetail";
        try (Connection conn = JDBC.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                int stt = rs.getInt("stt");
                String bookingID = rs.getString("ServiceBookingID");
                int serviceID = rs.getInt("ServiceID");

                list.add(new ServiceBookingDetail(stt, bookingID, serviceID));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    public boolean deleteByServiceBookingID(String serviceBookingID) {
        String sql = "DELETE FROM ServiceBookingDetail WHERE serviceBookingID = ?";
        try (Connection conn = JDBC.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, serviceBookingID);
            return ps.executeUpdate() > 0; // trả về true nếu có ít nhất 1 dòng bị xóa
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public ArrayList<ServiceBookingDetail> getByServiceBookingId(String serviceBookingId) {
        ArrayList<ServiceBookingDetail> details = new ArrayList<>();
        // SQL query to select records from ServiceBookingDetail where serviceBookingId matches
        String query = "SELECT * FROM ServiceBookingDetail WHERE ServiceBookingID = ?";
        try (Connection conn = JDBC.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, serviceBookingId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ServiceBookingDetail detail = new ServiceBookingDetail();
                detail.setServiceBookingID(rs.getString("ServiceBookingID"));
                detail.setServiceID(rs.getInt("ServiceID"));
                details.add(detail);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return details;
    }
    public ArrayList<Service> getServicesByBookingId(String serviceBookingId) {
        ArrayList<Service> services = new ArrayList<>();
        String sql = "SELECT s.Name, s.Price FROM Service s " +
                "JOIN ServiceBookingDetail sbd ON s.ServiceID = sbd.ServiceID " +
                "WHERE sbd.ServiceBookingID = ?";
        try (Connection conn = JDBC.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, serviceBookingId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Service service = new Service();
                service.setName(rs.getString("Name"));
                service.setPrice(rs.getDouble("Price"));
                services.add(service);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return services;
    }

}
