package com.gestionhopital.gestionhopital.services;

import com.gestionhopital.gestionhopital.entities.Medecin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MedecinService {
    // La méthode clé qui va gérer la double création
    Medecin saveMedecin(Medecin medecin, String email, String password, String photo);
    
    Medecin getMedecin(Long id);
    Page<Medecin> searchMedecins(String kw, Pageable pageable);
    void deleteMedecin(Long id);
    Medecin updateMedecin(Medecin medecin);
}