package sn.ugb.centredoc.controller.admin;

import java.util.List;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import sn.ugb.centredoc.exception.ChampInvalideException;
import sn.ugb.centredoc.exception.DoublonException;
import sn.ugb.centredoc.model.Gestionnaire;
import sn.ugb.centredoc.model.Ufr;
import sn.ugb.centredoc.model.Utilisateur;
import sn.ugb.centredoc.service.UfrService;
import sn.ugb.centredoc.service.UtilisateurService;
import sn.ugb.centredoc.util.Alertes;

public class UtilisateursController {

    @FXML private TextField champRecherche;
    @FXML private TableView<GestionnaireLigne> tableUtilisateurs;
    @FXML private TableColumn<GestionnaireLigne, String> colNom;
    @FXML private TableColumn<GestionnaireLigne, String> colPrenom;
    @FXML private TableColumn<GestionnaireLigne, String> colEmail;
    @FXML private TableColumn<GestionnaireLigne, String> colUfr;
    @FXML private TableColumn<GestionnaireLigne, Void> colActions;

    private final UtilisateurService utilisateurService = new UtilisateurService();
    private final UfrService ufrService = new UfrService();

    private List<GestionnaireLigne> toutesLesLignes;

    // Ligne du tableau : Gestionnaire + nom de son UFR (calcule)
    public static class GestionnaireLigne {
        private final Gestionnaire gestionnaire;
        private final String nomUfr;

        public GestionnaireLigne(Gestionnaire gestionnaire, String nomUfr) {
            this.gestionnaire = gestionnaire;
            this.nomUfr = nomUfr;
        }

        public String getNom() { return gestionnaire.getNom(); }
        public String getPrenom() { return gestionnaire.getPrenom(); }
        public String getEmail() { return gestionnaire.getEmail(); }
        public String getNomUfr() { return nomUfr; }
        public Gestionnaire getGestionnaire() { return gestionnaire; }
    }

    @FXML
    public void initialize() {
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colUfr.setCellValueFactory(new PropertyValueFactory<>("nomUfr"));

        colActions.setCellFactory(colonne -> new TableCell<>() {
            private final Button boutonModifier = new Button("Modifier");
            private final Button boutonSupprimer = new Button("Supprimer");
            private final HBox boite = new HBox(6, boutonModifier, boutonSupprimer);

            {
                boutonModifier.setOnAction(e -> {
                    GestionnaireLigne ligne = getTableView().getItems().get(getIndex());
                    ouvrirFormulaire(ligne.getGestionnaire());
                });
                boutonSupprimer.setOnAction(e -> {
                    GestionnaireLigne ligne = getTableView().getItems().get(getIndex());
                    supprimer(ligne.getGestionnaire());
                });
            }

            @Override
            protected void updateItem(Void item, boolean vide) {
                super.updateItem(item, vide);
                setGraphic(vide ? null : boite);
            }
        });

        chargerDonnees();
    }

    private void chargerDonnees() {
        try {
            List<Utilisateur> utilisateurs = utilisateurService.listerGestionnaires();
            ObservableList<GestionnaireLigne> lignes = FXCollections.observableArrayList();

            for (Utilisateur u : utilisateurs) {
                if (u instanceof Gestionnaire g) {
                    String nomUfr = (g.getIdUfr() != null)
                            ? ufrService.trouverParId(g.getIdUfr()).getNom()
                            : "Non affecte";
                    lignes.add(new GestionnaireLigne(g, nomUfr));
                }
            }

            toutesLesLignes = lignes;
            tableUtilisateurs.setItems(lignes);

        } catch (Exception e) {
            Alertes.afficherErreur("Erreur de chargement des utilisateurs : " + e.getMessage());
        }
    }

    @FXML
    private void onRecherche() {
        String motCle = champRecherche.getText().toLowerCase().trim();

        if (motCle.isEmpty()) {
            tableUtilisateurs.setItems(FXCollections.observableArrayList(toutesLesLignes));
            return;
        }

        ObservableList<GestionnaireLigne> filtrees = FXCollections.observableArrayList();
        for (GestionnaireLigne ligne : toutesLesLignes) {
            if (ligne.getNom().toLowerCase().contains(motCle)
                    || ligne.getPrenom().toLowerCase().contains(motCle)
                    || ligne.getEmail().toLowerCase().contains(motCle)) {
                filtrees.add(ligne);
            }
        }
        tableUtilisateurs.setItems(filtrees);
    }

    @FXML
    private void onOuvrirFormulaireAjout() {
        ouvrirFormulaire(null);
    }

    private void ouvrirFormulaire(Gestionnaire gestionnaireExistant) {
        boolean estModification = (gestionnaireExistant != null);

        Dialog<ButtonType> dialogue = new Dialog<>();
        dialogue.setTitle(estModification ? "Modifier un gestionnaire" : "Ajouter un gestionnaire");
        dialogue.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField champNom = new TextField(estModification ? gestionnaireExistant.getNom() : "");
        TextField champPrenom = new TextField(estModification ? gestionnaireExistant.getPrenom() : "");
        TextField champEmail = new TextField(estModification ? gestionnaireExistant.getEmail() : "");
        PasswordField champMotDePasse = new PasswordField();
        champMotDePasse.setPromptText(estModification ? "(laisser vide = inchange)" : "Mot de passe");

        ComboBox<Ufr> choixUfr = new ComboBox<>();
        try {
            choixUfr.getItems().addAll(ufrService.listerToutes());
        } catch (Exception e) {
            Alertes.afficherErreur("Erreur de chargement des UFR : " + e.getMessage());
        }
        if (estModification && gestionnaireExistant.getIdUfr() != null) {
            for (Ufr ufr : choixUfr.getItems()) {
                if (ufr.getIdUfr() == gestionnaireExistant.getIdUfr()) {
                    choixUfr.setValue(ufr);
                    break;
                }
            }
        }

        GridPane grille = new GridPane();
        grille.setHgap(10);
        grille.setVgap(10);
        grille.setPadding(new Insets(20));
        grille.addRow(0, new Label("Nom :"), champNom);
        grille.addRow(1, new Label("Prenom :"), champPrenom);
        grille.addRow(2, new Label("Email :"), champEmail);
        grille.addRow(3, new Label("Mot de passe :"), champMotDePasse);
        grille.addRow(4, new Label("UFR :"), choixUfr);

        dialogue.getDialogPane().setContent(grille);

        Optional<ButtonType> resultat = dialogue.showAndWait();
        if (resultat.isPresent() && resultat.get() == ButtonType.OK) {
            try {
                Ufr ufrChoisie = choixUfr.getValue();
                Integer idUfrChoisie = (ufrChoisie != null) ? ufrChoisie.getIdUfr() : null;

                if (estModification) {
                    gestionnaireExistant.setNom(champNom.getText());
                    gestionnaireExistant.setPrenom(champPrenom.getText());
                    gestionnaireExistant.setEmail(champEmail.getText());
                    gestionnaireExistant.setIdUfr(idUfrChoisie);
                    utilisateurService.modifierGestionnaire(gestionnaireExistant);
                } else {
                    Gestionnaire nouveauGestionnaire = new Gestionnaire(
                            champNom.getText(), champPrenom.getText(), champEmail.getText(), idUfrChoisie);
                    utilisateurService.creerGestionnaire(nouveauGestionnaire, champMotDePasse.getText());
                }

                Alertes.afficherSucces("Gestionnaire enregistre avec succes");
                chargerDonnees();

            } catch (ChampInvalideException | DoublonException e) {
                Alertes.afficherErreur(e.getMessage());
            } catch (Exception e) {
                Alertes.afficherErreur("Erreur technique : " + e.getMessage());
            }
        }
    }

    private void supprimer(Gestionnaire gestionnaire) {
        boolean confirme = Alertes.demanderConfirmation(
                "Supprimer le gestionnaire " + gestionnaire.getPrenom() + " " + gestionnaire.getNom() + " ?");

        if (confirme) {
            try {
                utilisateurService.supprimer(gestionnaire.getIdUtilisateur());
                Alertes.afficherSucces("Gestionnaire supprime");
                chargerDonnees();
            } catch (Exception e) {
                Alertes.afficherErreur("Erreur lors de la suppression : " + e.getMessage());
            }
        }
    }
}