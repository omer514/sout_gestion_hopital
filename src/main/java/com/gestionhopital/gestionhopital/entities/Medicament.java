package com.gestionhopital.gestionhopital.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Medicament {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String nom;
    private String forme; // Comprimé, Sirop, Injection
    private String dosage; // 500mg, 1g, 5ml
    
    private double prixUnitaire;
    private int quantiteStock; // Stock interne à l'hôpital
    
    private boolean disponible; // Si false, le médicament est retiré de la vente
}