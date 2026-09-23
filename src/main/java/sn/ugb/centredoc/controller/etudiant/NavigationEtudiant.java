package sn.ugb.centredoc.controller.etudiant;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;

import java.io.IOException;

public final class NavigationEtudiant {

    private NavigationEtudiant() {}

    /** Remplace le contenu de la fenêtre par l'écran demandé et renvoie son contrôleur. */
    public static <T> T afficher(Node source, String cheminFxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(NavigationEtudiant.class.getResource(cheminFxml));
        Parent racine = loader.load();
        source.getScene().setRoot(racine);
        return loader.getController();
    }
}