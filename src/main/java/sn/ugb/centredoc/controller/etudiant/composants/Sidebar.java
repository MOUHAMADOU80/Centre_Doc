package sn.ugb.centredoc.controller.etudiant.composants;

import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import sn.ugb.centredoc.controller.etudiant.HistoriqueController;
import sn.ugb.centredoc.controller.etudiant.NavigationEtudiant;
import sn.ugb.centredoc.controller.etudiant.RechercheController;
import sn.ugb.centredoc.model.Utilisateur;

import java.io.IOException;
import java.io.InputStream;

/**
 * Barre de navigation latérale, commune aux 4 écrans étudiant.
 * "Tableau de bord" et "Paramètres" ne font pas partie du module étudiant
 * (ils appartiennent à d'autres membres de l'équipe) : ils affichent un message neutre.
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
        Label titreLogo = new Label("UGB");
        titreLogo.getStyleClass().add("sidebar-logo-titre");
        Label sousLogo = new Label("Université Gaston Berger\nSaint-Louis du Sénégal");
        sousLogo.getStyleClass().add("sidebar-logo-sous");
        sousLogo.setWrapText(true);
        texteLogo.getChildren().addAll(titreLogo, sousLogo);
        logo.getChildren().add(texteLogo);

        VBox nav = new VBox(4);
        nav.getStyleClass().add("sidebar-nav");
        nav.getChildren().addAll(
                item("🏠", "Tableau de bord", "tableau-de-bord".equals(ongletActif),
                        () -> indisponible()),
                item("📄", "Documents", "documents".equals(ongletActif),
                        () -> allerRecherche(source, utilisateur)),
                item("🕒", "Historique", "historique".equals(ongletActif),
                        () -> allerHistorique(source, utilisateur)),
                item("⚙", "Paramètres", "parametres".equals(ongletActif),
                        () -> indisponible())
        );

        Region espace = new Region();
        VBox.setVgrow(espace, Priority.ALWAYS);

        Button deconnexion = item("↪", "Déconnexion", false, Sidebar::indisponible);
        deconnexion.getStyleClass().add("sidebar-deconnexion");

        racine.getChildren().addAll(logo, nav, espace, deconnexion);
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

    private static Button item(String icone, String texte, boolean actif, Runnable action) {
        Button b = new Button(icone + "  " + texte);
        b.getStyleClass().add("sidebar-item");
        if (actif) {
            b.getStyleClass().add("sidebar-item-actif");
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

    private static void indisponible() {
        alerte("Cet écran fait partie du module d'un autre membre de l'équipe.");
    }

    private static void alerte(String message) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, message);
        a.setHeaderText("Indisponible");
        a.showAndWait();
    }
}