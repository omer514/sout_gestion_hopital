package com.gestionhopital.gestionhopital.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class LignePrescription {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Medicament medicament;

    @ManyToOne
    private Consultation consultation;

    // --- Détails de la prise ---
    private String posologie; 
    private int dureeTraitement; 
    private int quantite; // <-- AJOUTÉ : Nombre de boites ou plaquettes
    private String instructions; 

    // --- Suivi de l'achat ---
    private boolean achatExterne; 
    private boolean confirmeAchete; 
    private LocalDateTime dateConfirmation; 
    private String agentValidateur; 
}