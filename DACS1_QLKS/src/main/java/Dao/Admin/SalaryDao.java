package Dao.Admin;

import Model.Employee;
import Util.JDBC;
import Model.Salary;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SalaryDao {
    private Connection conn;
    EmployeeDao empDao=new EmployeeDao();
    public SalaryDao() {
        this.conn = null;
    }

    // CREATE
    public boolean insertSalary(Salary salary)  {
        conn= JDBC.getConnection();
        String sql = "INSERT INTO salary (employeeID, amount, transportAllowance) VALUES (?, ?, ?)";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, salary.getEmployeeID());
            stmt.setInt(2, salary.getAmount());
            stmt.setInt(3, salary.getTransportAllowance());
            int ans = stmt.executeUpdate();
            conn.close();
            return ans > 0;
        }
        catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    // READ - All
    public List<Salary> getAllSalaries()  {
        conn = JDBC.getConnection();
        List<Salary> list = new ArrayList<>();
        String sql = "SELECT * FROM salary";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Salary salary = new Salary(
                        rs.getInt("salaryID"),
                        rs.getString("employeeID"),
                        rs.getInt("amount"),
                        rs.getInt("transportAllowance")
                );
                Employee emp= empDao.findEmployeeById(salary.getEmployeeID());
                salary.setName(emp.getName());
                salary.setPosition(emp.getPosition());
                list.add(salary);
            }
            conn.close();
            return list;
        }catch(SQLException e){
            e.printStackTrace();
            return null;
        }
    }

    // READ - By ID
    public Salary getSalaryByID(int salaryID) {
        conn= JDBC.getConnection();
        String sql = "SELECT * FROM salary WHERE salaryID = ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, salaryID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Salary salary=new Salary(
                        rs.getInt("salaryID"),
                        rs.getString("employeeID"),
                        rs.getInt("amount"),
                        rs.getInt("transportAllowance")
                );
                Employee emp = empDao.findEmployeeById(salary.getEmployeeID());
                salary.setName(emp.getName());
                salary.setPosition(emp.getPosition());
            }
            conn.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    // UPDATE
    public boolean updateSalary(Salary salary) {
        conn=JDBC.getConnection();
        String sql = "UPDATE salary SET employeeID = ?, amount = ?, transportAllowance = ? WHERE salaryID = ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, salary.getEmployeeID());
            stmt.setInt(2, salary.getAmount());
            stmt.setInt(3, salary.getTransportAllowance());
            stmt.setInt(4, salary.getSalaryID());
            int ans = stmt.executeUpdate();
            conn.close();
            return ans > 0;
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    // DELETE
    public boolean deleteSalary(int salaryID)  {
        conn=JDBC.getConnection();
        String sql = "DELETE FROM salary WHERE salaryID = ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, salaryID);
            int ans = stmt.executeUpdate();
            conn.close();
            return ans > 0;
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }
    public Salary getSalaryByEmployeeID(String employeeID) {
        conn = JDBC.getConnection();
        String sql = "SELECT * FROM salary WHERE employeeID = ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, employeeID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Salary salary = new Salary(
                        rs.getInt("salaryID"),
                        rs.getString("employeeID"),
                        rs.getInt("amount"),
                        rs.getInt("transportAllowance")
                );
                Employee emp = empDao.findEmployeeById(salary.getEmployeeID());
                salary.setName(emp.getName());
                salary.setPosition(emp.getPosition());
                return salary;
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
