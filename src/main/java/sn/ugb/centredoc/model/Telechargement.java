package sn.ugb.centredoc.model;

import java.time.LocalDateTime;

public class Telechargement {
    private int idTelechargement;
    private int idUtilisateur;
    private int idDocument;
    private LocalDateTime dateTelechargement;

    public Telechargement() {}

    public Telechargement(int idUtilisateur, int idDocument, LocalDateTime dateTelechargement) {
        this.idUtilisateur = idUtilisateur;
        this.idDocument = idDocument;
        this.dateTelechargement = dateTelechargement;
    }

    public int getIdTelechargement() { return idTelechargement; }
    public void setIdTelechargement(int idTelechargement) { this.idTelechargement = idTelechargement; }

    public int getIdUtilisateur() { return idUtilisateur; }
    public void setIdUtilisateur(int idUtilisateur) { this.idUtilisateur = idUtilisateur; }

    public int getIdDocument() { return idDocument; }
    public void setIdDocument(int idDocument) { this.idDocument = idDocument; }

    public LocalDateTime getDateTelechargement() { return dateTelechargement; }
    public void setDateTelechargement(LocalDateTime dateTelechargement) { this.dateTelechargement = dateTelechargement; }
}