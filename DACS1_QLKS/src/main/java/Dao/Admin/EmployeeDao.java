package Dao.Admin;

import Model.Employee;
import Util.JDBC;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDao {

    private Connection conn;

    public EmployeeDao() {
        this.conn = JDBC.getConnection();
    }

    // Lấy danh sách tất cả nhân viên
    public List<Employee> getAllEmployees() {
        JDBC.getConnection();
        List<Employee> list = new ArrayList<>();
        String sql = "SELECT * FROM employee";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Employee emp = new Employee();
                emp.setId(rs.getString("employeeID"));
                emp.setName(rs.getString("name"));
                emp.setEmail(rs.getString("email"));
                emp.setPhone(rs.getString("phone"));
                emp.setGender(rs.getString("gender"));
                emp.setBirth(rs.getDate("birth").toLocalDate());
                emp.setHireDate(rs.getDate("hireDate").toLocalDate());
                emp.setDiaChi(""); // Nếu bạn cần địa chỉ, thêm cột 'address' vào bảng
                emp.setIdNumber(""); // Nếu cần số CCCD, thêm cột 'idNumber' vào bảng
                list.add(emp);
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Thêm nhân viên mới
    public boolean insertEmployee(Employee emp) {
        JDBC.getConnection();
        String sql = "INSERT INTO employee (employeeID, name, email, phone, gender, birth, hireDate, position) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, emp.getId());
            ps.setString(2, emp.getName());
            ps.setString(3, emp.getEmail());
            ps.setString(4, emp.getPhone());
            ps.setString(5, emp.getGender());
            ps.setDate(6, Date.valueOf(emp.getBirth()));
            ps.setDate(7, Date.valueOf(emp.getHireDate()));
            ps.setString(8, ""); // Nếu có trường 'position' trong model thì thêm setPosition()
            int ans= ps.executeUpdate();
            conn.close();
            return ans > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Cập nhật thông tin nhân viên
    public boolean updateEmployee(Employee emp) {
        String sql = "UPDATE employee SET name=?, email=?, phone=?, gender=?, birth=?, hireDate=?, position=? WHERE employeeID=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, emp.getName());
            ps.setString(2, emp.getEmail());
            ps.setString(3, emp.getPhone());
            ps.setString(4, emp.getGender());
            ps.setDate(5, Date.valueOf(emp.getBirth()));
            ps.setDate(6, Date.valueOf(emp.getHireDate()));
            ps.setString(7, ""); // position
            ps.setString(8, emp.getId());
            int ans = ps.executeUpdate();
            conn.close();
            return ans> 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Xóa nhân viên theo ID
    public boolean deleteEmployee(String employeeID) {
        String sql = "DELETE FROM employee WHERE employeeID=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, employeeID);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    // Tìm nhân viên theo ID
    public Employee findEmployeeById(String employeeID) {
        conn=JDBC.getConnection();
        String sql = "SELECT * FROM employee WHERE employeeID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, employeeID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Employee emp = new Employee();
                emp.setId(rs.getString("employeeID"));
                emp.setName(rs.getString("name"));
                emp.setEmail(rs.getString("email"));
                emp.setPhone(rs.getString("phone"));
                emp.setGender(rs.getString("gender"));
                emp.setBirth(rs.getDate("birth").toLocalDate());
                emp.setHireDate(rs.getDate("hireDate").toLocalDate());
                emp.setDiaChi(""); // nếu có cột 'address' thì lấy thêm
                emp.setIdNumber(""); // nếu có cột 'idNumber' thì lấy thêm
                return emp;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
