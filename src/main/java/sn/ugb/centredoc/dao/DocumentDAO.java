package sn.ugb.centredoc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import sn.ugb.centredoc.model.Document;
import sn.ugb.centredoc.model.enums.NiveauAcces;
import sn.ugb.centredoc.model.enums.TypeDocument;

public class DocumentDAO {

    public void ajouter(Document doc) throws SQLException {
        String sql = "INSERT INTO documents (titre, auteur, encadrant, annee, type, ufr, discipline, "
                   + "resume, mots_cles, chemin_pdf, niveau_acces) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, doc.getTitre());
            ps.setString(2, doc.getAuteur());
            ps.setString(3, doc.getEncadrant());
            ps.setInt(4, doc.getAnnee());
            ps.setString(5, doc.getType().name());
            ps.setInt(6, doc.getIdUfr());
            ps.setString(7, doc.getDiscipline());
            ps.setString(8, doc.getResume());
            ps.setString(9, doc.getMotsCles());
            ps.setString(10, doc.getCheminPdf());
            ps.setString(11, doc.getNiveauAcces().name());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    doc.setIdDocument(keys.getInt(1));
                }
            }
        }
    }

    public void modifier(Document doc) throws SQLException {
        String sql = "UPDATE documents SET titre = ?, auteur = ?, encadrant = ?, annee = ?, type = ?, "
                   + "ufr = ?, discipline = ?, resume = ?, mots_cles = ?, chemin_pdf = ?, niveau_acces = ? "
                   + "WHERE id_document = ?";
        try (Connection conn = ConnexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, doc.getTitre());
            ps.setString(2, doc.getAuteur());
            ps.setString(3, doc.getEncadrant());
            ps.setInt(4, doc.getAnnee());
            ps.setString(5, doc.getType().name());
            ps.setInt(6, doc.getIdUfr());
            ps.setString(7, doc.getDiscipline());
            ps.setString(8, doc.getResume());
            ps.setString(9, doc.getMotsCles());
            ps.setString(10, doc.getCheminPdf());
            ps.setString(11, doc.getNiveauAcces().name());
            ps.setInt(12, doc.getIdDocument());

            ps.executeUpdate();
        }
    }

    public void supprimer(int idDocument) throws SQLException {
        String sql = "DELETE FROM documents WHERE id_document = ?";
        try (Connection conn = ConnexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idDocument);
            ps.executeUpdate();
        }
    }

    public Document trouverParId(int idDocument) throws SQLException {
        String sql = "SELECT * FROM documents WHERE id_document = ?";
        try (Connection conn = ConnexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idDocument);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapResultSetToDocument(rs) : null;
            }
        }
    }

    /**
     * Recherche pour verifier un doublon : meme titre + meme auteur + meme annee.
     */
    public Document trouverDoublon(String titre, String auteur, int annee) throws SQLException {
        String sql = "SELECT * FROM documents WHERE titre = ? AND auteur = ? AND annee = ?";
        try (Connection conn = ConnexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, titre);
            ps.setString(2, auteur);
            ps.setInt(3, annee);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapResultSetToDocument(rs) : null;
            }
        }
    }

    public List<Document> listerTous() throws SQLException {
        List<Document> resultats = new ArrayList<>();
        String sql = "SELECT * FROM documents ORDER BY titre";
        try (Connection conn = ConnexionBD.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                resultats.add(mapResultSetToDocument(rs));
            }
        }
        return resultats;
    }

    public List<Document> listerParUfr(int idUfr) throws SQLException {
        List<Document> resultats = new ArrayList<>();
        String sql = "SELECT * FROM documents WHERE ufr = ? ORDER BY titre";
        try (Connection conn = ConnexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUfr);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultats.add(mapResultSetToDocument(rs));
                }
            }
        }
        return resultats;
    }

    /**
     * Recherche multi-criteres pour le module Etudiant.
     * Chaque critere est optionnel (null ou vide = ignore).
     */
    public List<Document> rechercher(String motCle, String annee, String idUfr, String discipline) throws SQLException {
        List<Document> resultats = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT * FROM documents WHERE (titre LIKE ? OR auteur LIKE ? OR mots_cles LIKE ?)");

        if (annee != null && !annee.isBlank()) {
            sql.append(" AND annee = ?");
        }
        if (idUfr != null && !idUfr.isBlank()) {
            sql.append(" AND ufr = ?");
        }
        if (discipline != null && !discipline.isBlank()) {
            sql.append(" AND discipline = ?");
        }
        sql.append(" ORDER BY titre");

        try (Connection conn = ConnexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            String recherche = "%" + (motCle == null ? "" : motCle) + "%";
            int index = 1;
            ps.setString(index++, recherche);
            ps.setString(index++, recherche);
            ps.setString(index++, recherche);

            if (annee != null && !annee.isBlank()) {
                ps.setInt(index++, Integer.parseInt(annee));
            }
            if (idUfr != null && !idUfr.isBlank()) {
                ps.setInt(index++, Integer.parseInt(idUfr));
            }
            if (discipline != null && !discipline.isBlank()) {
                ps.setString(index++, discipline);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultats.add(mapResultSetToDocument(rs));
                }
            }
        }
        return resultats;
    }

    public long compterTous() throws SQLException {
        String sql = "SELECT COUNT(*) FROM documents";
        try (Connection conn = ConnexionBD.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            return rs.next() ? rs.getLong(1) : 0L;
        }
    }

    private Document mapResultSetToDocument(ResultSet rs) throws SQLException {
        Document doc = new Document();
        doc.setIdDocument(rs.getInt("id_document"));
        doc.setTitre(rs.getString("titre"));
        doc.setAuteur(rs.getString("auteur"));
        doc.setEncadrant(rs.getString("encadrant"));
        doc.setAnnee(rs.getInt("annee"));
        doc.setType(TypeDocument.valueOf(rs.getString("type")));
        doc.setIdUfr(rs.getInt("ufr"));
        doc.setDiscipline(rs.getString("discipline"));
        doc.setResume(rs.getString("resume"));
        doc.setMotsCles(rs.getString("mots_cles"));
        doc.setCheminPdf(rs.getString("chemin_pdf"));
        doc.setNiveauAcces(NiveauAcces.valueOf(rs.getString("niveau_acces")));
        return doc;
    }
}
