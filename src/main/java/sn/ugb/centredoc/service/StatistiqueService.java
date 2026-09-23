package sn.ugb.centredoc.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import sn.ugb.centredoc.dao.DocumentDAO;
import sn.ugb.centredoc.dao.TelechargementDAO;
import sn.ugb.centredoc.dao.UfrDAO;
import sn.ugb.centredoc.dao.UtilisateurDAO;
import sn.ugb.centredoc.model.Document;
import sn.ugb.centredoc.model.Ufr;
import sn.ugb.centredoc.model.enums.NiveauAcces;
import sn.ugb.centredoc.model.enums.Role;

public class StatistiqueService {

    private final DocumentDAO documentDAO;
    private final TelechargementDAO telechargementDAO;
    private final UfrDAO ufrDAO;
    private final UtilisateurDAO utilisateurDAO;

    public StatistiqueService() {
        this.documentDAO = new DocumentDAO();
        this.telechargementDAO = new TelechargementDAO();
        this.ufrDAO = new UfrDAO();
        this.utilisateurDAO = new UtilisateurDAO();
    }

    /** Repartition du fonds par discipline. */
    public Map<String, Long> repartitionParDiscipline() throws SQLException {
        Map<String, Long> repartition = new HashMap<>();
        for (Document doc : documentDAO.listerTous()) {
            String discipline = doc.getDiscipline() == null ? "Non renseignee" : doc.getDiscipline();
            repartition.merge(discipline, 1L, Long::sum);
        }
        return repartition;
    }

    /** Repartition du fonds par annee. */
    public Map<Integer, Long> repartitionParAnnee() throws SQLException {
        Map<Integer, Long> repartition = new HashMap<>();
        for (Document doc : documentDAO.listerTous()) {
            repartition.merge(doc.getAnnee(), 1L, Long::sum);
        }
        return repartition;
    }

    /** Activite (nombre de documents) par UFR, avec le nom de l'UFR en cle. */
    public Map<String, Long> activiteParUfr() throws SQLException {
        Map<Integer, String> nomsUfr = new HashMap<>();
        for (Ufr ufr : ufrDAO.listerTous()) {
            nomsUfr.put(ufr.getIdUfr(), ufr.getNom());
        }

        Map<String, Long> activite = new HashMap<>();
        for (Document doc : documentDAO.listerTous()) {
            String nomUfr = nomsUfr.getOrDefault(doc.getIdUfr(), "UFR inconnue");
            activite.merge(nomUfr, 1L, Long::sum);
        }
        return activite;
    }

    /** Liste des documents en acces restreint (embargo). */
    public List<Document> documentsRestreints() throws SQLException {
        List<Document> restreints = new ArrayList<>();
        for (Document doc : documentDAO.listerTous()) {
            if (doc.getNiveauAcces() == NiveauAcces.RESTREINT) {
                restreints.add(doc);
            }
        }
        return restreints;
    }

    /**
     * Top N des documents les plus telecharges.
     * Cle = document, valeur = nombre de telechargements. Ordre conserve (LinkedHashMap).
     */
    public Map<Document, Long> topDocumentsTelecharges(int n) throws SQLException {
        Map<Document, Long> top = new LinkedHashMap<>();

        List<Object[]> comptes = telechargementDAO.compterParDocument(); // deja trie DESC par le DAO
        int limite = Math.min(n, comptes.size());

        for (int i = 0; i < limite; i++) {
            int idDocument = (int) comptes.get(i)[0];
            long nb = (long) comptes.get(i)[1];
            Document doc = documentDAO.trouverParId(idDocument);
            if (doc != null) {
                top.put(doc, nb);
            }
        }
        return top;
    }

    /** Statistiques globales pour le tableau de bord (utilisateurs, documents, telechargements). */
    public Map<String, Long> statistiquesGlobales() throws SQLException {
        long nombreUtilisateurs = 0;
        for (Role role : Role.values()) {
            nombreUtilisateurs += utilisateurDAO.listerParRole(role).size();
        }

        Map<String, Long> stats = new LinkedHashMap<>();
        stats.put("nombreUtilisateurs", nombreUtilisateurs);
        stats.put("nombreDocuments", documentDAO.compterTous());
        stats.put("nombreTelechargements", telechargementDAO.compterTous());
        return stats;
    }
}
