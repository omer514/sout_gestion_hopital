package com.gestionhopital.gestionhopital.repositories;

import com.gestionhopital.gestionhopital.entities.Specialite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SpecialiteRepository extends JpaRepository<Specialite, Long> {
    // Pour chercher une spécialité par son nom (utile pour la barre de recherche)
    Page<Specialite> findByNomContains(String kw, Pageable pageable);
}