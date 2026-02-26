package com.gestionhopital.gestionhopital.repositories;

import com.gestionhopital.gestionhopital.entities.Consultation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List ; 

public interface ConsultationRepository extends JpaRepository<Consultation, Long> {
    // Cette méthode permet de retrouver la consultation liée à un RDV précis
    // Utile pour transformer "Consulter" en "Dossier Médical"
    Consultation findByRendezVousId(Long rdvId);

    // Récupère toutes les consultations d'un patient via le RDV, classées par date (la plus récente en premier)
    List<Consultation> findByRendezVousPatientIdOrderByDateConsultationDesc(Long patientId);

    List<Consultation> findByRendezVous_Patient_IdOrderByDateConsultationDesc(Long patientId);
}
