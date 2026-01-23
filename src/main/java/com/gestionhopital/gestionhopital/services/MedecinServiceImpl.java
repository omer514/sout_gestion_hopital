package com.gestionhopital.gestionhopital.services;

import com.gestionhopital.gestionhopital.entities.AppUser;
import com.gestionhopital.gestionhopital.entities.Medecin;
import com.gestionhopital.gestionhopital.repositories.MedecinRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class MedecinServiceImpl implements MedecinService {

    private MedecinRepository medecinRepository;
    private AccountService accountService;

    @Override
@Transactional
public Medecin saveMedecin(Medecin medecin, String email, String password, String photo) {
    AppUser user;

    // 1. CAS DE LA MODIFICATION (L'ID existe déjà)
    if (medecin.getId() != null) {
        // On récupère le médecin existant
        Medecin existingMedecin = medecinRepository.findById(medecin.getId())
                .orElseThrow(() -> new RuntimeException("Médecin non trouvé"));
        
        // On récupère son compte utilisateur
        user = existingMedecin.getAppUser();
        
        // Mise à jour des informations du compte
        user.setEmail(email);
        user.setUsername(email); // On garde l'email comme identifiant
        user.setPhoto(photo);
        
        // On ne change le password que s'il n'est pas vide
        if (password != null && !password.isEmpty()) {
            // Note: Si ton accountService possède une méthode pour encoder, utilise-la.
            // Sinon, assure-toi que le mot de passe est bien traité.
            user.setPassword(password); 
        }
        
        // On lie l'utilisateur mis à jour au médecin envoyé par le formulaire
        medecin.setAppUser(user);

    } else {
        // 2. CAS DE L'AJOUT (Nouvel utilisateur)
        user = accountService.addNewUser(
            email,      // username
            password,   // password
            email,      // email
            password,   // confirmPassword
            photo
        );
        
        // Attribution du rôle seulement à la création
        accountService.addRoleToUser(email, "MEDECIN");
        
        // Liaison
        medecin.setAppUser(user);
    }

    // 3. Sauvegarde (UPDATE si ID présent, INSERT si ID est null)
    return medecinRepository.save(medecin);
}

    // ... (les autres méthodes restent identiques)
    @Override
    public Medecin getMedecin(Long id) {
        return medecinRepository.findById(id).orElse(null);
    }

    @Override
    public Page<Medecin> searchMedecins(String kw, Pageable pageable) {
        return medecinRepository.findByNomContainsOrPrenomContains(kw, kw, pageable);
    }

    @Override
    public void deleteMedecin(Long id) {
        medecinRepository.deleteById(id);
    }

    @Override
    public Medecin updateMedecin(Medecin medecin) {
        return medecinRepository.save(medecin);
    }
}