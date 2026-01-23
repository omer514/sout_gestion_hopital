package com.gestionhopital.gestionhopital.repositories;

import com.gestionhopital.gestionhopital.entities.Medecin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedecinRepository extends JpaRepository<Medecin, Long> {
    // Pour la recherche par nom ou prénom
    Page<Medecin> findByNomContainsOrPrenomContains(String nom, String prenom, Pageable pageable);
}