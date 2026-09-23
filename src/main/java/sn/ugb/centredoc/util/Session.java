package sn.ugb.centredoc.util;

import sn.ugb.centredoc.model.Utilisateur;

public class Session {
    private static Utilisateur utilisateurConnecte;

    private Session() {} // empeche de creer un objet Session

    public static void connecter(Utilisateur utilisateur) {
        utilisateurConnecte = utilisateur;
    }

    public static void deconnecter() {
        utilisateurConnecte = null;
    }

    public static Utilisateur getUtilisateurConnecte() {
        return utilisateurConnecte;
    }

    public static boolean estConnecte() {
        return utilisateurConnecte != null;
    }
}