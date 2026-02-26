package com.gestionhopital.gestionhopital.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Analyse {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private TypeAnalyse typeAnalyse; // Ex: Glycémie

    @ManyToOne
    private Consultation consultation; // La consultation qui a généré la demande

    private String resultat; // Rempli par le laborantin plus tard
    private String commentaireLabo;
    
    private LocalDateTime dateDemande;
    private LocalDateTime dateResultat;

    // Statut : DEMANDE, EN_COURS, TERMINE
    private String statut; 

    @ManyToOne
    private AppUser laborantin; // L'utilisateur (LABO) qui a fait l'analyse
}