package com.gestionhopital.gestionhopital.web;

import com.gestionhopital.gestionhopital.entities.Medecin;
import com.gestionhopital.gestionhopital.entities.Specialite;
import com.gestionhopital.gestionhopital.repositories.SpecialiteRepository;
import com.gestionhopital.gestionhopital.services.EmailService;
import com.gestionhopital.gestionhopital.services.MedecinService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Controller
@AllArgsConstructor
public class MedecinController {

    private MedecinService medecinService;
    private SpecialiteRepository specialiteRepository;
    private EmailService emailService;

    // 1. Liste des médecins
    @GetMapping("/admin/medecins")
    public String listMedecins(Model model,
                               @RequestParam(name = "page", defaultValue = "0") int page,
                               @RequestParam(name = "size", defaultValue = "5") int size,
                               @RequestParam(name = "keyword", defaultValue = "") String keyword) {
        Page<Medecin> pageMedecins = medecinService.searchMedecins(keyword, PageRequest.of(page, size));
        model.addAttribute("listMedecins", pageMedecins.getContent());
        model.addAttribute("pages", new int[pageMedecins.getTotalPages()]);
        model.addAttribute("currentPage", page);
        model.addAttribute("keyword", keyword);
        return "medecins/list";
    }

    // 2. Formulaire Ajout
    @GetMapping("/admin/formMedecin")
    public String formMedecin(Model model) {
        model.addAttribute("medecin", new Medecin());
        model.addAttribute("specialites", specialiteRepository.findAll());
        model.addAttribute("mode", "new");
        return "medecins/form";
    }

    // 3. Formulaire Modification
    @GetMapping("/admin/editMedecin")
    public String editMedecin(Model model, @RequestParam(name = "id") Long id) {
        Medecin medecin = medecinService.getMedecin(id);
        if (medecin == null) throw new RuntimeException("Médecin introuvable");
        
        model.addAttribute("medecin", medecin);
        model.addAttribute("specialites", specialiteRepository.findAll());
        model.addAttribute("mode", "edit");
        return "medecins/form";
    }

    // 4. Sauvegarde (Ajout et Modification)
    @PostMapping("/admin/saveMedecin")
    public String saveMedecin(Medecin medecin, 
                              @RequestParam String email, 
                              @RequestParam String password,
                              @RequestParam("imageFile") MultipartFile imageFile,
                              @RequestParam(name = "mode", defaultValue = "new") String mode) throws IOException {
        
        String photoName;

        // Gestion de la photo
        if (!imageFile.isEmpty()) {
            photoName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
            String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/photos/";
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();
            Path path = Paths.get(uploadDir + photoName);
            Files.write(path, imageFile.getBytes());
        } else {
            // Si modification et pas de nouvelle photo, on garde l'ancienne
            if (mode.equals("edit") && medecin.getAppUser() != null) {
                photoName = medecin.getAppUser().getPhoto();
            } else {
                photoName = "default.png";
            }
        }

        // Sauvegarde via le service
        medecinService.saveMedecin(medecin, email, password, photoName);

        // Envoi de l'email
        String subject = mode.equals("new") ? 
            "🏥 Création de votre compte Médecin" : 
            "🏥 Mise à jour de vos accès Médecin";

        String content = "Bonjour Dr " + medecin.getNom() + ",\n\n" +
                         "Vos accès au système GestHopital ont été " + (mode.equals("new") ? "créés" : "mis à jour") + ".\n" +
                         "Voici vos identifiants :\n" +
                         "------------------------------------------\n" +
                         "👉 Identifiant : " + email + "\n" +
                         "👉 Mot de passe : " + password + "\n" + 
                         "------------------------------------------\n\n" +
                         "Lien d'accès : http://localhost:8080/login\n\n" +
                         "Cordialement,\nL'Administration.";
        
        emailService.sendSimpleEmail(email, subject, content);
        
        return "redirect:/admin/medecins";
    }

    // 5. Suppression
    @GetMapping("/admin/deleteMedecin")
    public String deleteMedecin(@RequestParam(name = "id") Long id, String keyword, int page) {
        medecinService.deleteMedecin(id);
        return "redirect:/admin/medecins?page=" + page + "&keyword=" + keyword;
    }


    @GetMapping("/admin/medecinDetails")
public String medecinDetails(Model model, @RequestParam(name = "id") Long id) {
    Medecin medecin = medecinService.getMedecin(id);
    if (medecin == null) throw new RuntimeException("Médecin introuvable");
    
    model.addAttribute("medecin", medecin);
    return "medecins/details";
}
}