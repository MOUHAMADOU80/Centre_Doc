package sn.ugb.centredoc.service;

import java.sql.SQLException;
import java.util.List;

import sn.ugb.centredoc.dao.UtilisateurDAO;
import sn.ugb.centredoc.exception.ChampInvalideException;
import sn.ugb.centredoc.exception.DoublonException;
import sn.ugb.centredoc.model.Gestionnaire;
import sn.ugb.centredoc.model.Utilisateur;
import sn.ugb.centredoc.model.enums.Role;
import sn.ugb.centredoc.util.HashUtil;
import sn.ugb.centredoc.util.Validateur;

public class UtilisateurService {

    private final UtilisateurDAO utilisateurDAO;

    public UtilisateurService() {
        this.utilisateurDAO = new UtilisateurDAO();
    }

    public void creerGestionnaire(Gestionnaire gestionnaire, String motDePasse)
            throws ChampInvalideException, DoublonException, SQLException {

        Validateur.validerChampNonVide(gestionnaire.getNom(), "nom");
        Validateur.validerChampNonVide(gestionnaire.getPrenom(), "prenom");
        Validateur.validerEmailUgb(gestionnaire.getEmail());
        Validateur.validerChampNonVide(motDePasse, "mot de passe");

        if (utilisateurDAO.trouverParEmail(gestionnaire.getEmail()) != null) {
            throw new DoublonException("L'email " + gestionnaire.getEmail() + " est deja utilise");
        }

        String motDePasseHache = HashUtil.hacher(motDePasse);
        utilisateurDAO.ajouter(gestionnaire, motDePasseHache);
    }

    public void modifierGestionnaire(Gestionnaire gestionnaire)
            throws ChampInvalideException, SQLException {

        Validateur.validerChampNonVide(gestionnaire.getNom(), "nom");
        Validateur.validerChampNonVide(gestionnaire.getPrenom(), "prenom");
        Validateur.validerEmailUgb(gestionnaire.getEmail());

        utilisateurDAO.modifier(gestionnaire);
    }

    public void affecterUfr(Gestionnaire gestionnaire, int idUfr) throws SQLException {
        gestionnaire.setIdUfr(idUfr);
        utilisateurDAO.modifier(gestionnaire);
    }

    public void supprimer(int idUtilisateur) throws SQLException {
        utilisateurDAO.supprimer(idUtilisateur);
    }

    public List<Utilisateur> listerGestionnaires() throws SQLException {
        return utilisateurDAO.listerParRole(Role.GESTIONNAIRE);
    }

    public List<Utilisateur> rechercherParNomOuEmail(String motCle) throws SQLException {
        return utilisateurDAO.rechercherParNomOuEmail(motCle);
    }
}