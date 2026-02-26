package com.gestionhopital.gestionhopital.web;

import com.gestionhopital.gestionhopital.entities.Consultation;
import com.gestionhopital.gestionhopital.entities.Patient;
import com.gestionhopital.gestionhopital.entities.StatusRDV; 
import com.gestionhopital.gestionhopital.entities.RendezVous;
import com.gestionhopital.gestionhopital.repositories.AnalyseRepository;
import com.gestionhopital.gestionhopital.repositories.ConsultationRepository;
import com.gestionhopital.gestionhopital.repositories.PatientRepository;
import com.gestionhopital.gestionhopital.repositories.RendezVousRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Set;
import java.util.Arrays ; 

@Controller
@AllArgsConstructor
public class DashboardController {

    private final AnalyseRepository analyseRepository;
    private final PatientRepository patientRepository;
    private final RendezVousRepository rendezVousRepository;
    private final ConsultationRepository consultationRepository; // Ajout du final pour l'injection Lombok

    @GetMapping("/dashboard")
    public String index(Authentication authentication, Model model) {
        if (authentication == null) return "redirect:/login";

        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
        String username = authentication.getName();

        // 1. Si c'est l'ADMIN
        if (roles.contains("ADMIN")) {
            return "home";
        } 
        
        // 2. Si c'est le LABORANTIN
        if (roles.contains("LABO")) {
            long nbDemandes = analyseRepository.findByStatut("DEMANDE").size();
            model.addAttribute("totalEnAttente", nbDemandes);
            return "dashboards/laborantin";
        }

        // 3. Si c'est le MEDECIN
        if (roles.contains("MEDECIN")) {
            return "redirect:/admin/rendezvous"; 
        }

        // 4. SI C'EST LE PATIENT
        if (roles.contains("PATIENT")) {
        Patient p = patientRepository.findByAppUser_Username(username);
        
        if (p != null) {
            model.addAttribute("patient", p);
            
            // 1. Récupération de l'historique des consultations (Déjà bon)
            List<Consultation> historique = consultationRepository.findByRendezVous_Patient_IdOrderByDateConsultationDesc(p.getId());
            model.addAttribute("historique", historique);
            
            // 2. Logique de filtrage pour les RDV à venir (Comme pour le médecin)
            // On définit les statuts à exclure
            List<StatusRDV> exclusions = Arrays.asList(StatusRDV.TERMINE, StatusRDV.ANNULE);
            
            // On récupère uniquement les RDV "actifs"
            List<RendezVous> rdvsAvenir = rendezVousRepository.findByPatientIdAndStatusNotInOrderByDateAscHeureAsc(p.getId(), exclusions);
            
            // /!\ IMPORTANT : On utilise le nom "mesRdvs" pour correspondre au fichier HTML
            model.addAttribute("mesRdvs", rdvsAvenir);
        }
        
        return "dossiers/dossier_recap"; 
    }

        // Par défaut
        return "home";
    }
}