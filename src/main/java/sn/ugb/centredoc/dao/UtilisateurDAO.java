package sn.ugb.centredoc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import sn.ugb.centredoc.model.Administrateur;
import sn.ugb.centredoc.model.Etudiant;
import sn.ugb.centredoc.model.Gestionnaire;
import sn.ugb.centredoc.model.Utilisateur;
import sn.ugb.centredoc.model.enums.Role;

public class UtilisateurDAO {

    public void ajouter(Utilisateur u, String motDePasseHache) throws SQLException {
        String sql = "INSERT INTO utilisateurs (nom, prenom, email, role, ufr, code_etudiant, mot_de_passe) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, u.getNom());
            ps.setString(2, u.getPrenom());
            ps.setString(3, u.getEmail());
            ps.setString(4, u.getRole().name());

            if (u instanceof Gestionnaire g && g.getIdUfr() != null) {
                ps.setInt(5, g.getIdUfr());
            } else {
                ps.setNull(5, Types.INTEGER);
            }

            if (u instanceof Etudiant e) {
                ps.setString(6, e.getCodeEtudiant());
            } else {
                ps.setNull(6, Types.VARCHAR);
            }

            if (motDePasseHache != null) {
                ps.setString(7, motDePasseHache);
            } else {
                ps.setNull(7, Types.VARCHAR);
            }

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    u.setIdUtilisateur(keys.getInt(1));
                }
            }
        }
    }

    public void modifier(Utilisateur u) throws SQLException {
        String sql = "UPDATE utilisateurs SET nom = ?, prenom = ?, email = ?, ufr = ?, code_etudiant = ? "
                   + "WHERE id_utilisateur = ?";
        try (Connection conn = ConnexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, u.getNom());
            ps.setString(2, u.getPrenom());
            ps.setString(3, u.getEmail());

            if (u instanceof Gestionnaire g && g.getIdUfr() != null) {
                ps.setInt(4, g.getIdUfr());
            } else {
                ps.setNull(4, Types.INTEGER);
            }

            if (u instanceof Etudiant e) {
                ps.setString(5, e.getCodeEtudiant());
            } else {
                ps.setNull(5, Types.VARCHAR);
            }

            ps.setInt(6, u.getIdUtilisateur());
            ps.executeUpdate();
        }
    }

    public void supprimer(int idUtilisateur) throws SQLException {
        String sql = "DELETE FROM utilisateurs WHERE id_utilisateur = ?";
        try (Connection conn = ConnexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUtilisateur);
            ps.executeUpdate();
        }
    }

    public Utilisateur trouverParId(int idUtilisateur) throws SQLException {
        String sql = "SELECT * FROM utilisateurs WHERE id_utilisateur = ?";
        try (Connection conn = ConnexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUtilisateur);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapResultSetToUtilisateur(rs) : null;
            }
        }
    }

    public Utilisateur trouverParEmail(String email) throws SQLException {
        String sql = "SELECT * FROM utilisateurs WHERE email = ?";
        try (Connection conn = ConnexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapResultSetToUtilisateur(rs) : null;
            }
        }
    }

    public String trouverMotDePasseHacheParEmail(String email) throws SQLException {
        String sql = "SELECT mot_de_passe FROM utilisateurs WHERE email = ?";
        try (Connection conn = ConnexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("mot_de_passe") : null;
            }
        }
    }

    public Utilisateur trouverEtudiantParEmailEtCode(String email, String codeEtudiant) throws SQLException {
        String sql = "SELECT * FROM utilisateurs WHERE email = ? AND code_etudiant = ? AND role = 'ETUDIANT'";
        try (Connection conn = ConnexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, codeEtudiant);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapResultSetToUtilisateur(rs) : null;
            }
        }
    }

    public List<Utilisateur> rechercherParNomOuEmail(String motCle) throws SQLException {
        List<Utilisateur> resultats = new ArrayList<>();
        String sql = "SELECT * FROM utilisateurs WHERE nom LIKE ? OR prenom LIKE ? OR email LIKE ?";
        try (Connection conn = ConnexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String recherche = "%" + motCle + "%";
            ps.setString(1, recherche);
            ps.setString(2, recherche);
            ps.setString(3, recherche);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultats.add(mapResultSetToUtilisateur(rs));
                }
            }
        }
        return resultats;
    }

    public List<Utilisateur> listerParRole(Role role) throws SQLException {
        List<Utilisateur> resultats = new ArrayList<>();
        String sql = "SELECT * FROM utilisateurs WHERE role = ?";
        try (Connection conn = ConnexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, role.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultats.add(mapResultSetToUtilisateur(rs));
                }
            }
        }
        return resultats;
    }

    private Utilisateur mapResultSetToUtilisateur(ResultSet rs) throws SQLException {
        Role role = Role.valueOf(rs.getString("role"));
        String nom = rs.getString("nom");
        String prenom = rs.getString("prenom");
        String email = rs.getString("email");

        Utilisateur u = switch (role) {
            case ADMIN -> new Administrateur(nom, prenom, email);
            case GESTIONNAIRE -> {
                int idUfr = rs.getInt("ufr");
                Integer idUfrObj = rs.wasNull() ? null : idUfr;
                yield new Gestionnaire(nom, prenom, email, idUfrObj);
            }
            case ETUDIANT -> new Etudiant(nom, prenom, email, rs.getString("code_etudiant"));
        };

        u.setIdUtilisateur(rs.getInt("id_utilisateur"));
        return u;
    }
    public Utilisateur trouverGestionnaireParUfr(int idUfr) throws SQLException {
    String sql = "SELECT * FROM utilisateurs WHERE ufr = ? AND role = 'GESTIONNAIRE'";
    try (Connection conn = ConnexionBD.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, idUfr);
        try (ResultSet rs = ps.executeQuery()) {
            return rs.next() ? mapResultSetToUtilisateur(rs) : null;
        }
    }
}
}