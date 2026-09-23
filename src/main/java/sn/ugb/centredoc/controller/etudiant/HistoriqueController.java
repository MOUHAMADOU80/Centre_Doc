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
import sn.ugb.centredoc.exception.DocumentIntrouvableException;
import sn.ugb.centredoc.model.Utilisateur;
import sn.ugb.centredoc.model.enums.TypeDocument;
import sn.ugb.centredoc.service.TelechargementService;
import sn.ugb.centredoc.service.TelechargementService.HistoriqueLigne;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HistoriqueController {

    private static final DateTimeFormatter FORMAT_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML private BorderPane racine;
    @FXML private TableView<HistoriqueLigne> table;
    @FXML private TableColumn<HistoriqueLigne, String> colDocument;
    @FXML private TableColumn<HistoriqueLigne, String> colDate;
    @FXML private TableColumn<HistoriqueLigne, String> colType;
    @FXML private TableColumn<HistoriqueLigne, HistoriqueLigne> colAction;

    private final TelechargementService service = new TelechargementService();

    private Utilisateur utilisateur;

    @FXML
    private void initialize() {
        colDocument.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTitreDocument()));
        colDate.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDateTelechargement().format(FORMAT_DATE)));
        colType.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getTypeDocument() == TypeDocument.THESE ? "Thèse" : "Mémoire"));
        colAction.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue()));
        colAction.setCellFactory(col -> new CelluleTelecharger());
    }

    public void initialiser(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
        racine.setLeft(Sidebar.construire(racine, utilisateur, "historique"));
        racine.setTop(Topbar.construire(utilisateur, null));
        chargerHistorique();
    }

    private void chargerHistorique() {
        try {
            List<HistoriqueLigne> lignes = service.historiqueDetaille(utilisateur);
            table.getItems().setAll(lignes);
        } catch (SQLException e) {
            alerte(Alert.AlertType.ERROR, "Erreur base de données", e.getMessage());
        }
    }

    @FXML
    private void onNouveauTelechargement() {
        try {
            TelechargementController c = NavigationEtudiant.afficher(racine, "/fxml/telechargement.fxml");
            c.initialiser(utilisateur);
        } catch (IOException e) {
            alerte(Alert.AlertType.ERROR, "Erreur", e.getMessage());
        }
    }

    private void telechargerDeNouveau(HistoriqueLigne ligne) {
        FileChooser choix = new FileChooser();
        choix.setTitle("Enregistrer le PDF");
        choix.setInitialFileName(nomFichier(ligne.getTitreDocument()));
        choix.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichier PDF", "*.pdf"));
        File destination = choix.showSaveDialog(table.getScene().getWindow());
        if (destination == null) return;
        try {
            service.telecharger(utilisateur, ligne.getIdDocument(), destination);
            chargerHistorique();
            alerte(Alert.AlertType.INFORMATION, "Téléchargement réussi", "Le document a été enregistré avec succès.");
        } catch (AccesRefuseException e) {
            alerte(Alert.AlertType.WARNING, "Téléchargement refusé", e.getMessage());
        } catch (DocumentIntrouvableException e) {
            alerte(Alert.AlertType.WARNING, "Document introuvable", e.getMessage());
        } catch (SQLException | IOException e) {
            alerte(Alert.AlertType.ERROR, "Erreur", e.getMessage());
        }
    }

    private String nomFichier(String titre) {
        String propre = titre.replaceAll("[^\\p{L}\\p{N}]+", "_");
        if (propre.length() > 60) propre = propre.substring(0, 60);
        return propre + ".pdf";
    }

    private void alerte(Alert.AlertType type, String titre, String message) {
        Alert a = new Alert(type, message);
        a.setTitle(titre);
        a.setHeaderText(titre);
        a.showAndWait();
    }

    private class CelluleTelecharger extends TableCell<HistoriqueLigne, HistoriqueLigne> {
        private final Button bouton = new Button("⬇");

        CelluleTelecharger() {
            bouton.getStyleClass().add("bouton-icone");
            bouton.setOnAction(e -> telechargerDeNouveau(getItem()));
        }

        @Override
        protected void updateItem(HistoriqueLigne item, boolean vide) {
            super.updateItem(item, vide);
            setGraphic(vide || item == null ? null : bouton);
        }
    }
}