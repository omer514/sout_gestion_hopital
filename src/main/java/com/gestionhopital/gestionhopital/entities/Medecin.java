package com.gestionhopital.gestionhopital.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor
public class Medecin {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String prenom;
    
    @Column(unique = true)
    private String tel;

    // Liaison avec le compte utilisateur (Sécurité)
    @OneToOne(cascade = CascadeType.ALL) 
    private AppUser appUser;

    // Liaison avec la spécialité
    @ManyToOne
    private Specialite specialite;
}