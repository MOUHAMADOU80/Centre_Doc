package sn.ugb.centredoc.service;

import java.sql.SQLException;

import sn.ugb.centredoc.dao.UtilisateurDAO;
import sn.ugb.centredoc.exception.AuthentificationException;
import sn.ugb.centredoc.model.Utilisateur;
import sn.ugb.centredoc.util.HashUtil;

public class AuthService {

    private final UtilisateurDAO utilisateurDAO;

    public AuthService() {
        this.utilisateurDAO = new UtilisateurDAO();
    }

    public Utilisateur connecterAdminOuGestionnaire(String email, String motDePasse)
            throws AuthentificationException, SQLException {

        Utilisateur u = utilisateurDAO.trouverParEmail(email);
        if (u == null) {
            throw new AuthentificationException("Aucun compte ne correspond a cet email");
        }

        String motDePasseHache = utilisateurDAO.trouverMotDePasseHacheParEmail(email);
        if (motDePasseHache == null || !HashUtil.verifier(motDePasse, motDePasseHache)) {
            throw new AuthentificationException("Mot de passe incorrect");
        }

        return u;
    }

    public Utilisateur connecterEtudiant(String nom, String prenom, String email, String codeEtudiant)
            throws AuthentificationException, SQLException {

        Utilisateur u = utilisateurDAO.trouverEtudiantParEmailEtCode(email, codeEtudiant);

        if (u == null
                || !u.getNom().equalsIgnoreCase(nom)
                || !u.getPrenom().equalsIgnoreCase(prenom)) {
            throw new AuthentificationException("Aucun compte etudiant ne correspond a ces informations");
        }

        return u;
    }
}