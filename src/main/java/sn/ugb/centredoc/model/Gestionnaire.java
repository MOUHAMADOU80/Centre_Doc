package sn.ugb.centredoc.model;

import sn.ugb.centredoc.model.enums.Role;

public class Gestionnaire extends Utilisateur {
    private Integer idUfr;

    public Gestionnaire() { super(); }

    public Gestionnaire(String nom, String prenom, String email, Integer idUfr) {
        super(nom, prenom, email);
        this.idUfr = idUfr;
    }

    public Integer getIdUfr() { return idUfr; }
    public void setIdUfr(Integer idUfr) { this.idUfr = idUfr; }

    @Override
    public Role getRole() { return Role.GESTIONNAIRE; }
}