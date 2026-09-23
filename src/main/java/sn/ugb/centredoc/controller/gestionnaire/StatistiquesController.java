package sn.ugb.centredoc.controller.gestionnaire;

import java.util.Map;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import sn.ugb.centredoc.model.Document;
import sn.ugb.centredoc.service.StatistiqueService;
import sn.ugb.centredoc.util.Alertes;

public class StatistiquesController {

    @FXML private Label labelNbUtilisateurs;
    @FXML private Label labelNbDocuments;
    @FXML private Label labelNbTelechargements;
    @FXML private ListView<String> listeTopDocuments;
    @FXML private ListView<String> listeRepartitionDiscipline;

    private final StatistiqueService statistiqueService = new StatistiqueService();

    @FXML
    public void initialize() {
        try {
            Map<String, Long> global = statistiqueService.statistiquesGlobales();
            labelNbUtilisateurs.setText(String.valueOf(global.get("nombreUtilisateurs")));
            labelNbDocuments.setText(String.valueOf(global.get("nombreDocuments")));
            labelNbTelechargements.setText(String.valueOf(global.get("nombreTelechargements")));

            Map<Document, Long> top = statistiqueService.topDocumentsTelecharges(5);
            for (Map.Entry<Document, Long> entree : top.entrySet()) {
                listeTopDocuments.getItems().add(entree.getKey().getTitre() + " — " + entree.getValue() + " telechargement(s)");
            }

            Map<String, Long> repartition = statistiqueService.repartitionParDiscipline();
            for (Map.Entry<String, Long> entree : repartition.entrySet()) {
                listeRepartitionDiscipline.getItems().add(entree.getKey() + " : " + entree.getValue() + " document(s)");
            }

        } catch (Exception e) {
            Alertes.afficherErreur("Erreur de chargement des statistiques : " + e.getMessage());
        }
    }
}
