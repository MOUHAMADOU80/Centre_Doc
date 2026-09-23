package sn.ugb.centredoc.dao;

import sn.ugb.centredoc.model.Ufr;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UfrDAO {

    public void ajouter(Ufr ufr) throws SQLException {
        String sql = "INSERT INTO ufr (nom) VALUES (?)";
        try (Connection conn = ConnexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, ufr.getNom());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    ufr.setIdUfr(keys.getInt(1));
                }
            }
        }
    }

    public void modifier(Ufr ufr) throws SQLException {
        String sql = "UPDATE ufr SET nom = ? WHERE id_ufr = ?";
        try (Connection conn = ConnexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ufr.getNom());
            ps.setInt(2, ufr.getIdUfr());
            ps.executeUpdate();
        }
    }

    public void supprimer(int idUfr) throws SQLException {
        String sql = "DELETE FROM ufr WHERE id_ufr = ?";
        try (Connection conn = ConnexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUfr);
            ps.executeUpdate();
        }
    }

    public Ufr trouverParId(int idUfr) throws SQLException {
        String sql = "SELECT * FROM ufr WHERE id_ufr = ?";
        try (Connection conn = ConnexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUfr);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUfr(rs);
                }
                return null;
            }
        }
    }

    public List<Ufr> listerTous() throws SQLException {
        List<Ufr> resultats = new ArrayList<>();
        String sql = "SELECT * FROM ufr ORDER BY nom";
        try (Connection conn = ConnexionBD.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                resultats.add(mapResultSetToUfr(rs));
            }
        }
        return resultats;
    }

    private Ufr mapResultSetToUfr(ResultSet rs) throws SQLException {
        Ufr ufr = new Ufr();
        ufr.setIdUfr(rs.getInt("id_ufr"));
        ufr.setNom(rs.getString("nom"));
        return ufr;
    }
}