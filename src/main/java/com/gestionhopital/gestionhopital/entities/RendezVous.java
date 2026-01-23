package com.gestionhopital.gestionhopital.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor
public class RendezVous {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;

    private String heure; // Format "14:30"
    
    private String motif;
    
    @Enumerated(EnumType.STRING)
    private StatusRDV status; // EN_ATTENTE, CONFIRME, ANNULE

    private boolean present = false;

    private String motifAnnulation;

    @ManyToOne
    private Patient patient;

    @ManyToOne
    private Medecin medecin;
}