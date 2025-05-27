package Dao.Employee;
import Model.Service;
import Util.JDBC;

import java.sql.*;
import java.util.ArrayList;

public class ServiceDao implements DaoInterface<Service> {

    @Override
    public boolean insert(Service service) {
        try {
            Connection con = JDBC.getConnection();
            String sql = "INSERT INTO service(serviceID, name, price, description) VALUES (?, ?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, service.getServiceId());
            pst.setString(2, service.getName());
            pst.setDouble(3, service.getPrice());
            pst.setString(4, service.getDescription());

            int rows = pst.executeUpdate();
            con.close();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Service service) {
        try {
            Connection con = JDBC.getConnection();
            String sql = "UPDATE service SET name=?, price=?, description=? WHERE serviceID=?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, service.getName());
            pst.setDouble(2, service.getPrice());
            pst.setString(3, service.getDescription());
            pst.setInt(4, service.getServiceId());

            int rows = pst.executeUpdate();
            con.close();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(String id) {
        try {
            Connection con = JDBC.getConnection();
            String sql = "DELETE FROM service WHERE serviceID=?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, id);

            int rows = pst.executeUpdate();
            con.close();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public ArrayList<Service> selectAll() {
        ArrayList<Service> list = new ArrayList<>();
        try {
            Connection con = JDBC.getConnection();
            String sql = "SELECT * FROM service";
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                Service s = new Service(
                        rs.getInt("serviceID"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getString("description")
                );
                list.add(s);
            }

            JDBC.closeConnection(con);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    public Service selectById(int id) {
        Service s = null;
        try {
            Connection con = JDBC.getConnection();
            String sql = "SELECT * FROM service WHERE serviceID=?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                s = new Service(
                        rs.getInt("serviceID"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getString("description")
                );
            }

           con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }
}
