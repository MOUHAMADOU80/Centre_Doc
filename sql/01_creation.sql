-- ============================================================
-- Script de creation de la base "centre_doc"
-- ============================================================

CREATE DATABASE IF NOT EXISTS centre_doc;
USE centre_doc;

CREATE TABLE ufr (
    id_ufr INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL
);

CREATE TABLE utilisateurs (
    id_utilisateur INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(50) NOT NULL,
    prenom VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    role VARCHAR(20) NOT NULL,
    ufr INT NULL,
    code_etudiant VARCHAR(20) NULL UNIQUE,
    mot_de_passe VARCHAR(255) NULL,
    CONSTRAINT fk_utilisateur_ufr FOREIGN KEY (ufr) REFERENCES ufr(id_ufr)
);

CREATE TABLE documents (
    id_document INT AUTO_INCREMENT PRIMARY KEY,
    titre VARCHAR(150) NOT NULL,
    auteur VARCHAR(100) NOT NULL,
    encadrant VARCHAR(100),
    annee INT NOT NULL,
    type VARCHAR(20) NOT NULL,
    ufr INT NOT NULL,
    discipline VARCHAR(100),
    resume TEXT,
    mots_cles VARCHAR(255),
    chemin_pdf VARCHAR(255),
    niveau_acces VARCHAR(25) NOT NULL,
    CONSTRAINT fk_document_ufr FOREIGN KEY (ufr) REFERENCES ufr(id_ufr)
);

CREATE TABLE telechargements (
    id_telechargement INT AUTO_INCREMENT PRIMARY KEY,
    utilisateur INT NOT NULL,
    document INT NOT NULL,
    date_telechargement DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_telechargement_utilisateur FOREIGN KEY (utilisateur) REFERENCES utilisateurs(id_utilisateur),
    CONSTRAINT fk_telechargement_document FOREIGN KEY (document) REFERENCES documents(id_document)
);