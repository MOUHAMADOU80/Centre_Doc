package sn.ugb.centredoc.controller.etudiant.composants;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import sn.ugb.centredoc.model.Etudiant;
import sn.ugb.centredoc.model.Utilisateur;

public final class Topbar {

    private Topbar() {}

    public static HBox construire(Utilisateur utilisateur, TextField champExistant) {
        HBox racine = new HBox(14);
        racine.getStyleClass().add("topbar");
        racine.setAlignment(Pos.CENTER_LEFT);

        TextField recherche = (champExistant != null) ? champExistant : new TextField();
        recherche.setPromptText("Rechercher un document, un mot-clé, une UFR...");
        recherche.getStyleClass().add("topbar-recherche");
        HBox.setHgrow(recherche, Priority.ALWAYS);

        Label cloche = new Label("🔔");
        cloche.getStyleClass().add("topbar-icone");

        VBox identite = new VBox(0);
        identite.setAlignment(Pos.CENTER_RIGHT);
        String nomComplet = (utilisateur.getPrenom() == null ? "" : utilisateur.getPrenom())
                + " " + (utilisateur.getNom() == null ? "" : utilisateur.getNom());
        Label nom = new Label(nomComplet.trim().isEmpty() ? "Étudiant" : nomComplet.trim());
        nom.getStyleClass().add("topbar-role");
        String code = (utilisateur instanceof Etudiant e) ? e.getCodeEtudiant() : null;
        Label codeLabel = new Label("Code : " + (code == null ? "-" : code));
        codeLabel.getStyleClass().add("topbar-code");
        identite.getChildren().addAll(nom, codeLabel);

        Label avatar = new Label("👤");
        avatar.getStyleClass().add("topbar-avatar");

        racine.getChildren().addAll(recherche, cloche, identite, avatar);
        return racine;
    }
}