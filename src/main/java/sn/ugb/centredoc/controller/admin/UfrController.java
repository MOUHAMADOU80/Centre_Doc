package sn.ugb.centredoc.controller.admin;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import sn.ugb.centredoc.exception.ChampInvalideException;
import sn.ugb.centredoc.model.Ufr;
import sn.ugb.centredoc.service.UfrService;
import sn.ugb.centredoc.service.UtilisateurService;
import sn.ugb.centredoc.util.Alertes;

public class UfrController {

    @FXML private TableView<UfrLigne> tableUfr;
    @FXML private TableColumn<UfrLigne, Integer> colId;
    @FXML private TableColumn<UfrLigne, String> colNom;
    @FXML private TableColumn<UfrLigne, String> colResponsable;
    @FXML private TableColumn<UfrLigne, Void> colActions;

    private final UfrService ufrService = new UfrService();
    private final UtilisateurService utilisateurService = new UtilisateurService();

    // Petite classe interne : represente une ligne du tableau (Ufr + nom du responsable calcule)
    public static class UfrLigne {
        private final Ufr ufr;
        private final String nomResponsable;

        public UfrLigne(Ufr ufr, String nomResponsable) {
            this.ufr = ufr;
            this.nomResponsable = nomResponsable;
        }

        public int getId() { return ufr.getIdUfr(); }
        public String getNom() { return ufr.getNom(); }
        public String getNomResponsable() { return nomResponsable; }
        public Ufr getUfr() { return ufr; }
    }

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colResponsable.setCellValueFactory(new PropertyValueFactory<>("nomResponsable"));

        colActions.setCellFactory(colonne -> new TableCell<>() {
            private final Button boutonModifier = new Button("Modifier");
            private final Button boutonSupprimer = new Button("Supprimer");
            private final HBox boite = new HBox(6, boutonModifier, boutonSupprimer);

            {
                boutonModifier.setOnAction(e -> {
                    UfrLigne ligne = getTableView().getItems().get(getIndex());
                    ouvrirFormulaire(ligne.getUfr());
                });
                boutonSupprimer.setOnAction(e -> {
                    UfrLigne ligne = getTableView().getItems().get(getIndex());
                    supprimer(ligne.getUfr());
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
            List<Ufr> listeUfr = ufrService.listerToutes();
            ObservableList<UfrLigne> lignes = FXCollections.observableArrayList();

            for (Ufr ufr : listeUfr) {
                String nomResponsable = utilisateurService.trouverNomResponsable(ufr.getIdUfr());
                lignes.add(new UfrLigne(ufr, nomResponsable));
            }

            tableUfr.setItems(lignes);
        } catch (Exception e) {
            Alertes.afficherErreur("Erreur de chargement des UFR : " + e.getMessage());
        }
    }

    @FXML
    private void onOuvrirFormulaireAjout() {
        ouvrirFormulaire(null);
    }

    private void ouvrirFormulaire(Ufr ufrExistante) {
        boolean estModification = (ufrExistante != null);

        TextInputDialog dialogue = new TextInputDialog(estModification ? ufrExistante.getNom() : "");
        dialogue.setTitle(estModification ? "Modifier une UFR" : "Ajouter une UFR");
        dialogue.setHeaderText(null);
        dialogue.setContentText("Nom de l'UFR :");

        dialogue.showAndWait().ifPresent(nomSaisi -> {
            try {
                if (estModification) {
                    ufrExistante.setNom(nomSaisi);
                    ufrService.modifier(ufrExistante);
                } else {
                    Ufr nouvelleUfr = new Ufr();
                    nouvelleUfr.setNom(nomSaisi);
                    ufrService.ajouter(nouvelleUfr);
                }
                Alertes.afficherSucces("UFR enregistree avec succes");
                chargerDonnees();
            } catch (ChampInvalideException e) {
                Alertes.afficherErreur(e.getMessage());
            } catch (Exception e) {
                Alertes.afficherErreur("Erreur technique : " + e.getMessage());
            }
        });
    }

    private void supprimer(Ufr ufr) {
        boolean confirme = Alertes.demanderConfirmation(
                "Supprimer l'UFR \"" + ufr.getNom() + "\" ? Cette action est irreversible.");

        if (confirme) {
            try {
                ufrService.supprimer(ufr.getIdUfr());
                Alertes.afficherSucces("UFR supprimee");
                chargerDonnees();
            } catch (Exception e) {
                Alertes.afficherErreur("Erreur lors de la suppression : " + e.getMessage());
            }
        }
    }
}