package Dao.Employee;

import Model.Room;
import Util.JDBC;

import java.sql.*;
import java.sql.Date;
import java.util.ArrayList;

public class RoomDao implements DaoInterface<Room> {
    Connection con;

    public RoomDao() {
        con = JDBC.getConnection();
    }

    @Override
    public boolean insert(Room room) {
        String query = "INSERT INTO room (roomID, number, loaiPhong, price, status) VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement pstm = con.prepareStatement(query);
            pstm.setString(1, room.getId());
            pstm.setInt(2, room.getNumber());
            pstm.setString(3, room.getLoaiPhong());
            pstm.setInt(4, room.getPrice());
            pstm.setInt(5, room.status);

            int rowsAffected = pstm.executeUpdate();
            con.close();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Room room) {
        String query = "UPDATE room SET number = ?, loaiPhong = ?, price = ?, status = ? WHERE roomID = ?";
        try {
            PreparedStatement pstm = con.prepareStatement(query);
            pstm.setInt(1, room.getNumber());
            pstm.setString(2, room.getLoaiPhong());
            pstm.setInt(3, room.getPrice());
            pstm.setInt(4, room.status);
            pstm.setString(5, room.getId());

            int rowsAffected = pstm.executeUpdate();
            con.close();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(String id) {
        String query = "DELETE FROM room WHERE roomID = ?";
        try {
            PreparedStatement pstm = con.prepareStatement(query);
            pstm.setString(1, id);

            int rowsAffected = pstm.executeUpdate();
            con.close();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public ArrayList<Room> selectAll() {
        ArrayList<Room> rooms = new ArrayList<>();
        String query = "SELECT * FROM room";

        try {
            PreparedStatement pstm = con.prepareStatement(query);
            ResultSet rs = pstm.executeQuery();

            while (rs.next()) {
                Room room = new Room();
                room.setId(rs.getString("roomID"));
                room.setNumber(rs.getInt("number"));
                room.setLoaiPhong(rs.getString("loaiPhong"));
                room.setPrice(rs.getInt("price"));
                room.setStatus(rs.getInt("status"));

                rooms.add(room);
            }
            con.close();
            return rooms;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public ArrayList<Room> timKiemPhong1(String loaiPhong, Date ngayNhanPhong, ArrayList<Room> listRoom) {
        con = JDBC.getConnection();
        String query = "SELECT r.roomID, r.number, r.price, r.loaiPhong , r.status FROM room r WHERE r.loaiPhong = ? AND (r.status = 1 OR r.roomID IN (SELECT b.roomId FROM Booking b WHERE b.roomId = r.roomID AND (? > b.checkOutDate OR (? <= b.checkOutDate AND b.status = 'Đã hoàn thành'))))";
        try {
            PreparedStatement pstm = con.prepareStatement(query);
            pstm.setString(1, loaiPhong);
            pstm.setDate(2, ngayNhanPhong); // Sử dụng ngày nhận phòng cho tham số thứ hai
            pstm.setDate(3, ngayNhanPhong); // Sử dụng ngày nhận phòng cho tham số thứ ba

            ResultSet rs = pstm.executeQuery();

            while (rs.next()) {
                Room room = new Room();
                room.setId(rs.getString("roomID"));
                room.setNumber(rs.getInt("number"));
                room.setLoaiPhong(rs.getString("loaiPhong"));
                room.setPrice(rs.getInt("price"));
                room.setStatus(rs.getInt("status"));
                // Bạn có thể thêm logic xử lý trạng thái phòng (status) nếu cần
                listRoom.add(room);
            }
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return listRoom;
    }
    public boolean updateStatus(String roomId, int newStatus) {
        con=JDBC.getConnection();
        String query = "UPDATE room SET status = ? WHERE roomID = ?";
        try {
            PreparedStatement pstm = con.prepareStatement(query);
            pstm.setInt(1, newStatus);
            pstm.setString(2, roomId);

            int rowsAffected = pstm.executeUpdate();
            con.close();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}