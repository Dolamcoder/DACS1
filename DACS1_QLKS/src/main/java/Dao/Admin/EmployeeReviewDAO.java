package Dao.Admin;
import Model.EmployeeReview;
import Util.JDBC;

import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.time.LocalDate;

public class EmployeeReviewDAO {
    private Connection conn;

    public EmployeeReviewDAO() {
        this.conn = null;
    }

    // Thêm đánh giá
    public boolean insertReview(EmployeeReview review) {
        conn= JDBC.getConnection();
        String sql = "INSERT INTO employee_review (employeeID, reviewDate, rating_score, comments, bonusAmount) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, review.getEmployeeID());
            stmt.setDate(2, Date.valueOf(review.getReviewDate()));
            stmt.setInt(3, review.getRatingScore());
            stmt.setString(4, review.getComments());
            stmt.setDouble(5, review.getBonusAmount());
            int ans = stmt.executeUpdate();
            return ans > 0;
        }catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Sửa đánh giá
    public boolean updateReview(EmployeeReview review)  {
        conn=JDBC.getConnection();
        String sql = "UPDATE employee_review SET employeeID = ?, reviewDate = ?, rating_score = ?, comments = ?, bonusAmount = ? WHERE reviewID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, review.getEmployeeID());
            stmt.setDate(2, Date.valueOf(review.getReviewDate()));
            stmt.setInt(3, review.getRatingScore());
            stmt.setString(4, review.getComments());
            stmt.setDouble(5, review.getBonusAmount());
            stmt.setInt(6, review.getReviewID());
            int ans = stmt.executeUpdate();
            return ans > 0;
        }catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Xoá đánh giá
    public boolean deleteReview(int reviewID){
        conn=JDBC.getConnection();
        String sql = "DELETE FROM employee_review WHERE reviewID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, reviewID);
            int ans = stmt.executeUpdate();
            return ans > 0;
        }catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Lấy toàn bộ đánh giá
    public List<EmployeeReview> getAllReviews() {
        conn=JDBC.getConnection();
        List<EmployeeReview> list = new ArrayList<>();
        String sql = "SELECT * FROM employee_review";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                EmployeeReview review = new EmployeeReview();
                review.setReviewID(rs.getInt("reviewID"));
                review.setEmployeeID(rs.getString("employeeID"));
                review.setReviewDate(rs.getDate("reviewDate").toLocalDate());
                review.setRatingScore(rs.getInt("rating_score"));
                review.setComments(rs.getString("comments"));
                review.setBonusAmount(rs.getDouble("bonusAmount"));
                list.add(review);
            } return list;

        }catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }

    // Tìm theo ID
    public EmployeeReview getReviewByID(int reviewID)  {
        conn=JDBC.getConnection();
        String sql = "SELECT * FROM employee_review WHERE reviewID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, reviewID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new EmployeeReview(
                            rs.getInt("reviewID"),
                            rs.getString("employeeID"),
                            rs.getDate("reviewDate").toLocalDate(),
                            rs.getInt("rating_score"),
                            rs.getString("comments"),
                            rs.getDouble("bonusAmount")
                    );
                }
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public List<EmployeeReview> getReviewsByDate(int month, int year) {
        conn = JDBC.getConnection();
        List<EmployeeReview> reviews = new ArrayList<>();
        String sql = "SELECT * FROM employee_review WHERE MONTH(reviewDate) = ? AND YEAR(reviewDate) = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, month);
            stmt.setInt(2, year);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    EmployeeReview review = new EmployeeReview(
                            rs.getInt("reviewID"),
                            rs.getString("employeeID"),
                            rs.getDate("reviewDate").toLocalDate(),
                            rs.getInt("rating_score"),
                            rs.getString("comments"),
                            rs.getDouble("bonusAmount")
                    );
                    reviews.add(review);
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
            return null;
        }
        return reviews;
    }
    public EmployeeReview getEmployeeReviewByEmployeeID(String employeeID) {
        conn = JDBC.getConnection();
        String sql = "SELECT * FROM employee_review WHERE employeeID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, employeeID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new EmployeeReview(
                            rs.getInt("reviewID"),
                            rs.getString("employeeID"),
                            rs.getDate("reviewDate").toLocalDate(),
                            rs.getInt("rating_score"),
                            rs.getString("comments"),
                            rs.getDouble("bonusAmount")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
