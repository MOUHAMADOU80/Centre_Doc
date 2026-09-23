package sn.ugb.centredoc.controller.gestionnaire;

import java.util.List;
import java.util.Map;

import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import sn.ugb.centredoc.model.Document;
import sn.ugb.centredoc.model.Gestionnaire;
import sn.ugb.centredoc.model.enums.TypeDocument;
import sn.ugb.centredoc.service.DocumentService;
import sn.ugb.centredoc.util.Alertes;

public class GestionnaireDashboardHomeController {

    @FXML private Label labelNbDocuments;
    @FXML private Label labelNbTelechargements;
    @FXML private PieChart graphiqueRepartition;

    private final DocumentService documentService = new DocumentService();

    @FXML
    public void initialize() {
        Gestionnaire gestionnaire = GestionnaireDashboardController.gestionnaireConnecte();
        if (gestionnaire == null) {
            return;
        }

        try {
            List<Document> documents = documentService.listerParUfr(gestionnaire.getIdUfr());
            labelNbDocuments.setText(String.valueOf(documents.size()));

            long nbTelechargements = 0;
            long nbTheses = 0;
            long nbMemoires = 0;
            for (Document doc : documents) {
                nbTelechargements += documentService.historiqueDocument(doc.getIdDocument()).size();
                if (doc.getType() == TypeDocument.THESE) {
                    nbTheses++;
                } else if (doc.getType() == TypeDocument.MEMOIRE) {
                    nbMemoires++;
                }
            }
            labelNbTelechargements.setText(String.valueOf(nbTelechargements));

            graphiqueRepartition.getData().add(new PieChart.Data("These", nbTheses));
            graphiqueRepartition.getData().add(new PieChart.Data("Memoire", nbMemoires));

        } catch (Exception e) {
            Alertes.afficherErreur("Erreur de chargement des statistiques : " + e.getMessage());
        }
    }
}
