================================================================================
 SYSTEME D'INFORMATION DES CENTRES DE DOCUMENTATION DE L'UGB
 Gestion des theses et memoires - Projet POO Java / Interface Graphique
================================================================================

Licence 3 Informatique / MIAGE - Annee Universitaire 2025-2026
Universite Gaston Berger de Saint-Louis, Senegal


--------------------------------------------------------------------------------
1. EQUIPE
--------------------------------------------------------------------------------

Trinome :
  -Mouhamadou Gaye- Module Administrateur
      Gestion des utilisateurs, gestion des UFR, authentification, socle
      technique (connexion base de donnees, Maven, structure du projet)

  - Maoudo Diaw - Module Gestionnaire
      Gestion du fonds documentaire (ajout/modification/suppression de
      documents), tableau de bord gestionnaire, module Statistiques

  - Ousmane Sane  - Module Etudiant
      Recherche et consultation des documents, fiche detaillee,
      telechargement, historique des telechargements


--------------------------------------------------------------------------------
2. TECHNOLOGIES UTILISEES
--------------------------------------------------------------------------------

  - Langage    : Java 21 (JDK)
  - Interface  : JavaFX 21.0.2
  - Base de donnees : MySQL 8.4 (via WampServer)
  - Acces BD   : JDBC (mysql-connector-j 9.7.0)
  - Build      : Apache Maven


--------------------------------------------------------------------------------
3. PREREQUIS A INSTALLER
--------------------------------------------------------------------------------

  a) JDK 21 ou superieur
     Verifier avec : java -version

  b) Apache Maven
     Verifier avec : mvn -v
     Telechargement : https://maven.apache.org/download.cgi
     (extraire l'archive et ajouter le dossier "bin" au PATH systeme)

  c) WampServer (fournit MySQL et phpMyAdmin)
     Telechargement : https://www.wampserver.com/
     Version 64 bits recommandee. Installer les "Visual C++ Redistributable
     Packages" proposes sur le site avant l'installation de WAMP si demande.


--------------------------------------------------------------------------------
4. INSTALLATION DE LA BASE DE DONNEES
--------------------------------------------------------------------------------

  1. Lancer WampServer. Attendre que l'icone dans la barre des taches
     devienne VERTE (MySQL et Apache demarres).

  2. Ouvrir phpMyAdmin dans un navigateur :
         http://localhost/phpmyadmin
     (ou http://localhost/phpmyadmin5.2.3/ selon la version installee)
     Connexion : utilisateur "root", mot de passe vide.

  3. Onglet "SQL", coller le contenu du fichier :
         sql/01_creation.sql
     puis cliquer sur "Executer".
     Cela cree la base "centre_doc" et ses 4 tables (ufr, utilisateurs,
     documents, telechargements).

  4. Toujours dans l'onglet "SQL" (base "centre_doc" selectionnee), coller
     le contenu du fichier :
         sql/02_insertion.sql
     puis "Executer".
     Cela insere les UFR, un compte administrateur, des etudiants et des
     documents de test.

  5. Verifier que les fichiers PDF de test sont presents dans le dossier
     "storage/pdfs/" a la racine du projet (ils sont deja inclus dans
     l'archive). Ce sont ces fichiers que l'application copie lors d'un
     telechargement.


--------------------------------------------------------------------------------
5. COMPILATION ET LANCEMENT
--------------------------------------------------------------------------------

  Depuis un terminal, a la racine du projet (ou se trouve le fichier
  pom.xml) :

      mvn clean compile exec:java

  Cela compile l'ensemble du code et ouvre l'ecran de connexion de
  l'application.

  Alternative (dans un IDE comme VS Code ou IntelliJ) : ouvrir la classe
  MainApp.java et lancer son execution directement.


--------------------------------------------------------------------------------
6. COMPTES DE TEST
--------------------------------------------------------------------------------

  ADMINISTRATEUR (mot de passe requis)
      Email        : admin@ugb.edu.sn
      Mot de passe : admin123

  ETUDIANTS (sans mot de passe : nom + prenom + email + code etudiant)
      Nom : Diop     Prenom : Awa        Email : awa.diop@ugb.edu.sn
      Code etudiant : P331156

      Nom : Ndiaye   Prenom : Moussa     Email : moussa.ndiaye@ugb.edu.sn
      Code etudiant : P302544

      Nom : Fall     Prenom : Ibrahima   Email : ibrahima.fall@ugb.edu.sn
      Code etudiant : P361123

      Nom : Sow      Prenom : Aissatou   Email : aissatou.sow@ugb.edu.sn
      Code etudiant : P321789

      Nom : Kane     Prenom : Ousmane    Email : ousmane.kane@ugb.edu.sn
      Code etudiant : P345678

  GESTIONNAIRE
      [A COMPLETER PAR L'EQUIPE : email + mot de passe du compte
      gestionnaire de test cree via l'espace Administrateur]


--------------------------------------------------------------------------------
7. MODULES ET FONCTIONNALITES IMPLEMENTEES
--------------------------------------------------------------------------------

  Module Administrateur :
      - Creation, modification, suppression d'un compte gestionnaire
      - Affectation d'un gestionnaire a une UFR
      - Gestion des UFR (ajout, modification, suppression)
      - Recherche d'un utilisateur par nom ou email
      - Tableau de bord avec statistiques globales

  Module Gestionnaire :
      - Ajout, modification, suppression d'une these ou d'un memoire
      - Definition du niveau d'acces (TELECHARGEABLE / CONSULTATION_SEULE /
        RESTREINT)
      - Le gestionnaire ne gere que les documents de sa propre UFR (regle
        metier n4)
      - Module Statistiques (repartition par discipline, par annee,
        activite par UFR, documents les plus telecharges)

  Module Etudiant :
      - Recherche de documents (titre, auteur, mot-cle, annee, UFR,
        discipline)
      - Fiche detaillee d'un document, avec apercu du resume avant
        telechargement
      - Telechargement du fichier PDF, avec verification du niveau d'acces
      - Historique personnel des telechargements
      - Les documents en acces restreint (embargo) sont visibles avec un
        badge, mais leur fiche detaillee et leur telechargement sont
        refuses (regle metier n3)


--------------------------------------------------------------------------------
8. REGLES METIER IMPLEMENTEES
--------------------------------------------------------------------------------

  1. Un etudiant ne peut telecharger un document que si son niveau d'acces
     est TELECHARGEABLE.
  2. Un document CONSULTATION_SEULE n'expose que ses metadonnees et son
     resume (pas de telechargement du PDF).
  3. Un document RESTREINT (embargo) n'est ni consultable ni telechargeable
     par l'etudiant.
  4. Un gestionnaire ne gere que les documents de son UFR.
  5. L'email d'un utilisateur doit etre un email UGB (format @ugb.edu.sn).
  6. Les identifiants (email, code etudiant) sont uniques.

  Toutes les violations de ces regles levent une exception personnalisee
  dediee (AccesRefuseException, DoublonException, ChampInvalideException,
  DocumentIntrouvableException, AuthentificationException,
  UtilisateurIntrouvableException, UfrIntrouvableException), jamais un
  simple message affiche ou un retour null.

  Toutes les listes et collections (utilisateurs, documents, resultats de
  recherche, historiques) sont gerees avec les collections java.util
  (ArrayList, HashMap, etc.), sans tableau natif.


--------------------------------------------------------------------------------
9. STRUCTURE DU PROJET
--------------------------------------------------------------------------------

  Centre_Doc/
  |-- pom.xml
  |-- README.txt
  |-- sql/
  |   |-- 01_creation.sql
  |   |-- 02_insertion.sql
  |-- storage/
  |   |-- pdfs/                  (fichiers PDF des documents de test)
  |-- demo/                      (video de demonstration)
  |-- src/main/
      |-- java/sn/ugb/centredoc/
      |   |-- MainApp.java
      |   |-- model/             (classes metier + enums)
      |   |-- exception/         (exceptions personnalisees)
      |   |-- dao/                (acces base de donnees)
      |   |-- service/            (regles metier)
      |   |-- controller/
      |   |   |-- admin/
      |   |   |-- gestionnaire/
      |   |   |-- etudiant/
      |   |-- util/               (Session, Validateur, HashUtil, Alertes)
      |-- resources/
          |-- fxml/                (un ecran par fichier)
          |-- css/
          |-- images/


--------------------------------------------------------------------------------
10. LIMITATIONS CONNUES
--------------------------------------------------------------------------------

  - Les fichiers PDF de demonstration sont des documents generiques utilises
    pour illustrer le fonctionnement du telechargement (le contenu reel des
    theses/memoires n'est pas fourni).
  - Le bouton "Ajouter a mes favoris" (ecran Fiche detaillee) fonctionne en
    memoire uniquement, sans persistance en base : c'est un ajout visuel
    hors perimetre du sujet.
  - [A COMPLETER SI D'AUTRES LIMITATIONS SONT IDENTIFIEES PAR L'EQUIPE]


--------------------------------------------------------------------------------
11. VIDEO DE DEMONSTRATION
--------------------------------------------------------------------------------

  La video de demonstration montrant le lancement de l'application, la
  navigation entre les trois espaces (Administrateur, Gestionnaire,
  Etudiant) et les principales fonctionnalites est disponible :

  [ ] Dans l'archive, dossier demo/ (fichier .mp4)
  


================================================================================
