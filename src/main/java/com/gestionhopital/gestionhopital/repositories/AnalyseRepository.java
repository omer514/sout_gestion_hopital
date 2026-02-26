package com.gestionhopital.gestionhopital.repositories;

import com.gestionhopital.gestionhopital.entities.Analyse;
import org.springframework.data.jpa.repository.JpaRepository;

// AJOUTEZ CES DEUX LIGNES ICI :
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AnalyseRepository extends JpaRepository<Analyse, Long> {
    // Utile pour le laborantin : voir toutes les analyses demandées (non terminées)
    List<Analyse> findByStatut(String statut);
    
    boolean existsByConsultationIdAndTypeAnalyseId(Long consultationId, Long typeAnalyseId);
    
    Page<Analyse> findByTypeAnalyseNomContains(String nom, Pageable pageable);
    
    Page<Analyse> findByStatutAndTypeAnalyseNomContains(String statut, String nom, Pageable pageable);
}