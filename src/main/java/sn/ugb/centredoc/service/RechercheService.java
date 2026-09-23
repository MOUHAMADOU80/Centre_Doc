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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RechercheService {

    private static final int ANNEE_MIN = 1900;

    private final DocumentDAO documentDAO = new DocumentDAO();
    private final UfrDAO ufrDAO = new UfrDAO();

    /** Recherche multicritère. Un critère null ou vide est ignoré. */
    public List<Document> rechercher(String titre, String auteur, String motCle,
                                     Integer annee, Integer idUfr, String discipline)
            throws ChampInvalideException, SQLException {

        if (annee != null && annee < ANNEE_MIN) {
            throw new ChampInvalideException("L'année saisie est invalide : " + annee);
        }

        List<Document> trouves = documentDAO.rechercher(
                nettoyer(titre), nettoyer(auteur), nettoyer(motCle),
                annee, idUfr, nettoyer(discipline));

        List<Document> resultat = new ArrayList<>();
        for (Document d : trouves) {
            resultat.add(vueEtudiant(d));
        }
        return resultat;
    }

    /** Un seul texte libre : cherché dans le titre OU l'auteur OU les mots-clés. */
    public List<Document> rechercherLibre(String texte, Integer annee, Integer idUfr, String discipline)
            throws ChampInvalideException, SQLException {

        String t = nettoyer(texte);
        if (t == null) {
            return rechercher(null, null, null, annee, idUfr, discipline);
        }

        Map<Integer, Document> fusion = new LinkedHashMap<>();
        for (Document d : rechercher(t, null, null, annee, idUfr, discipline)) {
            fusion.put(d.getIdDocument(), d);
        }
        for (Document d : rechercher(null, t, null, annee, idUfr, discipline)) {
            fusion.putIfAbsent(d.getIdDocument(), d);
        }
        for (Document d : rechercher(null, null, t, annee, idUfr, discipline)) {
            fusion.putIfAbsent(d.getIdDocument(), d);
        }
        return new ArrayList<>(fusion.values());
    }

    /** Fiche détaillée : refusée si le document est sous embargo (règle 3). */
    public Document consulter(int idDocument)
            throws DocumentIntrouvableException, AccesRefuseException, SQLException {

        Document d = documentDAO.getParId(idDocument);
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