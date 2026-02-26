package com.gestionhopital.gestionhopital.web;

import com.gestionhopital.gestionhopital.entities.Analyse;
import com.gestionhopital.gestionhopital.entities.Consultation;
import com.gestionhopital.gestionhopital.services.AnalyseService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@AllArgsConstructor
public class LaboController {
    private AnalyseService analyseService;

    @GetMapping("/labo/analyses")
    public String list(Model model,
                       @RequestParam(name = "page", defaultValue = "0") int page,
                       @RequestParam(name = "size", defaultValue = "10") int size,
                       @RequestParam(name = "keyword", defaultValue = "") String keyword,
                       @RequestParam(name = "statut", defaultValue = "DEMANDE") String statut) {
        
        // On garde la recherche par Analyse pour l'instant, 
        // mais le HTML groupera l'affichage
        Page<Analyse> pageAnalyses = analyseService.chercherAnalyses(keyword, statut, PageRequest.of(page, size));
        
        model.addAttribute("listAnalyses", pageAnalyses.getContent());
        model.addAttribute("pages", new int[pageAnalyses.getTotalPages()]);
        model.addAttribute("currentPage", page);
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentStatut", statut);
        
        return "labo/listAnalyses";
    }

    @GetMapping("/labo/saisirResultat")
    public String saisir(Model model, Long id) {
        // On récupère l'analyse cliquée
        Analyse analyseSelectionnee = analyseService.getAnalyse(id);
        // On récupère la consultation parente
        Consultation consultation = analyseSelectionnee.getConsultation();
        
        // On envoie la consultation (qui contient toutes les analyses du patient)
        model.addAttribute("consultation", consultation);
        model.addAttribute("patient", consultation.getRendezVous().getPatient());
        
        return "labo/saisieResultat";
    }

    @PostMapping("/labo/saveResultat")
    public String saveResultat(@RequestParam("idAnalyse") List<Long> ids, 
                               @RequestParam("resultat") List<String> resultats, 
                               @RequestParam("commentaire") List<String> commentaires, 
                               Authentication authentication) {
        
        // On boucle sur les listes reçues pour enregistrer chaque examen
        for (int i = 0; i < ids.size(); i++) {
            if (resultats.get(i) != null && !resultats.get(i).isEmpty()) {
                analyseService.enregistrerResultat(
                    ids.get(i), 
                    resultats.get(i), 
                    commentaires.get(i), 
                    authentication.getName()
                );
            }
        }
        
        return "redirect:/labo/analyses?statut=TERMINE";
    }
}