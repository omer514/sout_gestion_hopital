package com.gestionhopital.gestionhopital.web;

import com.gestionhopital.gestionhopital.entities.*;
import com.gestionhopital.gestionhopital.repositories.ConsultationRepository;
import com.gestionhopital.gestionhopital.repositories.RendezVousRepository;
import com.gestionhopital.gestionhopital.services.ConsultationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@AllArgsConstructor
public class ConsultationController {
    
    private ConsultationService consultationService;
    private RendezVousRepository rendezVousRepository;
    private ConsultationRepository consultationRepository;

    @GetMapping("/admin/demarrerConsultation")
    public String preparerConsultation(@RequestParam(name = "id") Long id, Model model) {
        RendezVous rdv = rendezVousRepository.findById(id).orElse(null);
        if (rdv == null) return "redirect:/admin/rendezvous";

        // On vérifie s'il y a déjà une consultation pour ce RDV
        Consultation consultation = consultationRepository.findByRendezVousId(id);
        
        // Si elle n'existe pas encore, on en crée une nouvelle (objet vide pour le formulaire)
        if (consultation == null) {
            consultation = new Consultation();
            consultation.setRendezVous(rdv);
        }
        
        model.addAttribute("consultation", consultation);
        model.addAttribute("rdv", rdv);
        model.addAttribute("listeMedicaments", consultationService.chargerMedicaments());
        model.addAttribute("listeAnalyses", consultationService.chargerTypesAnalyses());
        
        return "consultations/formConsultation"; 
    }

    @PostMapping("/admin/saveConsultation")
public String saveConsultation(Consultation consultation, 
                             @RequestParam Long rdvId,
                             @RequestParam(value = "medicamentIds", required = false) List<Long> medicamentIds,
                             @RequestParam(value = "posologies", required = false) List<String> posologies,
                             @RequestParam(value = "durees", required = false) List<Integer> durees,
                             @RequestParam(value = "quantites", required = false) List<Integer> quantites,
                             @RequestParam(value = "analyseIds", required = false) List<Long> analyseIds,
                             RedirectAttributes redirectAttributes) {
    
    // 1. On enregistre la consultation via le service
    // Le service va mettre le RDV en "EN_COURS" automatiquement
    consultationService.enregistrerConsultation(consultation, rdvId, medicamentIds, posologies, durees, quantites, analyseIds);
    
    // 2. SURTOUT PAS DE rdv.setStatus(StatusRDV.TERMINE) ICI !
    // On laisse le service gérer le statut EN_COURS.
    // Le patient restera ainsi dans la liste "Mon Agenda Actif".
    
    redirectAttributes.addFlashAttribute("success", "Modifications enregistrées. Le dossier reste ouvert.");
    return "redirect:/admin/rendezvous";
}

@GetMapping("/medecin/cloturerDossier")
public String cloturerDossier(@RequestParam Long id, RedirectAttributes redirectAttributes) {
    Consultation con = consultationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Consultation introuvable"));
    
    // Ici c'est correct : C'est seulement quand on CLOTURE que ça devient TERMINE
    con.setStatut("TERMINEE");
    consultationRepository.save(con);
    
    RendezVous rdv = con.getRendezVous();
    if (rdv != null) {
        rdv.setStatus(StatusRDV.TERMINE);
        rendezVousRepository.save(rdv);
    }
    
    redirectAttributes.addFlashAttribute("success", "Le dossier a été clôturé et le patient retiré de la liste.");
    return "redirect:/admin/rendezvous"; 
}

@PostMapping("/medecin/ajouterMedicamentRapide")
public String ajouterMedicamentRapide(@RequestParam Long consultationId, 
                                     @RequestParam Long medicamentId,
                                     @RequestParam String posologie,
                                     @RequestParam int quantite,
                                     @RequestParam int dureeTraitement,
                                     RedirectAttributes redirectAttributes) {
    
    // 1. Appel au service avec TOUS les paramètres
    consultationService.ajouterPrescriptionRapide(consultationId, medicamentId, posologie, quantite, dureeTraitement);
    
    // 2. Récupération de la consultation pour la redirection
    Consultation con = consultationRepository.findById(consultationId)
            .orElseThrow(() -> new RuntimeException("Consultation non trouvée"));
    
    // 3. Récupération de l'ID du rendez-vous
    Long rdvId = con.getRendezVous().getId(); 
    
    // 4. Notification de succès
    redirectAttributes.addFlashAttribute("success", "Le médicament a été ajouté avec succès au dossier.");
    
    // 5. Redirection vers le dossier de consultation en cours
    return "redirect:/medecin/dossierPatient?idRDV=" + rdvId;
}
}