package sn.ugb.centredoc.controller.etudiant;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import sn.ugb.centredoc.controller.etudiant.composants.Sidebar;
import sn.ugb.centredoc.controller.etudiant.composants.Topbar;
import sn.ugb.centredoc.exception.AccesRefuseException;
import sn.ugb.centredoc.exception.ChampInvalideException;
import sn.ugb.centredoc.exception.DocumentIntrouvableException;
import sn.ugb.centredoc.model.Document;
import sn.ugb.centredoc.model.Utilisateur;
import sn.ugb.centredoc.model.enums.NiveauAcces;
import sn.ugb.centredoc.model.enums.TypeDocument;
import sn.ugb.centredoc.service.RechercheService;
import sn.ugb.centredoc.service.TelechargementService;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class TelechargementController {

    @FXML private BorderPane racine;
    @FXML private TableView<Document> table;
    @FXML private TableColumn<Document, String> colTitre;
    @FXML private TableColumn<Document, String> colType;
    @FXML private TableColumn<Document, String> colDate;
    @FXML private TableColumn<Document, String> colAcces;
    @FXML private TableColumn<Document, Document> colActions;
    @FXML private Label labelEspace;
    @FXML private ProgressBar barreEspace;

    private final RechercheService rechercheService = new RechercheService();
    private final TelechargementService telechargementService = new TelechargementService();

    private Utilisateur utilisateur;

    @FXML
    private void initialize() {
        colTitre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTitre()));
        colType.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getType() == TypeDocument.THESE ? "Thèse" : "Mémoire"));
        colDate.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getAnnee())));
        colAcces.setCellValueFactory(c -> new SimpleStringProperty(libelleAcces(c.getValue().getNiveauAcces())));
        colActions.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue()));
        colActions.setCellFactory(col -> new CelluleTelecharger());
    }

    public void initialiser(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
        racine.setLeft(Sidebar.construire(racine, utilisateur, "documents"));
        racine.setTop(Topbar.construire(utilisateur, null));
        chargerListe();
        afficherEspaceDisque();
    }

    private void chargerListe() {
        try {
            List<Document> resultats = rechercheService.rechercherLibre(null, null, null, null);
            table.getItems().setAll(resultats);
        } catch (ChampInvalideException | SQLException e) {
            alerte(Alert.AlertType.ERROR, "Erreur", e.getMessage());
        }
    }

    /** Espace disque réel du poste de l'étudiant, à titre indicatif (le mockup l'affiche en bas de page). */
    private void afficherEspaceDisque() {
        File racineDisque = new File(System.getProperty("user.home"));
        double totalGo = racineDisque.getTotalSpace() / 1_073_741_824.0;
        double libreGo = racineDisque.getUsableSpace() / 1_073_741_824.0;
        double utiliseGo = totalGo - libreGo;
        labelEspace.setText(String.format("Espace disponible : %.1f Go / %.1f Go", libreGo, totalGo));
        barreEspace.setProgress(totalGo == 0 ? 0 : utiliseGo / totalGo);
    }

    private String libelleAcces(NiveauAcces n) {
        switch (n) {
            case TELECHARGEABLE: return "Téléchargeable";
            case CONSULTATION_SEULE: return "Consultation seule";
            default: return "🔒 Restreint";
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

    private void alerte(Alert.AlertType type, String titre, String message) {
        Alert a = new Alert(type, message);
        a.setTitle(titre);
        a.setHeaderText(titre);
        a.showAndWait();
    }

    private void telecharger(Document d) {
        if (d == null) return;
        FileChooser choix = new FileChooser();
        choix.setTitle("Enregistrer le PDF");
        choix.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichier PDF", "*.pdf"));
        File destination = choix.showSaveDialog(table.getScene().getWindow());
        if (destination == null) return;
        try {
            telechargementService.telecharger(utilisateur, d.getIdDocument(), destination);
            alerte(Alert.AlertType.INFORMATION, "Téléchargement réussi",
                   "Le document « " + destination.getName() + " » a été téléchargé avec succès.");
            afficherEspaceDisque();
        } catch (AccesRefuseException e) {
            alerte(Alert.AlertType.WARNING, "Téléchargement refusé", e.getMessage());
        } catch (DocumentIntrouvableException e) {
            alerte(Alert.AlertType.WARNING, "Fichier introuvable", e.getMessage());
        } catch (SQLException | IOException e) {
            alerte(Alert.AlertType.ERROR, "Erreur", e.getMessage());
        }
    }

    private class CelluleTelecharger extends TableCell<Document, Document> {
        private final Button bouton = new Button("⬇");

        CelluleTelecharger() {
            bouton.getStyleClass().add("bouton-icone");
            bouton.setOnAction(e -> telecharger(getItem()));
        }

        @Override
        protected void updateItem(Document d, boolean vide) {
            super.updateItem(d, vide);
            if (vide || d == null) {
                setGraphic(null);
                return;
            }
            bouton.setDisable(d.getNiveauAcces() != NiveauAcces.TELECHARGEABLE);
            setGraphic(bouton);
        }
    }
}