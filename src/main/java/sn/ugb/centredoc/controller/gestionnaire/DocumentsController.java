package sn.ugb.centredoc.controller.gestionnaire;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import sn.ugb.centredoc.exception.AccesRefuseException;
import sn.ugb.centredoc.exception.ChampInvalideException;
import sn.ugb.centredoc.exception.DocumentIntrouvableException;
import sn.ugb.centredoc.exception.DoublonException;
import sn.ugb.centredoc.model.Document;
import sn.ugb.centredoc.model.Gestionnaire;
import sn.ugb.centredoc.service.DocumentService;
import sn.ugb.centredoc.util.Alertes;

public class DocumentsController {

    @FXML private TableView<Document> tableDocuments;
    @FXML private TableColumn<Document, String> colTitre;
    @FXML private TableColumn<Document, String> colAuteur;
    @FXML private TableColumn<Document, Number> colAnnee;
    @FXML private TableColumn<Document, String> colType;
    @FXML private TableColumn<Document, String> colAcces;
    @FXML private TableColumn<Document, Void> colActions;

    private final DocumentService documentService = new DocumentService();
    private Gestionnaire gestionnaire;

    @FXML
    public void initialize() {
        gestionnaire = GestionnaireDashboardController.gestionnaireConnecte();

        colTitre.setCellValueFactory(new PropertyValueFactory<>("titre"));
        colAuteur.setCellValueFactory(new PropertyValueFactory<>("auteur"));
        colAnnee.setCellValueFactory(new PropertyValueFactory<>("annee"));
        colType.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue().getType() != null ? d.getValue().getType().name() : ""));
        colAcces.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue().getNiveauAcces() != null ? d.getValue().getNiveauAcces().name() : ""));

        colActions.setCellFactory(colonne -> new TableCell<>() {
            private final Button boutonModifier = new Button("Modifier");
            private final Button boutonSupprimer = new Button("Supprimer");
            private final HBox boite = new HBox(6, boutonModifier, boutonSupprimer);

            {
                boutonModifier.setOnAction(e -> ouvrirFormulaire(getTableView().getItems().get(getIndex())));
                boutonSupprimer.setOnAction(e -> supprimer(getTableView().getItems().get(getIndex())));
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
        if (gestionnaire == null) {
            return;
        }
        try {
            List<Document> documents = documentService.listerParUfr(gestionnaire.getIdUfr());
            ObservableList<Document> lignes = FXCollections.observableArrayList(documents);
            tableDocuments.setItems(lignes);
        } catch (Exception e) {
            Alertes.afficherErreur("Erreur de chargement des documents : " + e.getMessage());
        }
    }

    @FXML
    private void onOuvrirFormulaireAjout() {
        ouvrirFormulaire(null);
    }

    private void ouvrirFormulaire(Document documentExistant) {
        boolean estModification = (documentExistant != null);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/formulaire_document.fxml"));
            Parent contenu = loader.load();
            FormulaireDocumentController controleurFormulaire = loader.getController();

            if (estModification) {
                controleurFormulaire.preRemplir(documentExistant);
            }

            Dialog<ButtonType> dialogue = new Dialog<>();
            dialogue.setTitle(estModification ? "Modifier un document" : "Ajouter un document");
            dialogue.getDialogPane().setContent(contenu);
            dialogue.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            Optional<ButtonType> resultat = dialogue.showAndWait();
            if (resultat.isPresent() && resultat.get() == ButtonType.OK) {
                Document doc = controleurFormulaire.construireDocument();
                if (estModification) {
                    documentService.modifier(doc, gestionnaire);
                } else {
                    documentService.ajouter(doc, gestionnaire);
                }
                Alertes.afficherSucces("Document enregistre avec succes");
                chargerDonnees();
            }

        } catch (ChampInvalideException | DoublonException | AccesRefuseException | DocumentIntrouvableException e) {
            Alertes.afficherErreur(e.getMessage());
        } catch (IOException e) {
            Alertes.afficherErreur("Erreur de chargement du formulaire : " + e.getMessage());
        } catch (Exception e) {
            Alertes.afficherErreur("Erreur technique : " + e.getMessage());
        }
    }

    private void supprimer(Document doc) {
        boolean confirme = Alertes.demanderConfirmation(
                "Supprimer le document \"" + doc.getTitre() + "\" ? Cette action est irreversible.");

        if (!confirme) {
            return;
        }
        try {
            documentService.supprimer(doc.getIdDocument(), gestionnaire);
            Alertes.afficherSucces("Document supprime");
            chargerDonnees();
        } catch (DocumentIntrouvableException | AccesRefuseException e) {
            Alertes.afficherErreur(e.getMessage());
        } catch (Exception e) {
            Alertes.afficherErreur("Erreur lors de la suppression : " + e.getMessage());
        }
    }
}
