package com.gestionhopital.gestionhopital.repositories;

import com.gestionhopital.gestionhopital.entities.LignePrescription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LignePrescriptionRepository extends JpaRepository<LignePrescription, Long> {
    // Vérifie si un médicament est déjà dans l'ordonnance de cette consultation
    // Pour éviter de doubler les lignes quand le médecin ajoute des médicaments après le labo
    boolean existsByConsultationIdAndMedicamentId(Long consultationId, Long medicamentId);
}