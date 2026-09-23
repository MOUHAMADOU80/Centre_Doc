package sn.ugb.centredoc.service;

import sn.ugb.centredoc.dao.DocumentDAO;
import sn.ugb.centredoc.dao.TelechargementDAO;
import sn.ugb.centredoc.exception.AccesRefuseException;
import sn.ugb.centredoc.exception.DocumentIntrouvableException;
import sn.ugb.centredoc.model.Document;
import sn.ugb.centredoc.model.Telechargement;
import sn.ugb.centredoc.model.Utilisateur;
import sn.ugb.centredoc.model.enums.NiveauAcces;
import sn.ugb.centredoc.model.enums.TypeDocument;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TelechargementService {

    private final DocumentDAO documentDAO = new DocumentDAO();
    private final TelechargementDAO telechargementDAO = new TelechargementDAO();

    /** Règles 1, 2 et 3 : seul TELECHARGEABLE autorise le téléchargement. */
    public void verifierAcces(Document doc) throws AccesRefuseException {
        NiveauAcces niveau = doc.getNiveauAcces();
        if (niveau == NiveauAcces.RESTREINT) {
            throw new AccesRefuseException(
                "Ce document est sous embargo : il n'est ni consultable ni téléchargeable.");
        }
        if (niveau == NiveauAcces.CONSULTATION_SEULE) {
            throw new AccesRefuseException(
                "Ce document est en consultation seule : le téléchargement du PDF n'est pas autorisé.");
        }
    }

    public Telechargement telecharger(Utilisateur utilisateur, int idDocument, File destination)
            throws DocumentIntrouvableException, AccesRefuseException, SQLException, IOException {

        Document doc = documentDAO.getParId(idDocument);
        verifierAcces(doc);

        Path source = Paths.get(doc.getCheminPdf());
        if (!Files.exists(source)) {
            throw new DocumentIntrouvableException(
                "Le fichier PDF de ce document est introuvable sur le serveur.");
        }

        Files.copy(source, destination.toPath(), StandardCopyOption.REPLACE_EXISTING);

        Telechargement t = new Telechargement(
            utilisateur.getIdUtilisateur(), idDocument, LocalDateTime.now());
        telechargementDAO.enregistrer(t);
        return t;
    }

    public List<Telechargement> historique(Utilisateur utilisateur) throws SQLException {
        return telechargementDAO.listerParUtilisateur(utilisateur.getIdUtilisateur());
    }

    /** Historique enrichi avec le titre et le type du document, pour l'affichage à l'écran. */
    public List<HistoriqueLigne> historiqueDetaille(Utilisateur utilisateur) throws SQLException {
        List<HistoriqueLigne> lignes = new ArrayList<>();
        for (Telechargement t : historique(utilisateur)) {
            try {
                Document d = documentDAO.getParId(t.getIdDocument());
                lignes.add(new HistoriqueLigne(
                    d.getIdDocument(), d.getTitre(), d.getType(), t.getDateTelechargement()));
            } catch (DocumentIntrouvableException e) {
                // Document supprimé depuis le téléchargement : on l'ignore dans l'affichage.
            }
        }
        return lignes;
    }

    /** Ligne d'historique affichable : identité du document et date, sans exposer le chemin PDF. */
    public static class HistoriqueLigne {
        private final int idDocument;
        private final String titreDocument;
        private final TypeDocument typeDocument;
        private final LocalDateTime dateTelechargement;

        public HistoriqueLigne(int idDocument, String titreDocument,
                               TypeDocument typeDocument, LocalDateTime dateTelechargement) {
            this.idDocument = idDocument;
            this.titreDocument = titreDocument;
            this.typeDocument = typeDocument;
            this.dateTelechargement = dateTelechargement;
        }

        public int getIdDocument() { return idDocument; }
        public String getTitreDocument() { return titreDocument; }
        public TypeDocument getTypeDocument() { return typeDocument; }
        public LocalDateTime getDateTelechargement() { return dateTelechargement; }
    }
}