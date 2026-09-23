package sn.ugb.centredoc.util;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class Alertes {

    public static void afficherErreur(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void afficherSucces(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Succes");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static boolean demanderConfirmation(String message) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait()
                .filter(reponse -> reponse == javafx.scene.control.ButtonType.OK)
                .isPresent();
    }
}