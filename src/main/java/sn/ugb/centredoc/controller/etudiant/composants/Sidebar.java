package sn.ugb.centredoc.controller.etudiant.composants;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import sn.ugb.centredoc.controller.etudiant.HistoriqueController;
import sn.ugb.centredoc.controller.etudiant.NavigationEtudiant;
import sn.ugb.centredoc.controller.etudiant.RechercheController;
import sn.ugb.centredoc.model.Utilisateur;
import sn.ugb.centredoc.util.Session;

import java.io.IOException;
import java.io.InputStream;

/**
 * Barre de navigation laterale, commune aux 4 ecrans etudiant.
 * Meme structure visuelle que le dashboard Gestionnaire : logo + role + nom,
 * separateur, items texte simple, deconnexion en bas.
 */
public final class Sidebar {

    private Sidebar() {}

    public static VBox construire(Node source, Utilisateur utilisateur, String ongletActif) {
        VBox racine = new VBox();
        racine.getStyleClass().add("sidebar");

        HBox logo = new HBox(10);
        logo.getStyleClass().add("sidebar-logo");
        ImageView imageLogo = chargerLogo();
        if (imageLogo != null) {
            logo.getChildren().add(imageLogo);
        }
        VBox texteLogo = new VBox(1);
        Label titreLogo = new Label("Etudiant");
        titreLogo.getStyleClass().add("sidebar-titre");
        String nomComplet = (utilisateur.getPrenom() == null ? "" : utilisateur.getPrenom())
                + " " + (utilisateur.getNom() == null ? "" : utilisateur.getNom());
        Label sousLogo = new Label(nomComplet.trim().isEmpty() ? "Etudiant" : nomComplet.trim());
        sousLogo.getStyleClass().add("sidebar-logo-sous");
        texteLogo.getChildren().addAll(titreLogo, sousLogo);
        logo.getChildren().add(texteLogo);

        Separator separateur = new Separator();
        separateur.getStyleClass().add("sidebar-separateur");

        VBox nav = new VBox(4);
        nav.getStyleClass().add("sidebar-nav");
        nav.getChildren().addAll(
                item("Tableau de bord", "tableau-de-bord".equals(ongletActif),
                        () -> indisponible()),
                item("Documents", "documents".equals(ongletActif),
                        () -> allerRecherche(source, utilisateur)),
                item("Historique", "historique".equals(ongletActif),
                        () -> allerHistorique(source, utilisateur)),
                item("Parametres", "parametres".equals(ongletActif),
                        () -> indisponible())
        );

        Region espace = new Region();
        VBox.setVgrow(espace, Priority.ALWAYS);

        Button deconnexion = item("Deconnexion", false, () -> deconnecter(source));
        deconnexion.getStyleClass().add("sidebar-bouton-deconnexion");

        racine.getChildren().addAll(logo, separateur, nav, espace, deconnexion);
        return racine;
    }

    private static ImageView chargerLogo() {
        try (InputStream flux = Sidebar.class.getResourceAsStream("/images/logo_ugb.png")) {
            if (flux == null) {
                return null;
            }
            ImageView vue = new ImageView(new Image(flux));
            vue.setFitWidth(40);
            vue.setFitHeight(40);
            vue.setPreserveRatio(true);
            return vue;
        } catch (IOException e) {
            return null;
        }
    }

    private static Button item(String texte, boolean actif, Runnable action) {
        Button b = new Button(texte);
        b.getStyleClass().add("sidebar-bouton");
        if (actif) {
            b.getStyleClass().add("sidebar-bouton-actif");
        }
        b.setMaxWidth(Double.MAX_VALUE);
        b.setOnAction(e -> action.run());
        return b;
    }

    private static void allerRecherche(Node source, Utilisateur utilisateur) {
        try {
            RechercheController c = NavigationEtudiant.afficher(source, "/fxml/recherche.fxml");
            c.setUtilisateur(utilisateur);
        } catch (IOException e) {
            alerte(e.getMessage());
        }
    }

    private static void allerHistorique(Node source, Utilisateur utilisateur) {
        try {
            HistoriqueController c = NavigationEtudiant.afficher(source, "/fxml/historique.fxml");
            c.initialiser(utilisateur);
        } catch (IOException e) {
            alerte(e.getMessage());
        }
    }

    /** Deconnexion reelle : vide la session et retourne a l'ecran de connexion, comme cote Admin/Gestionnaire. */
    private static void deconnecter(Node source) {
        Session.deconnecter();
        try {
            FXMLLoader loader = new FXMLLoader(Sidebar.class.getResource("/fxml/login.fxml"));
            Parent racine = loader.load();
            Stage stage = (Stage) source.getScene().getWindow();
            stage.setScene(new Scene(racine));
        } catch (IOException e) {
            alerte("Erreur lors de la deconnexion : " + e.getMessage());
        }
    }

    private static void indisponible() {
        alerte("Cet ecran fait partie du module d'un autre membre de l'equipe.");
    }

    private static void alerte(String message) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, message);
        a.setHeaderText("Indisponible");
        a.showAndWait();
    }
}