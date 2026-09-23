package sn.ugb.centredoc.util;

import sn.ugb.centredoc.exception.ChampInvalideException;

public class Validateur {

    public static void validerEmailUgb(String email) throws ChampInvalideException {
        if (email == null || email.isBlank()) {
            throw new ChampInvalideException("L'email ne peut pas etre vide");
        }
        if (!email.endsWith("@ugb.edu.sn")) {
            throw new ChampInvalideException("L'email doit etre un email UGB (@ugb.edu.sn)");
        }
    }

    public static void validerChampNonVide(String valeur, String nomChamp) throws ChampInvalideException {
        if (valeur == null || valeur.isBlank()) {
            throw new ChampInvalideException("Le champ '" + nomChamp + "' ne peut pas etre vide");
        }
    }
}