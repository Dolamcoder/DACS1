package Dao.Employee;

import Model.Customer;
import Util.JDBC;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CustomerDao implements DaoInterface<Customer> {
    @Override
    public boolean insert(Customer customer) {
        String sql = "INSERT INTO customers (customerID, name, gender, birthDate, idNumber, address, email, phone, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = JDBC.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, customer.getId());
            ps.setString(2, customer.getName());
            ps.setString(3, customer.getGender());
            ps.setDate(4, customer.getBirth() != null ? Date.valueOf(customer.getBirth()) : null);
            ps.setString(5, customer.getIdNumber());
            ps.setString(6, customer.getDiaChi());
            ps.setString(7, customer.getEmail());
            ps.setString(8, customer.getPhone());
            ps.setString(9, "Đang ở");
            int rowsAffected = ps.executeUpdate();
            con.close();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Customer customer) {
        String sql = "UPDATE customers SET name = ?, gender = ?, birthDate = ?, idNumber = ?, address = ?, email = ?, phone = ?, status = ? WHERE customerID = ?";
        try (Connection con = JDBC.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, customer.getName());
            ps.setString(2, customer.getGender());
            ps.setDate(3, customer.getBirth() != null ? Date.valueOf(customer.getBirth()) : null);
            ps.setString(4, customer.getIdNumber());
            ps.setString(5, customer.getDiaChi());
            ps.setString(6, customer.getEmail());
            ps.setString(7, customer.getPhone());
            ps.setString(8, "Đang ở");
            ps.setString(9, customer.getId());
            int rowsAffected = ps.executeUpdate();
            con.close();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(String id) {
        String sql = "DELETE FROM customers WHERE customerID = ?";
        try (Connection con = JDBC.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            con.close();
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public ArrayList<Customer> selectAll() {
        ArrayList<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers";
        try (Connection con = JDBC.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Customer customer = new Customer();
                customer.setId(rs.getString("customerID"));
                customer.setName(rs.getString("name"));
                customer.setGender(rs.getString("gender"));
                customer.setBirth(rs.getDate("birthDate") != null ? rs.getDate("birthDate").toLocalDate() : null);
                customer.setIdNumber(rs.getString("idNumber"));
                customer.setDiaChi(rs.getString("address"));
                customer.setEmail(rs.getString("email"));
                customer.setPhone(rs.getString("phone"));
                customer.setStatus(rs.getString("status"));
                customers.add(customer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers;
    }

    public boolean insertBooking(Customer customer) {
        String sql = "INSERT INTO customers (customerID, name, email, phone, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = JDBC.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, customer.getId());
            ps.setString(2, customer.getName());
            ps.setString(3, customer.getEmail());
            ps.setString(4, customer.getPhone());
            ps.setString(5, "Vừa đặt phòng");
            int rowsAffected = ps.executeUpdate();
            con.close();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateRoomBooking(Customer customer) {
        String sql = "UPDATE customers SET name = ?, email = ?, phone = ? WHERE customerID = ?";
        try (Connection con = JDBC.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, customer.getName());
            ps.setString(2, customer.getEmail());
            ps.setString(3, customer.getPhone());
            ps.setString(4, customer.getId());
            int rowsAffected = ps.executeUpdate();
            con.close();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Customer searchById(String id) {
        String sql = "SELECT customerID, name, phone, email FROM customers WHERE customerID = ?";
        try (Connection con = JDBC.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Customer customer = new Customer();
                    customer.setId(rs.getString("customerID"));
                    customer.setName(rs.getString("name"));
                    customer.setPhone(rs.getString("phone"));
                    customer.setEmail(rs.getString("email"));
                    return customer;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public Set<String> getAllCustomerIDs() {
        Set<String> ids = new HashSet<>();
        String sql = "SELECT customerId FROM customers";
        try (Connection conn = JDBC.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ids.add(rs.getString("customerId"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ids;
    }
    public boolean insertWithoutEmail(Customer customer) {
        String sql = "INSERT INTO customers (customerID, name, gender, birthDate, idNumber, address, phone, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = JDBC.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, customer.getId());
            ps.setString(2, customer.getName());
            ps.setString(3, customer.getGender());
            ps.setDate(4, customer.getBirth() != null ? Date.valueOf(customer.getBirth()) : null);
            ps.setString(5, customer.getIdNumber());
            ps.setString(6, customer.getDiaChi());
            ps.setString(7, customer.getPhone());
            ps.setString(8, "Đang ở");
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
