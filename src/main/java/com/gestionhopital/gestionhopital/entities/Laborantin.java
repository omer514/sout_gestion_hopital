package com.gestionhopital.gestionhopital.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Laborantin {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;
    private String prenom;
    private String telephone;
    private String specialite; // Ex: Bio-chimiste, Hématologue
    
    @OneToOne
    private AppUser user; // Le compte pour se connecter
}