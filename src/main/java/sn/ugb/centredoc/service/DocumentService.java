package sn.ugb.centredoc.service;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import sn.ugb.centredoc.dao.DocumentDAO;
import sn.ugb.centredoc.dao.TelechargementDAO;
import sn.ugb.centredoc.exception.AccesRefuseException;
import sn.ugb.centredoc.exception.ChampInvalideException;
import sn.ugb.centredoc.exception.DocumentIntrouvableException;
import sn.ugb.centredoc.exception.DoublonException;
import sn.ugb.centredoc.model.Document;
import sn.ugb.centredoc.model.Gestionnaire;
import sn.ugb.centredoc.model.Telechargement;
import sn.ugb.centredoc.model.enums.NiveauAcces;
import sn.ugb.centredoc.util.Validateur;

public class DocumentService {

    private final DocumentDAO documentDAO;
    private final TelechargementDAO telechargementDAO;

    public DocumentService() {
        this.documentDAO = new DocumentDAO();
        this.telechargementDAO = new TelechargementDAO();
    }

    /**
     * Ajout d'un document par un gestionnaire.
     * Regle metier n4 : le gestionnaire ne gere que les documents de son UFR
     * -> on force l'UFR du document a celui du gestionnaire connecte.
     */
    public void ajouter(Document doc, Gestionnaire gestionnaire)
            throws ChampInvalideException, DoublonException, SQLException {

        validerChamps(doc);
        doc.setIdUfr(gestionnaire.getIdUfr());

        Document existant = documentDAO.trouverDoublon(doc.getTitre(), doc.getAuteur(), doc.getAnnee());
        if (existant != null) {
            throw new DoublonException(
                "Un document du meme titre, auteur et annee existe deja (id " + existant.getIdDocument() + ")");
        }

        documentDAO.ajouter(doc);
    }

    public void modifier(Document doc, Gestionnaire gestionnaire)
            throws ChampInvalideException, DocumentIntrouvableException, AccesRefuseException, SQLException {

        validerChamps(doc);
        Document existant = trouverOuLeverException(doc.getIdDocument());
        verifierAppartenanceUfr(existant, gestionnaire);

        doc.setIdUfr(gestionnaire.getIdUfr());
        documentDAO.modifier(doc);
    }

    public void supprimer(int idDocument, Gestionnaire gestionnaire)
            throws DocumentIntrouvableException, AccesRefuseException, SQLException {

        Document existant = trouverOuLeverException(idDocument);
        verifierAppartenanceUfr(existant, gestionnaire);
        documentDAO.supprimer(idDocument);
    }

    public List<Document> listerParUfr(int idUfr) throws SQLException {
        return documentDAO.listerParUfr(idUfr);
    }

    public List<Document> listerTous() throws SQLException {
        return documentDAO.listerTous();
    }

    /**
     * Recherche pour le module Etudiant (titre, mot-cle, annee, UFR, discipline).
     */
    public List<Document> rechercher(String motCle, String annee, String idUfr, String discipline) throws SQLException {
        return documentDAO.rechercher(motCle, annee, idUfr, discipline);
    }

    public Document trouverOuLeverException(int idDocument) throws DocumentIntrouvableException, SQLException {
        Document doc = documentDAO.trouverParId(idDocument);
        if (doc == null) {
            throw new DocumentIntrouvableException("Aucun document trouve avec l'id " + idDocument);
        }
        return doc;
    }

    /**
     * Regles metier n1, n2, n3 : verifie le niveau d'acces avant de telecharger,
     * puis enregistre le telechargement dans l'historique.
     */
    public void telecharger(int idDocument, int idUtilisateur) throws DocumentIntrouvableException, AccesRefuseException, SQLException {
        Document doc = trouverOuLeverException(idDocument);

        if (doc.getNiveauAcces() == NiveauAcces.RESTREINT) {
            throw new AccesRefuseException("Ce document est en acces restreint (embargo)");
        }
        if (doc.getNiveauAcces() == NiveauAcces.CONSULTATION_SEULE) {
            throw new AccesRefuseException("Ce document est en consultation seule, telechargement non autorise");
        }

        Telechargement t = new Telechargement(idUtilisateur, idDocument, LocalDateTime.now());
        telechargementDAO.enregistrer(t);
    }

    public List<Telechargement> historiqueUtilisateur(int idUtilisateur) throws SQLException {
        return telechargementDAO.listerParUtilisateur(idUtilisateur);
    }

    public List<Telechargement> historiqueDocument(int idDocument) throws SQLException {
        return telechargementDAO.listerParDocument(idDocument);
    }

    private void verifierAppartenanceUfr(Document doc, Gestionnaire gestionnaire) throws AccesRefuseException {
        if (gestionnaire.getIdUfr() == null || doc.getIdUfr() != gestionnaire.getIdUfr()) {
            throw new AccesRefuseException("Vous ne pouvez gerer que les documents de votre UFR");
        }
    }

    private void validerChamps(Document doc) throws ChampInvalideException {
        Validateur.validerChampNonVide(doc.getTitre(), "titre");
        Validateur.validerChampNonVide(doc.getAuteur(), "auteur");
        if (doc.getAnnee() <= 0) {
            throw new ChampInvalideException("L'annee doit etre valide");
        }
        if (doc.getType() == null) {
            throw new ChampInvalideException("Le type (THESE / MEMOIRE) est obligatoire");
        }
        if (doc.getNiveauAcces() == null) {
            throw new ChampInvalideException("Le niveau d'acces est obligatoire");
        }
    }
}
