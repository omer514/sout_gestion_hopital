package com.gestionhopital.gestionhopital.entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;
import java.util.List;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Consultation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Date dateConsultation;
    
    @Column(length = 5000)
    private String rapportExamen; 
    
    @Column(length = 2000)
    private String diagnostic;
    
    private double poids;
    private String tension; // <-- MODIFIÉ : String pour accepter "12/8"
    private double temperature;

    private String statut; // "EN_ATTENTE_LABO", "TERMINEE"

    @OneToOne
    private RendezVous rendezVous; 

    @OneToMany(mappedBy = "consultation", cascade = CascadeType.ALL)
    private List<LignePrescription> prescriptions;

    @OneToMany(mappedBy = "consultation", cascade = CascadeType.ALL)
    private List<Analyse> analyses;
}