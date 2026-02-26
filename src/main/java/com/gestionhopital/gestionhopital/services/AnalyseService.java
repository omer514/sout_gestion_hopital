package com.gestionhopital.gestionhopital.services;

import com.gestionhopital.gestionhopital.entities.Analyse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AnalyseService {
    // Pour le médecin : prescrire une analyse
    Analyse prescrireAnalyse(Long typeAnalyseId, Long consultationId);
    
    // Pour le laborantin : voir les tâches
    Page<Analyse> chercherAnalyses(String keyword, String statut, Pageable pageable);
    
    // Pour la saisie
    Analyse getAnalyse(Long id);
    
    // Pour valider le résultat final
    void enregistrerResultat(Long idAnalyse, String resultat, String commentaire, String emailLaborantin);

    // Dans AnalyseService.java
void enregistrerResultatsGroupes(List<Long> ids, List<String> valeurs, List<String> commentaires, String username);
}