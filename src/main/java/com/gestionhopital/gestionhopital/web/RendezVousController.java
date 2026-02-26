package com.gestionhopital.gestionhopital.web;

import com.gestionhopital.gestionhopital.entities.Consultation;
import com.gestionhopital.gestionhopital.entities.Patient;
import com.gestionhopital.gestionhopital.entities.RendezVous;
import com.gestionhopital.gestionhopital.entities.StatusRDV;
import com.gestionhopital.gestionhopital.repositories.ConsultationRepository;
import com.gestionhopital.gestionhopital.repositories.MedecinRepository;
import com.gestionhopital.gestionhopital.repositories.PatientRepository;
import com.gestionhopital.gestionhopital.repositories.RendezVousRepository;
import com.gestionhopital.gestionhopital.services.ConsultationService;
import com.gestionhopital.gestionhopital.services.RendezVousService;
import com.gestionhopital.gestionhopital.entities.Medecin;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import java.util.Arrays; 
import java.util.List;

@Controller
@AllArgsConstructor
public class RendezVousController {
    private final RendezVousService rendezvousService;
    private final PatientRepository patientRepository;
    private final MedecinRepository medecinRepository;
    private final RendezVousRepository rendezvousRepository;
    private final ConsultationRepository consultationRepository;
    private final ConsultationService consultationService;

    @GetMapping("/admin/rendezvous")
    public String list(Model model, Principal principal) {
        String emailConnecte = principal.getName();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        boolean isMedecin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("MEDECIN"));
        
        List<RendezVous> maListe;
        if (isMedecin) {
            // LISTE DES EXCLUSIONS : On ne veut plus voir ANNULE et TERMINE
            List<StatusRDV> exclusions = Arrays.asList(StatusRDV.ANNULE, StatusRDV.TERMINE);
            
            // On récupère tout le reste (EN_ATTENTE, CONFIRME, etc.)
            maListe = rendezvousRepository.findByMedecinEmailAndStatusNotIn(emailConnecte, exclusions);
            
            model.addAttribute("titrePage", "Mon Agenda Actif");
        } else {
            maListe = rendezvousService.findAll();
            model.addAttribute("titrePage", "Agenda Global");
        }
        
        model.addAttribute("listRDV", maListe);
        return "rendezvous/list";
    }

    @GetMapping("/medecin/dossierPatient")
@PreAuthorize("hasAuthority('MEDECIN')")
public String dossierPatient(Model model, @RequestParam Long idRDV) {
    RendezVous rdv = rendezvousRepository.findById(idRDV)
            .orElseThrow(() -> new RuntimeException("Rendez-vous introuvable"));
    
    Patient patient = rdv.getPatient();
    
    // --- LOGIQUE AJOUTÉE POUR LES RDV ACTIFS ---
    // On définit ce qu'on ne veut PAS voir (les RDV finis ou annulés)
    List<StatusRDV> exclusions = Arrays.asList(StatusRDV.TERMINE, StatusRDV.ANNULE);
    
    // On récupère uniquement les RDV "en cours" ou "confirmés" pour ce patient
    List<RendezVous> rdvsAvenir = rendezvousRepository.findByPatientIdAndStatusNotInOrderByDateAscHeureAsc(patient.getId(), exclusions);
    // -------------------------------------------

    List<Consultation> historique = consultationRepository.findByRendezVousPatientIdOrderByDateConsultationDesc(patient.getId());
    
    model.addAttribute("rdvActuel", rdv);
    model.addAttribute("patient", patient);
    model.addAttribute("historique", historique); 
    model.addAttribute("mesRdvs", rdvsAvenir); // <-- ON ENVOIE LA LISTE FILTRÉE ICI
    model.addAttribute("listeMedicaments", consultationService.chargerMedicaments()); 
    
    return "dossiers/dossier_recap"; 
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
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MEDECIN')")
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

@GetMapping("/medecin/accepterRDV")
public String accepter(@RequestParam Long id) {
    rendezvousService.validerRendezVous(id);
    return "redirect:/admin/rendezvous";
}

@GetMapping("/medecin/refuserRDV")
public String refuser(@RequestParam Long id) {
    rendezvousService.refuserRendezVous(id, "Refusé par le médecin");
    return "redirect:/admin/rendezvous";
}

@GetMapping("/patient/nouveauSuivi")
public String nouveauSuivi(Model model, Principal principal) {
    // 1. Récupérer le patient connecté
    Patient patient = patientRepository.findByEmail(principal.getName());

    // 2. Trouver son dernier médecin via son dernier RDV
    RendezVous dernierRDV = rendezvousRepository.findFirstByPatientEmailOrderByDateDesc(principal.getName());
    
    RendezVous nouveauRDV = new RendezVous();
    nouveauRDV.setPatient(patient);
    
    // Si on trouve un ancien médecin, on le pré-affecte
    if (dernierRDV != null) {
        nouveauRDV.setMedecin(dernierRDV.getMedecin());
    }

    model.addAttribute("rendezvous", nouveauRDV);
    // On passe quand même la liste au cas où il veut changer, mais on focus sur le sien
    model.addAttribute("medecins", medecinRepository.findAll()); 
    
    return "patients/formRDV_Suivi";
}


   @PostMapping("/patient/saveDemandeRDV")
@PreAuthorize("hasAuthority('PATIENT')")
public String saveDemande(RendezVous rdv, Principal principal) {
    try {
        // On essaie de récupérer le patient par son username (identifiant de session)
        Patient p = patientRepository.findByAppUser_Username(principal.getName());
        
        // Sécurité si le username n'est pas trouvé, on tente l'email
        if (p == null) {
            p = patientRepository.findByEmail(principal.getName());
        }

        if (p == null) {
            System.err.println("ERREUR : Patient introuvable pour : " + principal.getName());
            return "redirect:/dashboard?error=Patient_Introuvable";
        }
        
        // On attache le patient trouvé au rendez-vous
        rdv.setPatient(p);
        rdv.setStatus(StatusRDV.A_VALIDER); 
        rdv.setPresent(false); 
        
        rendezvousRepository.save(rdv);
        return "redirect:/dashboard?success=Demande_envoyee";

    } catch (Exception e) {
        System.err.println("ERREUR SAVE RDV : " + e.getMessage());
        e.printStackTrace(); 
        return "redirect:/dashboard?error=Erreur_Technique";
    }
}

@PostMapping("/medecin/saveSuiviDirect")
@PreAuthorize("hasAuthority('MEDECIN')")
public String saveSuiviDirect(@RequestParam Long patientId, 
                              @RequestParam String date, 
                              @RequestParam String heure,
                              @RequestParam String motif,
                              Principal principal) {
    try {
        RendezVous rdv = new RendezVous();
        
        // 1. Récupérer le patient
        Patient p = patientRepository.findById(patientId).get();
        
        // 2. Récupérer le médecin connecté
        // (Vérifie si dans ton projet le médecin est trouvé par son email de session)
        Medecin m = medecinRepository.findByEmail(principal.getName());

        // 3. Configurer le RDV
        // Note: Conversion de String vers Date si nécessaire selon ton entité
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        rdv.setDate(sdf.parse(date));
        
        rdv.setHeure(heure);
        rdv.setMotif(motif);
        rdv.setPatient(p);
        rdv.setMedecin(m);
        rdv.setStatus(StatusRDV.CONFIRME); // Directement confirmé !
        rdv.setPresent(false);

        rendezvousRepository.save(rdv);
        
        // On redirige vers le même dossier patient avec un message de succès
        return "redirect:/medecin/dossierPatient?idRDV=" + rdv.getId() + "&success=RDV_ajoute";
        
    } catch (Exception e) {
        return "redirect:/admin/rendezvous?error=Erreur_lors_de_la_creation";
    }
}
}