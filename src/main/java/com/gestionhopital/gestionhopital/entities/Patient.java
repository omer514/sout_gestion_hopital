package com.gestionhopital.gestionhopital.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    @Size(min = 2, max = 50)
    private String nom;

    @NotEmpty
    @Size(min = 2, max = 50)
    private String prenom;

    private String email; // Optionnel

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dateNaissance;

    private String genre; // M ou F
    private String telephone;
    private String adresse;

    private boolean active = false; // Sera activé lors de la 1ère consultation (UC105)

    private String matricule; // Ex: PAT-2026-X

    @OneToOne
    private AppUser appUser; // Le compte utilisateur lié au patient
}