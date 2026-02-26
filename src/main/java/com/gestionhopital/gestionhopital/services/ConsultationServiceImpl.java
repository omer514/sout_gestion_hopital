package com.gestionhopital.gestionhopital.services;

import com.gestionhopital.gestionhopital.entities.*;
import com.gestionhopital.gestionhopital.repositories.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import java.util.List;
import java.time.LocalDateTime;

@Service
@Transactional
@AllArgsConstructor
public class ConsultationServiceImpl implements ConsultationService {
    private ConsultationRepository consultationRepository;
    private RendezVousRepository rendezVousRepository;
    private MedicamentRepository medicamentRepository;
    private TypeAnalyseRepository typeAnalyseRepository;
    private LignePrescriptionRepository lignePrescriptionRepository;
    private AnalyseRepository analyseRepository;
    private PatientRepository patientRepository; 

    @Override
    public List<Medicament> chargerMedicaments() {
        return medicamentRepository.findAll();
    }

    @Override
    public List<TypeAnalyse> chargerTypesAnalyses() {
        return typeAnalyseRepository.findAll();
    }

    @Override
public Consultation enregistrerConsultation(Consultation consultation, Long rdvId, 
                                             List<Long> medicamentIds, List<String> posologies, 
                                             List<Integer> durees, List<Integer> quantites, 
                                             List<Long> analyseIds) {
    
    RendezVous rdv = rendezVousRepository.findById(rdvId).orElse(null);
    if (rdv == null) return null;

    // --- 1. GESTION DU DOSSIER EXISTANT ---
    Consultation existingConsul = consultationRepository.findByRendezVousId(rdvId);
    if (existingConsul != null) {
        consultation.setId(existingConsul.getId());
    }

    consultation.setRendezVous(rdv);
    consultation.setDateConsultation(new Date());
    
    // --- 2. ACTIVATION AUTOMATIQUE DU COMPTE PATIENT ---
    Patient patient = rdv.getPatient();
    if (patient != null) {
        patient.setActive(true); 
        if (patient.getAppUser() != null) {
            patient.getAppUser().setActive(true); 
        }
        patientRepository.save(patient);
    }

    // --- 3. LOGIQUE PROFESSIONNELLE DES STATUTS ---
    // On passe le RDV en statut "EN_COURS" pour qu'il reste dans la liste du médecin
    // mais qu'on sache que le travail a commencé.
    rdv.setStatus(StatusRDV.EN_COURS);
    rendezVousRepository.save(rdv);

    // Statut interne de la consultation
    if (analyseIds != null && !analyseIds.isEmpty()) {
        consultation.setStatut("EN_ATTENTE_LABO");
    } else {
        consultation.setStatut("EN_COURS");
    }

    // Sauvegarde de la consultation
    Consultation savedConsul = consultationRepository.save(consultation);

    // --- 4. ENREGISTREMENT DE L'ORDONNANCE ---
    if (medicamentIds != null && !medicamentIds.isEmpty()) {
        for (int i = 0; i < medicamentIds.size(); i++) {
            Long mId = medicamentIds.get(i);
            
            if (i < posologies.size() && i < durees.size() && i < quantites.size()) {
                // On évite les doublons de lignes pour la même consultation
                if (!lignePrescriptionRepository.existsByConsultationIdAndMedicamentId(savedConsul.getId(), mId)) {
                    Medicament m = medicamentRepository.findById(mId).orElse(null);
                    if (m != null) {
                        LignePrescription ligne = LignePrescription.builder()
                                .consultation(savedConsul)
                                .medicament(m)
                                .posologie(posologies.get(i))
                                .dureeTraitement(durees.get(i))
                                .quantite(quantites.get(i))
                                .achatExterne(false)
                                .confirmeAchete(false)
                                .build();
                        lignePrescriptionRepository.save(ligne);
                    }
                }
            }
        }
    }

    // --- 5. ENREGISTREMENT DES ANALYSES ---
    if (analyseIds != null && !analyseIds.isEmpty()) {
        for (Long aId : analyseIds) {
            if (!analyseRepository.existsByConsultationIdAndTypeAnalyseId(savedConsul.getId(), aId)) {
                TypeAnalyse ta = typeAnalyseRepository.findById(aId).orElse(null);
                if (ta != null) {
                    Analyse analyse = Analyse.builder()
                            .consultation(savedConsul)
                            .typeAnalyse(ta)
                            .statut("DEMANDE")
                            .dateDemande(LocalDateTime.now())
                            .build();
                    analyseRepository.save(analyse);
                }
            }
        }
    }

    return savedConsul;
}
    @Override
@Transactional
public void ajouterPrescriptionRapide(Long consultationId, Long medicamentId, String posologie, int quantite, int dureeTraitement) {
    Consultation con = consultationRepository.findById(consultationId)
            .orElseThrow(() -> new RuntimeException("Consultation non trouvée"));

    Medicament med = medicamentRepository.findById(medicamentId)
            .orElseThrow(() -> new RuntimeException("Médicament non trouvé"));

    LignePrescription lp = LignePrescription.builder()
            .consultation(con)
            .medicament(med)
            .posologie(posologie)
            .quantite(quantite) // Utilise la valeur reçue
            .dureeTraitement(dureeTraitement) // Utilise la valeur reçue
            .instructions("Ajouté via ajustement rapide")
            .build();

    lignePrescriptionRepository.save(lp);
}
}