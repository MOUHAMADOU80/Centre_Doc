package sn.ugb.centredoc.controller.gestionnaire;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import sn.ugb.centredoc.model.Document;
import sn.ugb.centredoc.model.enums.NiveauAcces;
import sn.ugb.centredoc.model.enums.TypeDocument;

/**
 * Controleur du contenu du formulaire (utilise dans un Dialog, voir DocumentsController).
 * Correspond a l'ecran "Formulaire d'ajout/modification de document" de la maquette.
 */
public class FormulaireDocumentController {

    @FXML private TextField champTitre;
    @FXML private TextField champAuteur;
    @FXML private TextField champEncadrant;
    @FXML private TextField champAnnee;
    @FXML private ComboBox<TypeDocument> champType;
    @FXML private TextField champDiscipline;
    @FXML private TextArea champResume;
    @FXML private TextField champMotsCles;
    @FXML private TextField champCheminPdf;
    @FXML private ComboBox<NiveauAcces> champNiveauAcces;

    private Document documentEnEdition;

    @FXML
    public void initialize() {
        champType.getItems().setAll(TypeDocument.values());
        champNiveauAcces.getItems().setAll(NiveauAcces.values());
        champNiveauAcces.setValue(NiveauAcces.TELECHARGEABLE);
    }

    /** Pre-remplit le formulaire pour une modification. Appeler avant l'affichage du Dialog. */
    public void preRemplir(Document doc) {
        this.documentEnEdition = doc;
        champTitre.setText(doc.getTitre());
        champAuteur.setText(doc.getAuteur());
        champEncadrant.setText(doc.getEncadrant());
        champAnnee.setText(String.valueOf(doc.getAnnee()));
        champType.setValue(doc.getType());
        champDiscipline.setText(doc.getDiscipline());
        champResume.setText(doc.getResume());
        champMotsCles.setText(doc.getMotsCles());
        champCheminPdf.setText(doc.getCheminPdf());
        champNiveauAcces.setValue(doc.getNiveauAcces());
    }

    /**
     * Construit (ou met a jour) le Document a partir des champs saisis.
     * La validation des champs obligatoires reste geree par DocumentService (ChampInvalideException).
     */
    public Document construireDocument() {
        Document doc = (documentEnEdition != null) ? documentEnEdition : new Document();

        doc.setTitre(champTitre.getText());
        doc.setAuteur(champAuteur.getText());
        doc.setEncadrant(champEncadrant.getText());

        try {
            doc.setAnnee(Integer.parseInt(champAnnee.getText().trim()));
        } catch (NumberFormatException e) {
            doc.setAnnee(0); // sera rejete par DocumentService (annee invalide)
        }

        doc.setType(champType.getValue());
        doc.setDiscipline(champDiscipline.getText());
        doc.setResume(champResume.getText());
        doc.setMotsCles(champMotsCles.getText());
        doc.setCheminPdf(champCheminPdf.getText());
        doc.setNiveauAcces(champNiveauAcces.getValue());

        return doc;
    }
}
