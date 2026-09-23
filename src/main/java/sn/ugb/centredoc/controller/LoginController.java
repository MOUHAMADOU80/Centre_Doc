package sn.ugb.centredoc.controller;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import sn.ugb.centredoc.controller.etudiant.RechercheController;
import sn.ugb.centredoc.exception.AuthentificationException;
import sn.ugb.centredoc.model.Administrateur;
import sn.ugb.centredoc.model.Etudiant;
import sn.ugb.centredoc.model.Gestionnaire;
import sn.ugb.centredoc.model.Utilisateur;
import sn.ugb.centredoc.service.AuthService;
import sn.ugb.centredoc.util.Alertes;
import sn.ugb.centredoc.util.Session;

public class LoginController {

    @FXML private ComboBox<String> choixRole;
    @FXML private TextField champIdentifiant;
    @FXML private PasswordField champMotDePasse;
    @FXML private VBox blocEtudiant;
    @FXML private TextField champNomEtudiant;
    @FXML private TextField champPrenomEtudiant;
    @FXML private TextField champCodeEtudiant;

    private final AuthService authService = new AuthService();

    @FXML
    private void onChangerRole() {
        boolean estEtudiant = "Etudiant".equals(choixRole.getValue());

        blocEtudiant.setVisible(estEtudiant);
        blocEtudiant.setManaged(estEtudiant);

        champMotDePasse.setVisible(!estEtudiant);
        champMotDePasse.setManaged(!estEtudiant);
    }

    @FXML
    private void onConnecter() {
        String role = choixRole.getValue();

        if (role == null) {
            Alertes.afficherErreur("Veuillez selectionner un role");
            return;
        }

        try {
            Utilisateur u = switch (role) {
                case "Administrateur", "Gestionnaire" -> authService.connecterAdminOuGestionnaire(
                        champIdentifiant.getText(),
                        champMotDePasse.getText()
                );
                case "Etudiant" -> authService.connecterEtudiant(
                        champNomEtudiant.getText(),
                        champPrenomEtudiant.getText(),
                        champIdentifiant.getText(),
                        champCodeEtudiant.getText()
                );
                default -> throw new AuthentificationException("Role invalide");
            };

            Session.connecter(u);
            ouvrirEspaceSelonRole(u);

        } catch (AuthentificationException e) {
            Alertes.afficherErreur(e.getMessage());
        } catch (Exception e) {
            Alertes.afficherErreur("Erreur technique : " + e.getMessage());
        }
    }

    private void ouvrirEspaceSelonRole(Utilisateur u) {
        if (u instanceof Administrateur) {
            chargerEcran("/fxml/admin_dashboard.fxml");
        } else if (u instanceof Gestionnaire) {
            chargerEcran("/fxml/gestionnaire_dashboard.fxml");
        } else if (u instanceof Etudiant) {
            ouvrirEspaceEtudiant((Etudiant) u);
        } else {
            Alertes.afficherErreur("Cet espace n'est pas encore disponible pour ce role.");
        }
    }

    /** Ouvre l'ecran de recherche etudiant (module Ousmane) et lui transmet l'utilisateur connecte. */
    private void ouvrirEspaceEtudiant(Etudiant etudiant) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/recherche.fxml"));
            Parent root = loader.load();

            RechercheController controleur = loader.getController();
            controleur.setUtilisateur(etudiant);

            Stage stage = (Stage) champIdentifiant.getScene().getWindow();
            stage.setScene(new Scene(root, 1280, 760));

        } catch (IOException e) {
            Alertes.afficherErreur("Erreur d'ouverture de l'espace etudiant : " + e.getMessage());
        }
    }

    private void chargerEcran(String cheminFxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(cheminFxml));
            Parent root = loader.load();

            Stage stage = (Stage) champIdentifiant.getScene().getWindow();
            stage.setScene(new Scene(root));

        } catch (IOException e) {
            Alertes.afficherErreur("Erreur d'ouverture de l'ecran : " + e.getMessage());
        }
    }
}