package sn.ugb.centredoc.model;

import sn.ugb.centredoc.model.enums.NiveauAcces;
import sn.ugb.centredoc.model.enums.TypeDocument;

public class Document {
    private int idDocument;
    private String titre;
    private String auteur;
    private String encadrant;
    private int annee;
    private TypeDocument type;
    private int idUfr;
    private String discipline;
    private String resume;
    private String motsCles;
    private String cheminPdf;
    private NiveauAcces niveauAcces;

    public Document() {}

    public Document(String titre, String auteur, int annee, TypeDocument type,
                     int idUfr, NiveauAcces niveauAcces) {
        this.titre = titre;
        this.auteur = auteur;
        this.annee = annee;
        this.type = type;
        this.idUfr = idUfr;
        this.niveauAcces = niveauAcces;
    }

    public int getIdDocument() { return idDocument; }
    public void setIdDocument(int idDocument) { this.idDocument = idDocument; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getAuteur() { return auteur; }
    public void setAuteur(String auteur) { this.auteur = auteur; }

    public String getEncadrant() { return encadrant; }
    public void setEncadrant(String encadrant) { this.encadrant = encadrant; }

    public int getAnnee() { return annee; }
    public void setAnnee(int annee) { this.annee = annee; }

    public TypeDocument getType() { return type; }
    public void setType(TypeDocument type) { this.type = type; }

    public int getIdUfr() { return idUfr; }
    public void setIdUfr(int idUfr) { this.idUfr = idUfr; }

    public String getDiscipline() { return discipline; }
    public void setDiscipline(String discipline) { this.discipline = discipline; }

    public String getResume() { return resume; }
    public void setResume(String resume) { this.resume = resume; }

    public String getMotsCles() { return motsCles; }
    public void setMotsCles(String motsCles) { this.motsCles = motsCles; }

    public String getCheminPdf() { return cheminPdf; }
    public void setCheminPdf(String cheminPdf) { this.cheminPdf = cheminPdf; }

    public NiveauAcces getNiveauAcces() { return niveauAcces; }
    public void setNiveauAcces(NiveauAcces niveauAcces) { this.niveauAcces = niveauAcces; }
}