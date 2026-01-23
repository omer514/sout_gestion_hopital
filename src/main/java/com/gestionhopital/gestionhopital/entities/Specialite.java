package com.gestionhopital.gestionhopital.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Collection;

/**
 * Entité représentant une spécialité médicale (ex: Cardiologie, Dentaire).
 */
@Entity
@Data // Génère les Getters, Setters, toString, equals et hashCode
@NoArgsConstructor // Génère un constructeur sans argument (obligatoire pour JPA)
@AllArgsConstructor // Génère un constructeur avec tous les arguments
public class Specialite {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nom; // Le nom de la spécialité (ex: Pédiatrie)

    private String description; // Une brève description de la spécialité

    /* * RELATION : Une spécialité possède plusieurs médecins.
     * NOTE : Cette partie est commentée car nous n'avons pas encore créé l'entité Medecin.
     * Une fois l'entité Medecin créée, nous décommenterons ces lignes.
     */
    
    @OneToMany(mappedBy = "specialite", fetch = FetchType.LAZY)
    private Collection<Medecin> medecins;
}