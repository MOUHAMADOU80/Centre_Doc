package sn.ugb.centredoc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import sn.ugb.centredoc.model.Telechargement;

public class TelechargementDAO {

    public void enregistrer(Telechargement t) throws SQLException {
        String sql = "INSERT INTO telechargements (utilisateur, document, date_telechargement) VALUES (?, ?, ?)";
        try (Connection conn = ConnexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, t.getIdUtilisateur());
            ps.setInt(2, t.getIdDocument());
            ps.setTimestamp(3, Timestamp.valueOf(t.getDateTelechargement()));

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    t.setIdTelechargement(keys.getInt(1));
                }
            }
        }
    }

    public List<Telechargement> listerParUtilisateur(int idUtilisateur) throws SQLException {
        List<Telechargement> resultats = new ArrayList<>();
        String sql = "SELECT * FROM telechargements WHERE utilisateur = ? ORDER BY date_telechargement DESC";
        try (Connection conn = ConnexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUtilisateur);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultats.add(mapResultSetToTelechargement(rs));
                }
            }
        }
        return resultats;
    }

    public List<Telechargement> listerParDocument(int idDocument) throws SQLException {
        List<Telechargement> resultats = new ArrayList<>();
        String sql = "SELECT * FROM telechargements WHERE document = ? ORDER BY date_telechargement DESC";
        try (Connection conn = ConnexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idDocument);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultats.add(mapResultSetToTelechargement(rs));
                }
            }
        }
        return resultats;
    }

    public List<Telechargement> listerTous() throws SQLException {
        List<Telechargement> resultats = new ArrayList<>();
        String sql = "SELECT * FROM telechargements ORDER BY date_telechargement DESC";
        try (Connection conn = ConnexionBD.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                resultats.add(mapResultSetToTelechargement(rs));
            }
        }
        return resultats;
    }

    public long compterTous() throws SQLException {
        String sql = "SELECT COUNT(*) FROM telechargements";
        try (Connection conn = ConnexionBD.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            return rs.next() ? rs.getLong(1) : 0L;
        }
    }

    /**
     * Nombre de telechargements par document (id_document -> nombre).
     * Utilise pour le module Statistiques (top documents telecharges).
     */
    public List<Object[]> compterParDocument() throws SQLException {
        List<Object[]> resultats = new ArrayList<>();
        String sql = "SELECT document, COUNT(*) AS nb FROM telechargements GROUP BY document ORDER BY nb DESC";
        try (Connection conn = ConnexionBD.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                resultats.add(new Object[]{ rs.getInt("document"), rs.getLong("nb") });
            }
        }
        return resultats;
    }

    private Telechargement mapResultSetToTelechargement(ResultSet rs) throws SQLException {
        Telechargement t = new Telechargement();
        t.setIdTelechargement(rs.getInt("id_telechargement"));
        t.setIdUtilisateur(rs.getInt("utilisateur"));
        t.setIdDocument(rs.getInt("document"));
        Timestamp ts = rs.getTimestamp("date_telechargement");
        t.setDateTelechargement(ts != null ? ts.toLocalDateTime() : LocalDateTime.now());
        return t;
    }
}
