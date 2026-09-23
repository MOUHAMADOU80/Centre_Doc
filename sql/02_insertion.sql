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