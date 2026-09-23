package sn.ugb.centredoc.controller.etudiant;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import sn.ugb.centredoc.controller.etudiant.composants.Sidebar;
import sn.ugb.centredoc.controller.etudiant.composants.Topbar;
import sn.ugb.centredoc.dao.UfrDAO;
import sn.ugb.centredoc.exception.AccesRefuseException;
import sn.ugb.centredoc.exception.ChampInvalideException;
import sn.ugb.centredoc.exception.DocumentIntrouvableException;
import sn.ugb.centredoc.model.Document;
import sn.ugb.centredoc.model.Ufr;
import sn.ugb.centredoc.model.Utilisateur;
import sn.ugb.centredoc.model.enums.TypeDocument;
import sn.ugb.centredoc.service.RechercheService;
import sn.ugb.centredoc.service.TelechargementService;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.Year;
import java.util.List;

public class RechercheController {

    private static final String TOUS = "Tous";
    private static final String TOUTES = "Toutes";

    @FXML private BorderPane racine;
    @FXML private TextField champRecherche;
    @FXML private ComboBox<String> comboType;
    @FXML private ComboBox<Ufr> comboUfr;
    @FXML private ComboBox<String> comboAnnee;
    @FXML private Label labelResultats;
    @FXML private TableView<Document> table;
    @FXML private TableColumn<Document, String> colTitre;
    @FXML private TableColumn<Document, String> colType;
    @FXML private TableColumn<Document, String> colUfr;
    @FXML private TableColumn<Document, String> colAuteur;
    @FXML private TableColumn<Document, String> colDate;
    @FXML private TableColumn<Document, Document> colActions;

    private final RechercheService service = new RechercheService();
    private final TelechargementService telechargementService = new TelechargementService();
    private final UfrDAO ufrDAO = new UfrDAO();

    private Utilisateur utilisateur;

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
        racine.setLeft(Sidebar.construire(racine, utilisateur, "documents"));
        racine.setTop(Topbar.construire(utilisateur, null));
    }

    @FXML
    private void initialize() {
        colTitre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTitre()));
        colType.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getType() == TypeDocument.THESE ? "Thèse" : "Mémoire"));
        colUfr.setCellValueFactory(c -> {
            try {
                return new SimpleStringProperty(service.nomUfr(c.getValue().getIdUfr()));
            } catch (SQLException e) {
                return new SimpleStringProperty("—");
            }
        });
        colAuteur.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAuteur()));
        colDate.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getAnnee())));
        colActions.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue()));
        colActions.setCellFactory(col -> new CelluleActions());

        chargerFiltres();
        lancerRecherche();
    }

    @FXML
    private void onRechercher() {
        lancerRecherche();
    }

    private void chargerFiltres() {
        comboType.getItems().addAll(TOUS, "Thèse", "Mémoire");
        comboType.getSelectionModel().selectFirst();
        comboType.setOnAction(e -> lancerRecherche());

        comboUfr.getItems().add(new Ufr(0, TOUTES));
        try {
            comboUfr.getItems().addAll(ufrDAO.listerTous());
        } catch (SQLException e) {
            alerte(Alert.AlertType.ERROR, "Erreur base de données", e.getMessage());
        }
        comboUfr.getSelectionModel().selectFirst();
        comboUfr.setOnAction(e -> lancerRecherche());

        comboAnnee.getItems().add(TOUTES);
        for (int a = Year.now().getValue(); a >= 2000; a--) {
            comboAnnee.getItems().add(String.valueOf(a));
        }
        comboAnnee.getSelectionModel().selectFirst();
        comboAnnee.setOnAction(e -> lancerRecherche());
    }

    private void lancerRecherche() {
        try {
            List<Document> resultats = service.rechercherLibre(
                    champRecherche.getText(), anneeChoisie(), idUfrChoisi(), null);

            String type = comboType.getValue();
            if (type != null && !TOUS.equals(type)) {
                TypeDocument td = type.equals("Thèse") ? TypeDocument.THESE : TypeDocument.MEMOIRE;
                resultats.removeIf(d -> d.getType() != td);
            }

            table.getItems().setAll(resultats);
            labelResultats.setText("Résultats de recherche (" + resultats.size() + ")");
        } catch (ChampInvalideException e) {
            alerte(Alert.AlertType.WARNING, "Recherche invalide", e.getMessage());
        } catch (SQLException e) {
            alerte(Alert.AlertType.ERROR, "Erreur base de données",
                   "Impossible de lire la base : " + e.getMessage());
        }
    }

    private Integer anneeChoisie() {
        String a = comboAnnee.getValue();
        return (a == null || TOUTES.equals(a)) ? null : Integer.valueOf(a);
    }

    private Integer idUfrChoisi() {
        Ufr u = comboUfr.getValue();
        return (u == null || u.getIdUfr() == 0) ? null : u.getIdUfr();
    }

    private void ouvrirFiche(Document d) {
        if (d == null) return;
        try {
            Document fiche = service.consulter(d.getIdDocument());
            FicheDocumentController c = NavigationEtudiant.afficher(racine, "/fxml/fiche_document.fxml");
            c.initialiser(utilisateur, fiche.getIdDocument());
        } catch (AccesRefuseException e) {
            alerte(Alert.AlertType.INFORMATION, "🔒 Document sous embargo", e.getMessage());
        } catch (DocumentIntrouvableException e) {
            alerte(Alert.AlertType.WARNING, "Document introuvable", e.getMessage());
        } catch (SQLException | IOException e) {
            alerte(Alert.AlertType.ERROR, "Erreur", e.getMessage());
        }
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
                   "Le document a été enregistré avec succès.");
        } catch (AccesRefuseException e) {
            alerte(Alert.AlertType.WARNING, "Téléchargement refusé", e.getMessage());
        } catch (DocumentIntrouvableException e) {
            alerte(Alert.AlertType.WARNING, "Fichier introuvable", e.getMessage());
        } catch (SQLException | IOException e) {
            alerte(Alert.AlertType.ERROR, "Erreur", e.getMessage());
        }
    }

    private void alerte(Alert.AlertType type, String titre, String message) {
        Alert a = new Alert(type, message);
        a.setTitle(titre);
        a.setHeaderText(titre);
        a.showAndWait();
    }

    /** Colonne Actions : icône œil (fiche) + icône téléchargement (si autorisé). */
    private class CelluleActions extends TableCell<Document, Document> {
        private final Button voir = new Button("👁");
        private final Button telechargerBtn = new Button("⬇");
        private final HBox conteneur = new HBox(6, voir, telechargerBtn);

        CelluleActions() {
            voir.getStyleClass().add("bouton-icone");
            telechargerBtn.getStyleClass().add("bouton-icone");
            voir.setOnAction(e -> ouvrirFiche(getItem()));
            telechargerBtn.setOnAction(e -> telecharger(getItem()));
        }

        @Override
        protected void updateItem(Document d, boolean vide) {
            super.updateItem(d, vide);
            if (vide || d == null) {
                setGraphic(null);
                return;
            }
            telechargerBtn.setDisable(!service.estTelechargeable(d));
            setGraphic(conteneur);
        }
    }
}