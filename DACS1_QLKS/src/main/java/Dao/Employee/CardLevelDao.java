package Dao.Employee;

import Model.CardLevel;
import Util.JDBC;

import java.sql.*;
import java.util.ArrayList;

public class CardLevelDao {
    private Connection conn;

    // Select all card levels
    public ArrayList<CardLevel> selectAll() {
        conn= JDBC.getConnection();
        ArrayList<CardLevel> cardLevels = new ArrayList<>();
        String sql = "SELECT * FROM card_levels";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                CardLevel cardLevel = new CardLevel();
                cardLevel.setStt(rs.getInt("stt"));
                cardLevel.setCustomerID(rs.getString("customerID"));
                cardLevel.setLevelName(rs.getString("levelName"));
                cardLevel.setTotalAmount(rs.getDouble("totalAmount"));
                cardLevel.setDescription(rs.getString("description"));

                cardLevels.add(cardLevel);
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cardLevels;
    }

    // Select card level by primary key
    public CardLevel selectById(int stt) {
        conn= JDBC.getConnection();
        String sql = "SELECT * FROM card_levels WHERE stt = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, stt);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    CardLevel cardLevel = new CardLevel();
                    cardLevel.setStt(rs.getInt("stt"));
                    cardLevel.setCustomerID(rs.getString("customerID"));
                    cardLevel.setLevelName(rs.getString("levelName"));
                    cardLevel.setTotalAmount(rs.getDouble("totalAmount"));
                    cardLevel.setDescription(rs.getString("description"));
                    conn.close();
                    return cardLevel;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Select card levels by customer ID
    public ArrayList<CardLevel> selectByName(String nameCards) {
        conn=JDBC.getConnection();
        ArrayList<CardLevel> cardLevels = new ArrayList<>();
        String sql = "SELECT * FROM card_levels WHERE levelName = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nameCards);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    CardLevel cardLevel = new CardLevel();
                    cardLevel.setStt(rs.getInt("stt"));
                    cardLevel.setCustomerID(rs.getString("customerID"));
                    cardLevel.setLevelName(rs.getString("levelName"));
                    cardLevel.setTotalAmount(rs.getDouble("totalAmount"));
                    cardLevel.setDescription(rs.getString("description"));
                    cardLevels.add(cardLevel);
                }
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cardLevels;
    }
    public CardLevel selectByCustomerId(String customerId) {
        conn = JDBC.getConnection();
        String sql = "SELECT * FROM card_levels WHERE customerID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    CardLevel cardLevel = new CardLevel();
                    cardLevel.setStt(rs.getInt("stt"));
                    cardLevel.setCustomerID(rs.getString("customerID"));
                    cardLevel.setLevelName(rs.getString("levelName"));
                    cardLevel.setTotalAmount(rs.getDouble("totalAmount"));
                    cardLevel.setDescription(rs.getString("description"));
                    conn.close();
                    return cardLevel;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    // Insert a new card level
    public boolean insert(CardLevel cardLevel) {
        conn = JDBC.getConnection();
        String sql = "INSERT INTO card_levels (customerID, levelName, totalAmount, description) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cardLevel.getCustomerID());
            stmt.setString(2, cardLevel.getLevelName());
            stmt.setDouble(3, cardLevel.getTotalAmount());
            stmt.setString(4, cardLevel.getDescription());

            int rowsInserted = stmt.executeUpdate();
            conn.close();
            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Update an existing card level
    public boolean update(CardLevel cardLevel) {
        conn=JDBC.getConnection();
        String sql = "UPDATE card_levels SET customerID = ?, levelName = ?, totalAmount = ?, description = ? WHERE stt = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cardLevel.getCustomerID());
            stmt.setString(2, cardLevel.getLevelName());
            stmt.setDouble(3, cardLevel.getTotalAmount());
            stmt.setString(4, cardLevel.getDescription());
            stmt.setInt(5, cardLevel.getStt());

            int rowsUpdated = stmt.executeUpdate();
            conn.close();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Delete a card level by primary key
    public boolean delete(int stt) {
        conn=JDBC.getConnection();
        String sql = "DELETE FROM card_levels WHERE stt = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, stt);

            int rowsDeleted = stmt.executeUpdate();
            conn.close();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get the latest card level for a customer
    public CardLevel getLatestCardLevelByCustomerId(String customerId) {
        conn=JDBC.getConnection();
        String sql = "SELECT * FROM card_levels WHERE customerID = ? ORDER BY totalAmount DESC LIMIT 1";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    CardLevel cardLevel = new CardLevel();
                    cardLevel.setStt(rs.getInt("stt"));
                    cardLevel.setCustomerID(rs.getString("customerID"));
                    cardLevel.setLevelName(rs.getString("levelName"));
                    cardLevel.setTotalAmount(rs.getDouble("totalAmount"));
                    cardLevel.setDescription(rs.getString("description"));

                    return cardLevel;
                }
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}