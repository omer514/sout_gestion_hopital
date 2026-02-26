-- Insertion de quelques médicaments de test
INSERT INTO medicament (nom, forme, dosage, prix_unitaire, quantite_stock, disponible) VALUES 
('Paracétamol', 'Comprimé', '500mg', 1500, 100, true),
('Amoxicilline', 'Gélule', '1g', 3500, 0, true), -- En rupture pour tester l'achat externe
('Sirop Touly', 'Sirop', '150ml', 2500, 20, true);

-- Insertion de quelques analyses de test
INSERT INTO type_analyse (nom, prix, valeurs_reference, unite) VALUES 
('Glycémie à jeun', 3000, '0.70 - 1.10', 'g/L'),
('Test Paludisme (TDR)', 1500, 'Négatif', 'N/A'),
('Numération Formule Sanguine (NFS)', 5000, '4.5 - 11.0', '10^3/µL');