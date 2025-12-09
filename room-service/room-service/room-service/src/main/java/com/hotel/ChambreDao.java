package com.hotel.room;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChambreDao {

    public List<Chambre> findAll() {
        List<Chambre> list = new ArrayList<>();
        String sql = "SELECT num, type, prix, etat FROM chambre ORDER BY num";

        try (Connection con = DbConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Chambre c = new Chambre(
                        rs.getInt("num"),
                        rs.getString("type"),
                        rs.getDouble("prix"),
                        rs.getBoolean("etat")
                );
                list.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Chambre findByNum(int num) {
        String sql = "SELECT num, type, prix, etat FROM chambre WHERE num = ?";
        try (Connection con = DbConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, num);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Chambre(
                            rs.getInt("num"),
                            rs.getString("type"),
                            rs.getDouble("prix"),
                            rs.getBoolean("etat")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean insert(Chambre chambre) {
        String sql = "INSERT INTO chambre (type, prix, etat) VALUES (?, ?, ?)";

        try (Connection con = DbConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, chambre.getType());
            ps.setDouble(2, chambre.getPrix());
            ps.setBoolean(3, chambre.isEtat());
            int rows = ps.executeUpdate();

            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        chambre.setNum(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(Chambre chambre) {
        String sql = "UPDATE chambre SET type = ?, prix = ?, etat = ? WHERE num = ?";

        try (Connection con = DbConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, chambre.getType());
            ps.setDouble(2, chambre.getPrix());
            ps.setBoolean(3, chambre.isEtat());
            ps.setInt(4, chambre.getNum());

            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int num) {
        String sql = "DELETE FROM chambre WHERE num = ?";
        try (Connection con = DbConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, num);
            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Chambre> findAvailable() {
        List<Chambre> list = new ArrayList<>();
        String sql = "SELECT num, type, prix, etat FROM chambre WHERE etat = TRUE ORDER BY num";

        try (Connection con = DbConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Chambre c = new Chambre(
                        rs.getInt("num"),
                        rs.getString("type"),
                        rs.getDouble("prix"),
                        rs.getBoolean("etat")
                );
                list.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Chambre> findByType(String type) {
        List<Chambre> list = new ArrayList<>();
        String sql = "SELECT num, type, prix, etat FROM chambre WHERE type = ? ORDER BY num";

        try (Connection con = DbConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, type);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Chambre c = new Chambre(
                            rs.getInt("num"),
                            rs.getString("type"),
                            rs.getDouble("prix"),
                            rs.getBoolean("etat")
                    );
                    list.add(c);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
