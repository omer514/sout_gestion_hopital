package com.gestionhopital.gestionhopital.repositories;

import com.gestionhopital.gestionhopital.entities.Pharmacien;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PharmacienRepository extends JpaRepository<Pharmacien, Long> {
    // Indispensable pour la méthode searchPharmaciens du Service
    Page<Pharmacien> findByNomContainsOrPrenomContains(String nom, String prenom, Pageable pageable);
}