package sn.ugb.centredoc.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import sn.ugb.centredoc.exception.AuthentificationException;
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
            Alertes.afficherSucces("Bienvenue " + u.getPrenom() + " !");
            // TODO : ouvrir le bon dashboard selon u.getRole()

        } catch (AuthentificationException e) {
            Alertes.afficherErreur(e.getMessage());
        } catch (Exception e) {
            Alertes.afficherErreur("Erreur technique : " + e.getMessage());
        }
    }
}