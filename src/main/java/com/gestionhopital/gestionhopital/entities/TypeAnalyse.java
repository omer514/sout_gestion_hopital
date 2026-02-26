package com.gestionhopital.gestionhopital.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class TypeAnalyse {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String nom; // Glycémie, NFS, Paludisme
    private String description;
    private double prix;
    
    private String valeursReference; // Ex: [0.70 - 1.10]
    private String unite; // g/L, mg/dL
}