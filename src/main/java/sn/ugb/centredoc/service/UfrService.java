package sn.ugb.centredoc.service;

import java.sql.SQLException;
import java.util.List;

import sn.ugb.centredoc.dao.UfrDAO;
import sn.ugb.centredoc.exception.ChampInvalideException;
import sn.ugb.centredoc.model.Ufr;
import sn.ugb.centredoc.util.Validateur;

public class UfrService {

    private final UfrDAO ufrDAO;

    public UfrService() {
        this.ufrDAO = new UfrDAO();
    }

    public void ajouter(Ufr ufr) throws ChampInvalideException, SQLException {
        Validateur.validerChampNonVide(ufr.getNom(), "nom de l'UFR");
        ufrDAO.ajouter(ufr);
    }

    public void modifier(Ufr ufr) throws ChampInvalideException, SQLException {
        Validateur.validerChampNonVide(ufr.getNom(), "nom de l'UFR");
        ufrDAO.modifier(ufr);
    }

    public void supprimer(int idUfr) throws SQLException {
        ufrDAO.supprimer(idUfr);
    }

    public List<Ufr> listerToutes() throws SQLException {
        return ufrDAO.listerTous();
    }

    public Ufr trouverParId(int idUfr) throws SQLException {
        return ufrDAO.trouverParId(idUfr);
    }
}