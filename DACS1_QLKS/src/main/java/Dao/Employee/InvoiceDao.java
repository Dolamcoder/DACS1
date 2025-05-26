package Dao.Employee;
import Model.Invoice;
import Util.JDBC;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InvoiceDao {
    private final Connection conn= JDBC.getConnection();

    public InvoiceDao() {

    }
    // Create - Insert
    public boolean insert(Invoice invoice){
        String sql = "INSERT INTO invoice (invoiceID, bookingID, ServiceBookingID, totalAmount, tax, discount, issueDate, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, invoice.getInvoiceID());
            stmt.setString(2, invoice.getBookingID());
            stmt.setString(3, invoice.getServiceBookingID());
            stmt.setDouble(4, invoice.getTotalAmount());
            stmt.setDouble(5, invoice.getTax());
            stmt.setDouble(6, invoice.getDiscount());
            stmt.setDate(7, invoice.getIssueDate());
            stmt.setString(8, invoice.getStatus());
            return stmt.executeUpdate() > 0;
        }catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Read - Find by invoiceID
    public Invoice findById(String invoiceID)  {
        String sql = "SELECT * FROM invoice WHERE invoiceID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, invoiceID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToInvoice(rs);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public ArrayList<Invoice> findAll() {
        ArrayList<Invoice> list = new ArrayList<>();
        String sql = "SELECT * FROM invoice";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapRowToInvoice(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Update
    public boolean update(Invoice invoice){
        String sql = "UPDATE invoice SET bookingID=?,  totalAmount=?, tax=?, discount=?, issueDate=?, status=?, serviceBookingID = ? WHERE invoiceID=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, invoice.getBookingID());
            stmt.setDouble(2, invoice.getTotalAmount());
            stmt.setDouble(3, invoice.getTax());
            stmt.setDouble(4, invoice.getDiscount());
            stmt.setDate(5, invoice.getIssueDate());
            stmt.setString(6, invoice.getStatus());
            stmt.setString(7, invoice.getServiceBookingID());
            stmt.setString(8, invoice.getInvoiceID());
            return stmt.executeUpdate() > 0;
        }catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Delete
    public boolean delete(String invoiceID) {
        String sql = "DELETE FROM invoice WHERE invoiceID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, invoiceID);
            return stmt.executeUpdate() > 0;
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }
    // Delete
    public boolean delete2(String bookingID) {
        String sql = "DELETE FROM invoice WHERE bookingID= ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, bookingID);
            return stmt.executeUpdate() > 0;
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }


    // Search by bookingID (partial match)
    public Invoice findByBookingID(String bookingID) {
        String sql = "SELECT * FROM invoice WHERE bookingID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, bookingID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToInvoice(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Trả về null nếu không tìm thấy hoặc có lỗi
    }



    // Helper method: map ResultSet row to Invoice object
    private Invoice mapRowToInvoice(ResultSet rs) throws SQLException {
        Invoice invoice = new Invoice();
        invoice.setInvoiceID(rs.getString("invoiceID"));
        invoice.setBookingID(rs.getString("bookingID"));
        invoice.setServiceBookingID(rs.getString("serviceBookingID"));
        invoice.setTotalAmount(rs.getDouble("totalAmount"));
        invoice.setTax(rs.getDouble("tax"));
        invoice.setDiscount(rs.getDouble("discount"));
        invoice.setIssueDate(rs.getDate("issueDate"));
        invoice.setStatus(rs.getString("status"));
        return invoice;
    }
}
