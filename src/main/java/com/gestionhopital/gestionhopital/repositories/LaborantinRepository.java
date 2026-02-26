package com.gestionhopital.gestionhopital.repositories;

import com.gestionhopital.gestionhopital.entities.Laborantin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LaborantinRepository extends JpaRepository<Laborantin, Long> {
    // Indispensable pour la méthode searchLaborantins du Service
    Page<Laborantin> findByNomContainsOrPrenomContains(String nom, String prenom, Pageable pageable);
}