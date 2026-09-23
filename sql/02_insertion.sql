USE centre_doc;

-- Quelques UFR de base
INSERT INTO ufr (nom) VALUES
('UFR Sciences Appliquees et Technologies'),
('UFR Sciences Economiques et de Gestion'),
('UFR Lettres et Sciences Humaines'),
('UFR Sciences Juridiques et Politiques');

-- Compte administrateur de test
-- Email : admin@ugb.edu.sn
-- Mot de passe : admin123
INSERT INTO utilisateurs (nom, prenom, email, role, mot_de_passe) VALUES
('Diallo', 'Admin', 'admin@ugb.edu.sn', 'ADMIN', 'JAvlGPq9JyTdtvBO6x2llnRI1+gxwIyPqCKAn3THIKk=');


USE centre_doc;

INSERT INTO `documents` (`id_document`, `titre`, `auteur`, `encadrant`, `annee`, `type`, `ufr`, `discipline`, `resume`, `mots_cles`, `chemin_pdf`, `niveau_acces`) VALUES
(NULL, 'Optimisation reseau', 'Modou SYLLA', 'Pr. MBAYE Maissa', '2026', 'MEMOIRE', '1', 'Architecture TCP/IP', 'Etude et optimisation des performances reseau dans un environnement universitaire.', 'TCP/IP, reseau, optimisation', 'C:\\Users\\ATIC\\Downloads\\optim_reseau.pdf', 'TELECHARGEABLE'),

(NULL, 'Securite des systemes distribues', 'Fatou NDIAYE', 'Pr. DIOP Amadou', '2025', 'THESE', '1', 'Cybersecurite', 'Analyse des vulnerabilites dans les architectures distribuees modernes.', 'securite, cybersecurite, systemes distribues', 'C:\\Users\\ATIC\\Downloads\\secu_distribuee.pdf', 'CONSULTATION_SEULE'),

(NULL, 'Intelligence artificielle et detection d intrusion', 'Ibrahima FALL', 'Pr. MBAYE Maissa', '2026', 'MEMOIRE', '1', 'Reseaux et Securite', 'Application du machine learning pour la detection d intrusions reseau.', 'IA, machine learning, IDS', 'C:\\Users\\ATIC\\Downloads\\ia_ids.pdf', 'RESTREINT'),

(NULL, 'Analyse des determinants de la croissance economique au Senegal', 'Awa DIOUF', 'Pr. SARR Cheikh', '2024', 'MEMOIRE', '2', 'Economie', 'Etude econometrique des facteurs de croissance sur la periode 2010-2023.', 'croissance, economie, econometrie', 'C:\\Users\\ATIC\\Downloads\\croissance_senegal.pdf', 'TELECHARGEABLE'),

(NULL, 'Gouvernance financiere des PME senegalaises', 'Moussa BA', 'Pr. GUEYE Ndeye', '2025', 'THESE', '2', 'Gestion', 'Analyse des pratiques de gouvernance financiere dans les petites et moyennes entreprises.', 'PME, finance, gouvernance', 'C:\\Users\\ATIC\\Downloads\\gouv_pme.pdf', 'TELECHARGEABLE'),

(NULL, 'La litterature orale wolof comme patrimoine culturel', 'Aissatou SOW', 'Pr. NDOUR Babacar', '2023', 'MEMOIRE', '3', 'Lettres Modernes', 'Recueil et analyse des contes et proverbes wolof transmis oralement.', 'litterature orale, wolof, patrimoine', 'C:\\Users\\ATIC\\Downloads\\litt_wolof.pdf', 'CONSULTATION_SEULE'),

(NULL, 'Histoire des royaumes precoloniaux du Senegal', 'Cheikh THIAM', 'Pr. DIALLO Oumar', '2025', 'THESE', '3', 'Histoire', 'Etude des structures politiques et sociales des royaumes du Senegal avant la colonisation.', 'histoire, royaumes, precolonial', 'C:\\Users\\ATIC\\Downloads\\royaumes_senegal.pdf', 'TELECHARGEABLE'),

(NULL, 'Le droit foncier au Senegal, entre coutume et modernite', 'Khady DIENG', 'Pr. FAYE Serigne', '2026', 'MEMOIRE', '4', 'Droit Prive', 'Analyse de la cohabitation entre droit coutumier et droit foncier moderne.', 'droit foncier, coutume, Senegal', 'C:\\Users\\ATIC\\Downloads\\droit_foncier.pdf', 'TELECHARGEABLE'),

(NULL, 'La responsabilite penale des personnes morales', 'Ousmane KANE', 'Pr. WADE Aminata', '2024', 'THESE', '4', 'Droit Penal', 'Etude comparative de la responsabilite penale des personnes morales en droit senegalais.', 'droit penal, personnes morales, responsabilite', 'C:\\Users\\ATIC\\Downloads\\resp_penale.pdf', 'RESTREINT'),

(NULL, 'Les enjeux de la decentralisation administrative', 'Mariama SECK', 'Pr. FAYE Serigne', '2026', 'MEMOIRE', '4', 'Droit Public', 'Analyse des defis de la decentralisation dans la gouvernance locale au Senegal.', 'decentralisation, administration, gouvernance locale', 'C:\\Users\\ATIC\\Downloads\\decentralisation.pdf', 'CONSULTATION_SEULE');