package com.gestionhopital.gestionhopital.web;

import com.gestionhopital.gestionhopital.entities.Specialite;
import com.gestionhopital.gestionhopital.services.SpecialiteService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@AllArgsConstructor
@PreAuthorize("hasRole('ADMIN')") // Seul l'admin configure les spécialités
public class SpecialiteController {

    private SpecialiteService specialiteService;

    // 1. Afficher la liste des spécialités avec pagination et recherche
    @GetMapping("/admin/specialites")
    public String list(Model model,
                       @RequestParam(name = "page", defaultValue = "0") int p,
                       @RequestParam(name = "size", defaultValue = "5") int s,
                       @RequestParam(name = "keyword", defaultValue = "") String kw) {
        
        Page<Specialite> pageSpecs = specialiteService.searchSpecialites(kw, PageRequest.of(p, s));
        
        model.addAttribute("listSpecialites", pageSpecs.getContent());
        model.addAttribute("pages", new int[pageSpecs.getTotalPages()]);
        model.addAttribute("currentPage", p);
        model.addAttribute("keyword", kw);
        
        return "specialites/list"; // Vers le fichier HTML
    }

    // 2. Formulaire d'ajout
    @GetMapping("/admin/formSpecialite")
    public String form(Model model) {
        model.addAttribute("specialite", new Specialite());
        return "specialites/form";
    }

    // 3. Sauvegarder (Ajout ou Modification)
    @PostMapping("/admin/saveSpecialite")
    public String save(Specialite specialite) {
        specialiteService.saveSpecialite(specialite);
        return "redirect:/admin/specialites";
    }

    // 4. Supprimer une spécialité
    @GetMapping("/admin/deleteSpecialite")
    public String delete(Long id, String keyword, int page) {
        specialiteService.deleteSpecialite(id);
        return "redirect:/admin/specialites?page=" + page + "&keyword=" + keyword;
    }

    // 5. Formulaire de modification
    @GetMapping("/admin/editSpecialite")
    public String edit(Model model, Long id) {
        Specialite s = specialiteService.getSpecialite(id);
        model.addAttribute("specialite", s);
        return "specialites/editForm";
    }
}