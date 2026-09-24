package sn.ugb.centredoc.service;

import sn.ugb.centredoc.dao.DocumentDAO;
import sn.ugb.centredoc.dao.UfrDAO;
import sn.ugb.centredoc.exception.AccesRefuseException;
import sn.ugb.centredoc.exception.ChampInvalideException;
import sn.ugb.centredoc.exception.DocumentIntrouvableException;
import sn.ugb.centredoc.model.Document;
import sn.ugb.centredoc.model.Ufr;
import sn.ugb.centredoc.model.enums.NiveauAcces;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RechercheService {

    private static final int ANNEE_MIN = 1900;

    private final DocumentDAO documentDAO = new DocumentDAO();
    private final UfrDAO ufrDAO = new UfrDAO();

    /**
     * Recherche : le mot-cle est cherche dans le titre, l'auteur ET les mots-cles (OU),
     * combine avec les filtres annee/UFR/discipline (ET). Un critere null ou vide est ignore.
     */
    public List<Document> rechercher(String motCle, Integer annee, Integer idUfr, String discipline)
            throws ChampInvalideException, SQLException {

        if (annee != null && annee < ANNEE_MIN) {
            throw new ChampInvalideException("L'année saisie est invalide : " + annee);
        }

        List<Document> trouves = documentDAO.rechercher(
                nettoyer(motCle),
                annee == null ? null : String.valueOf(annee),
                idUfr == null ? null : String.valueOf(idUfr),
                nettoyer(discipline));

        List<Document> resultat = new ArrayList<>();
        for (Document d : trouves) {
            resultat.add(vueEtudiant(d));
        }
        return resultat;
    }

    /** Alias conserve pour compatibilite avec les controleurs : le DAO fait deja la recherche OU. */
    public List<Document> rechercherLibre(String texte, Integer annee, Integer idUfr, String discipline)
            throws ChampInvalideException, SQLException {
        return rechercher(texte, annee, idUfr, discipline);
    }

    /** Fiche détaillée : refusée si le document est sous embargo (règle 3). */
    public Document consulter(int idDocument)
            throws DocumentIntrouvableException, AccesRefuseException, SQLException {

        Document d = documentDAO.trouverParId(idDocument);
        if (d == null) {
            throw new DocumentIntrouvableException("Aucun document avec l'identifiant " + idDocument);
        }
        if (estSousEmbargo(d)) {
            throw new AccesRefuseException(
                "Ce document est sous embargo : il n'est pas consultable.");
        }
        return vueEtudiant(d);
    }

    /** Nom de l'UFR pour l'affichage de la fiche. */
    public String nomUfr(int idUfr) throws SQLException {
        for (Ufr u : ufrDAO.listerTous()) {
            if (u.getIdUfr() == idUfr) {
                return u.getNom();
            }
        }
        return "UFR n°" + idUfr;
    }

    public boolean estSousEmbargo(Document d) {
        return d.getNiveauAcces() == NiveauAcces.RESTREINT;
    }

    public boolean estTelechargeable(Document d) {
        return d.getNiveauAcces() == NiveauAcces.TELECHARGEABLE;
    }

    /**
     * Copie du document destinée à l'étudiant : le chemin du PDF n'est jamais exposé,
     * et un document sous embargo perd tout ce qui n'est pas son identification.
     */
    private Document vueEtudiant(Document source) {
        Document vue = new Document();
        vue.setIdDocument(source.getIdDocument());
        vue.setTitre(source.getTitre());
        vue.setAuteur(source.getAuteur());
        vue.setAnnee(source.getAnnee());
        vue.setType(source.getType());
        vue.setIdUfr(source.getIdUfr());
        vue.setDiscipline(source.getDiscipline());
        vue.setNiveauAcces(source.getNiveauAcces());

        if (!estSousEmbargo(source)) {
            vue.setEncadrant(source.getEncadrant());
            vue.setResume(source.getResume());
            vue.setMotsCles(source.getMotsCles());
        }
        return vue;
    }

    private String nettoyer(String s) {
        if (s == null) {
            return null;
        }
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}