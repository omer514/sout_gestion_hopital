package com.gestionhopital.gestionhopital.repositories;

import com.gestionhopital.gestionhopital.entities.Medecin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MedecinRepository extends JpaRepository<Medecin, Long> {
    // Pour la recherche par nom ou prénom
    Page<Medecin> findByNomContainsOrPrenomContains(String nom, String prenom, Pageable pageable);
    @Query("select m from Medecin m where m.appUser.email = :email")
    Medecin findByEmail(@Param("email") String email);
}