package com.gestionhopital.gestionhopital.services;

import com.gestionhopital.gestionhopital.entities.*;
import java.util.List;

public interface ConsultationService {
    List<Medicament> chargerMedicaments();
    List<TypeAnalyse> chargerTypesAnalyses();
    
    // La méthode principale avec tous les paramètres nécessaires
    Consultation enregistrerConsultation(
        Consultation consultation, 
        Long rdvId, 
        List<Long> medicamentIds, 
        List<String> posologies, 
        List<Integer> durees, 
        List<Integer> quantites, 
        List<Long> analyseIds
    );

void ajouterPrescriptionRapide(Long consultationId, Long medicamentId, String posologie , int quantite , int dureeTraitement) ; 
}