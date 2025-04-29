package Dao.Employee;

import Model.type_room;
import Util.JDBC;

import java.sql.*;
import java.util.ArrayList;

public class Type_roomDao implements DaoInterface<type_room> {
    Connection con;

    public Type_roomDao() {
        con = JDBC.getConnection();
    }

    @Override
    public boolean insert(type_room typeRoom) {
        String sql = "INSERT INTO type_room (loaiPhong, nameLoaiPhong, soGiuong, moTa, sucChua, kichThuoc) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, typeRoom.getLoaiPhong());
            ps.setString(2, typeRoom.getNameLoaiPhong());
            ps.setString(3, typeRoom.getSoGiuong());
            ps.setString(4, typeRoom.getMoTa());
            ps.setInt(5, typeRoom.getSucChua());
            ps.setDouble(6, typeRoom.getKichThuoc());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean update(type_room typeRoom) {
        String sql = "UPDATE type_room SET nameLoaiPhong=?, soGiuong=?, moTa=?, sucChua=?, kichThuoc=? WHERE loaiPhong=?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, typeRoom.getNameLoaiPhong());
            ps.setString(2, typeRoom.getSoGiuong());
            ps.setString(3, typeRoom.getMoTa());
            ps.setInt(4, typeRoom.getSucChua());
            ps.setDouble(5, typeRoom.getKichThuoc());
            ps.setString(6, typeRoom.getLoaiPhong());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(String loaiPhong) {
        String sql = "DELETE FROM type_room WHERE loaiPhong = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, loaiPhong);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public ArrayList<type_room> selectAll() {
        ArrayList<type_room> list = new ArrayList<>();
        String sql = "SELECT * FROM type_room";
        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                type_room typeRoom = new type_room(
                        rs.getString("loaiPhong"),
                        rs.getString("nameLoaiPhong"),
                        rs.getString("soGiuong"),
                        rs.getString("moTa"),
                        rs.getInt("sucChua"),
                        rs.getDouble("kichThuoc")
                );
                list.add(typeRoom);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
