package com.gestionhopital.gestionhopital.services;

import com.gestionhopital.gestionhopital.entities.AppUser;
import com.gestionhopital.gestionhopital.entities.Patient;
import com.gestionhopital.gestionhopital.repositories.PatientRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class HospitalServiceImpl implements HospitalService {
    private PatientRepository patientRepository;
    private AccountService accountService; // Injection de ton service de compte

    @Override
    public Patient savePatient(Patient patient) {
        // CAS : NOUVEAU PATIENT (Génération compte + matricule)
        if (patient.getId() == null) {
            // 1. Génération d'un matricule unique basé sur l'année et un nombre aléatoire
            // Format : PAT-2026-XXXX
            String matricule = "PAT-2026-" + (int)(Math.random() * 9000 + 1000);
            patient.setMatricule(matricule);
            
            // 2. Définition du mot de passe temporaire
            // On peut utiliser une règle simple : Patient + les 4 derniers chiffres du matricule
            String tempPassword = "Pass" + matricule.substring(matricule.lastIndexOf("-") + 1);
            
            // 3. Création du compte utilisateur (AppUser) via ton AccountService
            // On utilise le matricule comme username pour la connexion
            AppUser savedUser = accountService.addNewUser(
                matricule,              // username
                tempPassword,           // password
                patient.getEmail(),     // email
                tempPassword,           // confirmPassword
                "default.png"           // photo par défaut
            );
            
            // 4. Attribution du rôle PATIENT
            accountService.addRoleToUser(matricule, "PATIENT");
            
            // 5. Liaison du compte à l'entité Patient
            patient.setAppUser(savedUser);
            
            // 6. Statut initial : Inactif (Sera activé par le médecin lors du RDV - UC105)
            patient.setActive(false);
            savedUser.setActive(false); // On s'assure que l'utilisateur ne peut pas encore se loguer
        }
        
        // Sauvegarde finale du patient (et mise à jour si c'est un EDIT)
        return patientRepository.save(patient);
    }

    @Override
    public Page<Patient> chercherPatients(String keyword, Pageable pageable) {
        return patientRepository.findByNomContains(keyword, pageable);
    }

    @Override
    public Patient getPatient(Long id) {
        return patientRepository.findById(id).orElse(null);
    }

    @Override
    public void deletePatient(Long id) {
        patientRepository.deleteById(id);
    }
}