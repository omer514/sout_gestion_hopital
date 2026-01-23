package com.gestionhopital.gestionhopital.web;

import com.gestionhopital.gestionhopital.entities.RendezVous;
import com.gestionhopital.gestionhopital.repositories.MedecinRepository;
import com.gestionhopital.gestionhopital.repositories.PatientRepository;
import com.gestionhopital.gestionhopital.repositories.RendezVousRepository;
import com.gestionhopital.gestionhopital.services.RendezVousService;
import lombok.AllArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
@AllArgsConstructor
public class RendezVousController {
    private final RendezVousService rendezvousService;
    private final PatientRepository patientRepository;
    private final MedecinRepository medecinRepository;
    private final RendezVousRepository rendezvousRepository; // Injecté pour les filtres

    @GetMapping("/admin/rendezvous")
    public String list(Model model, Principal principal) {
        // 1. Récupérer l'utilisateur connecté
        String emailConnecte = principal.getName();
        
        // 2. Vérifier les rôles de l'utilisateur
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isMedecin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("MEDECIN"));
        
        List<RendezVous> maListe;
        
        if (isMedecin) {
            // Le médecin ne voit que SES rendez-vous
            maListe = rendezvousRepository.findByMedecinEmail(emailConnecte);
            model.addAttribute("titrePage", "Mon Agenda Médical");
        } else {
            // L'admin et l'accueil voient TOUT
            maListe = rendezvousService.findAll();
            model.addAttribute("titrePage", "Agenda Global de l'Hôpital");
        }

        model.addAttribute("listRDV", maListe);
        return "rendezvous/list";
    }

    @GetMapping("/admin/formRendezVous")
    public String form(Model model) {
        model.addAttribute("rendezvous", new RendezVous());
        model.addAttribute("patients", patientRepository.findAll());
        model.addAttribute("medecins", medecinRepository.findAll());
        return "rendezvous/form";
    }

    @PostMapping("/admin/saveRendezVous")
    public String save(RendezVous rdv, Model model) {
        try {
            rendezvousService.saveRendezVous(rdv);
            return "redirect:/admin/rendezvous";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("patients", patientRepository.findAll());
            model.addAttribute("medecins", medecinRepository.findAll());
            return "rendezvous/form";
        }
    }

    @GetMapping("/admin/confirmerRDV")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MEDECIN')") // Bloque l'accès si c'est l'ACCUEIL qui tape l'URL
    public String confirmer(@RequestParam Long id) {
        rendezvousService.confirmerRendezVous(id);
        return "redirect:/admin/rendezvous";
    }

    @GetMapping("/admin/annulerRDV")
    public String annuler(@RequestParam Long id, @RequestParam(defaultValue = "Indisponibilité du médecin") String motif) {
        rendezvousService.annulerRendezVousAvecMotif(id, motif);
        return "redirect:/admin/rendezvous";
    }

    @GetMapping("/admin/marquerPresent")
    public String marquerPresent(@RequestParam Long id) {
        rendezvousService.marquerPresent(id);
        return "redirect:/admin/rendezvous";
    }

    // Cette méthode sera utilisée pour l'étape suivante : la consultation
    @GetMapping("/admin/demarrerConsultation")
    public String demarrerConsultation(@RequestParam Long id, Model model) {
        // Logique à venir pour créer la consultation
        return "redirect:/admin/rendezvous"; 
    }
}