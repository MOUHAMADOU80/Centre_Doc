package sn.ugb.centredoc.model;

import sn.ugb.centredoc.model.enums.Role;

public class Etudiant extends Utilisateur {
    private String codeEtudiant;

    public Etudiant() { super(); }

    public Etudiant(String nom, String prenom, String email, String codeEtudiant) {
        super(nom, prenom, email);
        this.codeEtudiant = codeEtudiant;
    }

    public String getCodeEtudiant() { return codeEtudiant; }
    public void setCodeEtudiant(String codeEtudiant) { this.codeEtudiant = codeEtudiant; }

    @Override
    public Role getRole() { return Role.ETUDIANT; }
}