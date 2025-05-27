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
            ps.setInt(3, typeRoom.getSoGiuong());
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
            ps.setInt(2, typeRoom.getSoGiuong());
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
                        rs.getInt("soGiuong"),
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
    public ArrayList<String> timKiemLoaiPhong(int bed, int sucChua){
        ArrayList<String> listLoaiPhong=new ArrayList<>();
        con=JDBC.getConnection();
        String query="Select loaiPhong from room_type where soGiuong=? and sucChua=?";
        try{
            PreparedStatement pstm=con.prepareStatement(query);
            pstm.setInt(1, bed);
            pstm.setInt(2, sucChua);
            ResultSet rs=pstm.executeQuery();
            while(rs.next()){
                String loaiPhong=rs.getString("loaiPhong");
                listLoaiPhong.add(loaiPhong);
            }
            con.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return listLoaiPhong;
    }
    public type_room timKiem(String loaiPhong){
        con=JDBC.getConnection();
        String query="Select * from room_type where loaiPhong=?";
        try{
            PreparedStatement pstm=con.prepareStatement(query);
            pstm.setString(1, loaiPhong);
            ResultSet rs=pstm.executeQuery();
            if(rs.next()){
                type_room tRoom=new type_room();
                String nameLoaiPhong=rs.getString("tenLoaiPhong");
                int bed=rs.getInt("soGiuong");
                int sucChua=rs.getInt("sucChua");
                double kichThuoc=rs.getDouble("kichThuoc");
                String moTa=rs.getString("moTa");
                tRoom.setLoaiPhong(loaiPhong);
                tRoom.setNameLoaiPhong(nameLoaiPhong);
                tRoom.setSoGiuong(bed);
                tRoom.setSucChua(sucChua);
                tRoom.setKichThuoc(kichThuoc);
                tRoom.setMoTa(moTa);
                con.close();
                return tRoom;
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public int getPrice(String loaiPhong){
        con = JDBC.getConnection();
        String query = "SELECT price FROM room_type WHERE loaiPhong =?";
        try {
            PreparedStatement pstm = con.prepareStatement(query);
            pstm.setString(1, loaiPhong);
            ResultSet rs = pstm.executeQuery();
            if (rs.next()) {
                return rs.getInt("price");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
