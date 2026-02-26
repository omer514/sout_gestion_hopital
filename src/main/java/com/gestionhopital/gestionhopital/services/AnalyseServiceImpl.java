package com.gestionhopital.gestionhopital.services;

import com.gestionhopital.gestionhopital.entities.Analyse;
import com.gestionhopital.gestionhopital.entities.AppUser;
import com.gestionhopital.gestionhopital.entities.TypeAnalyse;
import com.gestionhopital.gestionhopital.repositories.AnalyseRepository;
import com.gestionhopital.gestionhopital.repositories.TypeAnalyseRepository;
import com.gestionhopital.gestionhopital.repositories.AppUserRepository; // Ou AppUserRepository selon ton nom
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class AnalyseServiceImpl implements AnalyseService {

    private AnalyseRepository analyseRepository;
    private TypeAnalyseRepository typeAnalyseRepository;
    private AppUserRepository appUserRepository;

    @Override
    public Analyse prescrireAnalyse(Long typeAnalyseId, Long consultationId) {
        TypeAnalyse ta = typeAnalyseRepository.findById(typeAnalyseId)
                .orElseThrow(() -> new RuntimeException("Type d'analyse introuvable"));
        
        Analyse analyse = Analyse.builder()
                .typeAnalyse(ta)
                .dateDemande(LocalDateTime.now())
                .statut("DEMANDE")
                // Ici on liera la consultation quand ton module consultation sera prêt
                .build();
        
        return analyseRepository.save(analyse);
    }

    @Override
    public Page<Analyse> chercherAnalyses(String keyword, String statut, Pageable pageable) {
        // Si le statut est vide, on cherche tout, sinon on filtre par statut
        if (statut == null || statut.isEmpty()) {
            return analyseRepository.findByTypeAnalyseNomContains(keyword, pageable);
        }
        return analyseRepository.findByStatutAndTypeAnalyseNomContains(statut, keyword, pageable);
    }

    @Override
    public Analyse getAnalyse(Long id) {
        return analyseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Analyse introuvable"));
    }

    @Override
public void enregistrerResultat(Long idAnalyse, String resultat, String commentaire, String emailLaborantin) {
    Analyse analyse = getAnalyse(idAnalyse);
    
    AppUser laborantin = appUserRepository.findByEmail(emailLaborantin).orElse(null);
    
    analyse.setResultat(resultat);
    analyse.setCommentaireLabo(commentaire);
    analyse.setLaborantin(laborantin);
    analyse.setDateResultat(LocalDateTime.now());
    analyse.setStatut("TERMINE");
    
    analyseRepository.save(analyse);
}

@Override
public void enregistrerResultatsGroupes(List<Long> ids, List<String> valeurs, List<String> commentaires, String username) {
    for (int i = 0; i < ids.size(); i++) {
        // On récupère l'ID, la valeur et le commentaire pour chaque examen
        Long idAnalyse = ids.get(i);
        String resultat = (valeurs.size() > i) ? valeurs.get(i) : null;
        String commentaire = (commentaires.size() > i) ? commentaires.get(i) : "";

        // Si un résultat a été saisi, on utilise ta méthode existante pour l'enregistrer
        if (resultat != null && !resultat.trim().isEmpty()) {
            this.enregistrerResultat(idAnalyse, resultat, commentaire, username);
        }
    }
}
}