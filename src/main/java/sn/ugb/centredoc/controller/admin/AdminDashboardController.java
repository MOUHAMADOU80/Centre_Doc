package sn.ugb.centredoc.controller.admin;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import sn.ugb.centredoc.service.UfrService;
import sn.ugb.centredoc.service.UtilisateurService;
import sn.ugb.centredoc.util.Alertes;
import sn.ugb.centredoc.util.Session;

public class AdminDashboardController {

    @FXML private StackPane zoneContenu;

    @FXML private Button boutonNavTableauDeBord;
    @FXML private Button boutonNavUtilisateurs;
    @FXML private Button boutonNavUfr;

    // Champs de dashboard_home.fxml (remplis seulement quand cet ecran est charge)
    @FXML private Label labelNbUtilisateurs;
    @FXML private Label labelNbDocuments;
    @FXML private Label labelNbTelechargements;
    @FXML private Label labelNbUfr;
    @FXML private PieChart graphiqueRepartition;
    @FXML private BarChart<String, Number> graphiqueActivite;

    private final UtilisateurService utilisateurService = new UtilisateurService();
    private final UfrService ufrService = new UfrService();

    @FXML
    public void initialize() {
        // Si ce controleur est utilise pour le shell (admin_dashboard.fxml),
        // zoneContenu existe : on charge la vue d'accueil par defaut.
        if (zoneContenu != null) {
            onNavTableauDeBord();
        }
        // Si ce controleur est utilise pour dashboard_home.fxml directement,
        // labelNbUtilisateurs existe : on remplit les stats.
        if (labelNbUtilisateurs != null) {
            chargerStatistiques();
        }
    }

    private void chargerStatistiques() {
        try {
            labelNbUtilisateurs.setText(String.valueOf(utilisateurService.listerGestionnaires().size()));
            labelNbUfr.setText(String.valueOf(ufrService.listerToutes().size()));

            // TODO (Ousmane) : brancher le vrai total via DocumentService
            labelNbDocuments.setText("0");
            // TODO (Maoudo) : brancher le vrai total via TelechargementService
            labelNbTelechargements.setText("0");

            // TODO : remplacer par les vraies donnees une fois DocumentService pret
            graphiqueRepartition.getData().add(new PieChart.Data("These", 0));
            graphiqueRepartition.getData().add(new PieChart.Data("Memoire", 0));

            XYChart.Series<String, Number> serie = new XYChart.Series<>();
            serie.getData().add(new XYChart.Data<>("Jan", 0));
            serie.getData().add(new XYChart.Data<>("Fev", 0));
            serie.getData().add(new XYChart.Data<>("Mar", 0));
            graphiqueActivite.getData().add(serie);

        } catch (Exception e) {
            Alertes.afficherErreur("Erreur de chargement des statistiques : " + e.getMessage());
        }
    }

    @FXML
    private void onNavTableauDeBord() {
        chargerVue("/fxml/dashboard_home.fxml");
        marquerBoutonActif(boutonNavTableauDeBord);
    }

    @FXML
    private void onNavUtilisateurs() {
        chargerVue("/fxml/utilisateurs.fxml");
        marquerBoutonActif(boutonNavUtilisateurs);
    }

    @FXML
    private void onNavUfr() {
        chargerVue("/fxml/ufr.fxml");
        marquerBoutonActif(boutonNavUfr);
    }

    @FXML
    private void onNavIndisponible() {
        Alertes.afficherErreur("Ce module n'est pas encore disponible.");
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
        boutonNavUtilisateurs.getStyleClass().remove("sidebar-bouton-actif");
        boutonNavUfr.getStyleClass().remove("sidebar-bouton-actif");

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
}