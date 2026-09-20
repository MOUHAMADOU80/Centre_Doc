package sn.ugb.centredoc.model;

import sn.ugb.centredoc.model.enums.Role;

public class Administrateur extends Utilisateur {
    public Administrateur() { super(); }

    public Administrateur(String nom, String prenom, String email) {
        super(nom, prenom, email);
    }

    @Override
    public Role getRole() { return Role.ADMIN; }
}