package com.gestionhopital.gestionhopital.repositories;

import com.gestionhopital.gestionhopital.entities.Medecin;
import com.gestionhopital.gestionhopital.entities.RendezVous;
import com.gestionhopital.gestionhopital.entities.StatusRDV;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface RendezVousRepository extends JpaRepository<RendezVous, Long> {
    // Vérifie si le médecin a déjà un RDV à ce moment précis
    boolean existsByMedecinAndDateAndHeureAndStatusNot(Medecin medecin, Date date, String heure, StatusRDV status);

    List<RendezVous> findByMedecinId(Long medecinId);
    List<RendezVous> findByPatientId(Long patientId);

    // On traverse les relations : RendezVous -> Medecin -> AppUser -> Email
    @Query("select r from RendezVous r where r.medecin.appUser.email = :email")
    List<RendezVous> findByMedecinEmail(@Param("email") String email);
}