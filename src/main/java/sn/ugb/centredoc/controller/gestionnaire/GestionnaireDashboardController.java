package sn.ugb.centredoc.controller.gestionnaire;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import sn.ugb.centredoc.model.Gestionnaire;
import sn.ugb.centredoc.model.Utilisateur;
import sn.ugb.centredoc.util.Alertes;
import sn.ugb.centredoc.util.Session;

public class GestionnaireDashboardController {

    @FXML private StackPane zoneContenu;
    @FXML private Label labelNomGestionnaire;

    @FXML private Button boutonNavTableauDeBord;
    @FXML private Button boutonNavDocuments;
    @FXML private Button boutonNavStatistiques;

    @FXML
    public void initialize() {
        Utilisateur connecte = Session.getUtilisateurConnecte();
        if (labelNomGestionnaire != null && connecte != null) {
            labelNomGestionnaire.setText(connecte.getPrenom() + " " + connecte.getNom());
        }

        if (zoneContenu != null) {
            onNavTableauDeBord();
        }
    }

    @FXML
    private void onNavTableauDeBord() {
        chargerVue("/fxml/gestionnaire_dashboard_home.fxml");
        marquerBoutonActif(boutonNavTableauDeBord);
    }

    @FXML
    private void onNavDocuments() {
        chargerVue("/fxml/gestionnaire_documents.fxml");
        marquerBoutonActif(boutonNavDocuments);
    }

    @FXML
    private void onNavStatistiques() {
        chargerVue("/fxml/gestionnaire_statistiques.fxml");
        marquerBoutonActif(boutonNavStatistiques);
    }

    @FXML
    private void onDeconnexion() {
        Session.deconnecter();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) zoneContenu.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            Alertes.afficherErreur("Erreur lors de la deconnexion : " + e.getMessage());
        }
    }

    private void marquerBoutonActif(Button boutonActif) {
        boutonNavTableauDeBord.getStyleClass().remove("sidebar-bouton-actif");
        boutonNavDocuments.getStyleClass().remove("sidebar-bouton-actif");
        boutonNavStatistiques.getStyleClass().remove("sidebar-bouton-actif");

        boutonActif.getStyleClass().add("sidebar-bouton-actif");
    }

    private void chargerVue(String cheminFxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(cheminFxml));
            Parent vue = loader.load();
            zoneContenu.getChildren().setAll(vue);
        } catch (IOException e) {
            Alertes.afficherErreur("Erreur de chargement de la vue : " + e.getMessage());
        }
    }

    /**
     * Recupere l'UFR du gestionnaire connecte. Si l'utilisateur en session
     * n'est pas un Gestionnaire (ne devrait pas arriver sur cet ecran), retourne null.
     */
    public static Gestionnaire gestionnaireConnecte() {
        Utilisateur u = Session.getUtilisateurConnecte();
        return (u instanceof Gestionnaire g) ? g : null;
    }
}
