package sn.ugb.centredoc.controller.etudiant;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import sn.ugb.centredoc.controller.etudiant.composants.Sidebar;
import sn.ugb.centredoc.controller.etudiant.composants.Topbar;
import sn.ugb.centredoc.exception.AccesRefuseException;
import sn.ugb.centredoc.exception.DocumentIntrouvableException;
import sn.ugb.centredoc.model.Document;
import sn.ugb.centredoc.model.Utilisateur;
import sn.ugb.centredoc.model.enums.NiveauAcces;
import sn.ugb.centredoc.model.enums.TypeDocument;
import sn.ugb.centredoc.service.RechercheService;
import sn.ugb.centredoc.service.TelechargementService;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class FicheDocumentController {

    /** Favoris : liste en mémoire seulement, non enregistrée en base (extra visuel, hors règles métier du sujet). */
    private static final Set<Integer> FAVORIS = new HashSet<>();

    @FXML private BorderPane racine;
    @FXML private Label labelTitre;
    @FXML private Label badgeDiscipline;
    @FXML private Label badgeUfrCourt;
    @FXML private Label labelDate;
    @FXML private Label labelTaille;
    @FXML private Label valTitre;
    @FXML private Label valType;
    @FXML private Label valUfr;
    @FXML private Label valAuteur;
    @FXML private Label valEncadrant;
    @FXML private Label valAnnee;
    @FXML private Label valDiscipline;
    @FXML private Label valMotsCles;
    @FXML private TextArea zoneResume;
    @FXML private Label badgeAcces;
    @FXML private Label labelInfoAcces;
    @FXML private Button boutonTelecharger;
    @FXML private Button boutonFavori;

    private final RechercheService rechercheService = new RechercheService();
    private final TelechargementService telechargementService = new TelechargementService();

    private Utilisateur utilisateur;
    private Document document;

    public void initialiser(Utilisateur utilisateur, int idDocument) {
        this.utilisateur = utilisateur;
        racine.setLeft(Sidebar.construire(racine, utilisateur, "documents"));
        racine.setTop(Topbar.construire(utilisateur, null));
        try {
            this.document = rechercheService.consulter(idDocument);
            afficher();
        } catch (AccesRefuseException e) {
            alerte(Alert.AlertType.INFORMATION, "🔒 Document sous embargo", e.getMessage());
            onRetour();
        } catch (DocumentIntrouvableException e) {
            alerte(Alert.AlertType.WARNING, "Document introuvable", e.getMessage());
            onRetour();
        } catch (SQLException e) {
            alerte(Alert.AlertType.ERROR, "Erreur base de données", e.getMessage());
            onRetour();
        }
    }

    private void afficher() {
        try {
            String nomUfr = rechercheService.nomUfr(document.getIdUfr());
            valUfr.setText(nomUfr);
            badgeUfrCourt.setText(nomUfr);
        } catch (SQLException e) {
            valUfr.setText("—");
            badgeUfrCourt.setText("—");
        }

        labelTitre.setText(document.getTitre());
        badgeDiscipline.setText(texte(document.getDiscipline()));
        labelDate.setText("Année " + document.getAnnee());
        labelTaille.setText(document.getType() == TypeDocument.THESE ? "Thèse" : "Mémoire");

        valTitre.setText(document.getTitre());
        valType.setText(document.getType() == TypeDocument.THESE ? "Thèse" : "Mémoire");
        valAuteur.setText(texte(document.getAuteur()));
        valEncadrant.setText(texte(document.getEncadrant()));
        valAnnee.setText(String.valueOf(document.getAnnee()));
        valDiscipline.setText(texte(document.getDiscipline()));
        valMotsCles.setText(texte(document.getMotsCles()));
        zoneResume.setText(document.getResume() == null || document.getResume().isBlank()
                ? "Aucun résumé disponible." : document.getResume());

        boolean favori = FAVORIS.contains(document.getIdDocument());
        boutonFavori.setText(favori ? "★ Retirer des favoris" : "☆ Ajouter à mes favoris");

        boolean accesPdf = document.getNiveauAcces() == NiveauAcces.TELECHARGEABLE;
        badgeAcces.getStyleClass().setAll("badge", accesPdf ? "badge-ok" : "badge-consult");
        if (accesPdf) {
            badgeAcces.setText("Téléchargeable");
            labelInfoAcces.setText("Vous pouvez télécharger ce document au format PDF.");
        } else {
            badgeAcces.setText("Consultation seule");
            labelInfoAcces.setText("Consultation seule : le résumé est accessible, "
                    + "mais le PDF ne peut être ni ouvert ni téléchargé.");
        }
        boutonTelecharger.setDisable(!accesPdf);
    }

    @FXML
    private void onTelecharger() {
        FileChooser choix = new FileChooser();
        choix.setTitle("Enregistrer le PDF");
        choix.setInitialFileName(nomFichier(document.getTitre()));
        choix.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichier PDF", "*.pdf"));
        File destination = choix.showSaveDialog(boutonTelecharger.getScene().getWindow());
        if (destination == null) {
            return;
        }
        try {
            telechargementService.telecharger(utilisateur, document.getIdDocument(), destination);
            alerte(Alert.AlertType.INFORMATION, "Téléchargement réussi",
                   "Le document « " + destination.getName() + " » a été téléchargé avec succès.");
        } catch (AccesRefuseException e) {
            alerte(Alert.AlertType.WARNING, "Téléchargement refusé", e.getMessage());
        } catch (DocumentIntrouvableException e) {
            alerte(Alert.AlertType.WARNING, "Fichier introuvable", e.getMessage());
        } catch (SQLException | IOException e) {
            alerte(Alert.AlertType.ERROR, "Erreur", e.getMessage());
        }
    }

    /** "Ouvrir" copie le PDF vers un fichier temporaire puis l'ouvre : soumis aux mêmes règles d'accès que Télécharger. */
    @FXML
    private void onOuvrir() {
        if (document.getNiveauAcces() != NiveauAcces.TELECHARGEABLE) {
            alerte(Alert.AlertType.WARNING, "Ouverture refusée",
                   "Ce document est en consultation seule : le PDF ne peut pas être ouvert.");
            return;
        }
        try {
            File temp = File.createTempFile("centredoc_", ".pdf");
            temp.deleteOnExit();
            telechargementService.telecharger(utilisateur, document.getIdDocument(), temp);
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(temp);
            } else {
                alerte(Alert.AlertType.INFORMATION, "Fichier prêt",
                       "Fichier enregistré temporairement : " + temp.getAbsolutePath());
            }
        } catch (AccesRefuseException e) {
            alerte(Alert.AlertType.WARNING, "Ouverture refusée", e.getMessage());
        } catch (DocumentIntrouvableException e) {
            alerte(Alert.AlertType.WARNING, "Fichier introuvable", e.getMessage());
        } catch (SQLException | IOException e) {
            alerte(Alert.AlertType.ERROR, "Erreur", e.getMessage());
        }
    }

    @FXML
    private void onFavori() {
        if (FAVORIS.contains(document.getIdDocument())) {
            FAVORIS.remove(document.getIdDocument());
            boutonFavori.setText("☆ Ajouter à mes favoris");
        } else {
            FAVORIS.add(document.getIdDocument());
            boutonFavori.setText("★ Retirer des favoris");
        }
    }

    @FXML
    private void onRetour() {
        try {
            RechercheController c = NavigationEtudiant.afficher(racine, "/fxml/recherche.fxml");
            c.setUtilisateur(utilisateur);
        } catch (IOException e) {
            alerte(Alert.AlertType.ERROR, "Erreur", e.getMessage());
        }
    }

    private String nomFichier(String titre) {
        String propre = titre.replaceAll("[^\\p{L}\\p{N}]+", "_");
        if (propre.length() > 60) {
            propre = propre.substring(0, 60);
        }
        return propre + ".pdf";
    }

    private String texte(String s) {
        return (s == null || s.isBlank()) ? "—" : s;
    }

    private void alerte(Alert.AlertType type, String titre, String message) {
        Alert a = new Alert(type, message);
        a.setTitle(titre);
        a.setHeaderText(titre);
        a.showAndWait();
    }
}