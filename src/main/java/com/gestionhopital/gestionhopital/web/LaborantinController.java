package com.gestionhopital.gestionhopital.web;

import com.gestionhopital.gestionhopital.entities.Laborantin;
import com.gestionhopital.gestionhopital.services.EmailService;
import com.gestionhopital.gestionhopital.services.LaborantinService;
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
import java.util.UUID;

@Controller
@AllArgsConstructor
public class LaborantinController {

    private LaborantinService laborantinService;
    private EmailService emailService;

    @GetMapping("/admin/laborantins")
    public String listLaborantins(Model model,
                                 @RequestParam(name = "page", defaultValue = "0") int page,
                                 @RequestParam(name = "size", defaultValue = "5") int size,
                                 @RequestParam(name = "keyword", defaultValue = "") String keyword) {
        Page<Laborantin> pageLaborantins = laborantinService.searchLaborantins(keyword, PageRequest.of(page, size));
        model.addAttribute("listLaborantins", pageLaborantins.getContent());
        model.addAttribute("pages", new int[pageLaborantins.getTotalPages()]);
        model.addAttribute("currentPage", page);
        model.addAttribute("keyword", keyword);
        return "laborantins/list";
    }

    @GetMapping("/admin/formLaborantin")
    public String formLaborantin(Model model) {
        model.addAttribute("laborantin", new Laborantin());
        model.addAttribute("mode", "new");
        return "laborantins/form";
    }

    @GetMapping("/admin/editLaborantin")
    public String editLaborantin(Model model, @RequestParam(name = "id") Long id) {
        Laborantin laborantin = laborantinService.getLaborantin(id);
        if (laborantin == null) throw new RuntimeException("Laborantin introuvable");
        model.addAttribute("laborantin", laborantin);
        model.addAttribute("mode", "edit");
        return "laborantins/form";
    }

    @PostMapping("/admin/saveLaborantin")
    public String saveLaborantin(Laborantin laborantin, 
                                @RequestParam String email, 
                                @RequestParam String password,
                                @RequestParam("imageFile") MultipartFile imageFile,
                                @RequestParam(name = "mode", defaultValue = "new") String mode) throws IOException {
        
        String photoName;
        if (!imageFile.isEmpty()) {
            photoName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
            String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/photos/";
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();
            Path path = Paths.get(uploadDir + photoName);
            Files.write(path, imageFile.getBytes());
        } else {
            if (mode.equals("edit") && laborantin.getUser() != null) {
                photoName = laborantin.getUser().getPhoto();
            } else {
                photoName = "default.png";
            }
        }

        laborantinService.saveLaborantin(laborantin, email, password, photoName);

        String subject = mode.equals("new") ? "🏥 Compte Laborantin créé" : "🏥 Mise à jour compte Laborantin";
        String content = "Bonjour M/Mme " + laborantin.getNom() + ",\n\n" +
                         "Vos accès Laborantin :\n" +
                         "Identifiant : " + email + "\n" +
                         "Mot de passe : " + password + "\n\nAdministration.";
        
        emailService.sendSimpleEmail(email, subject, content);
        return "redirect:/admin/laborantins";
    }

    @GetMapping("/admin/deleteLaborantin")
    public String deleteLaborantin(@RequestParam(name = "id") Long id, String keyword, int page) {
        laborantinService.deleteLaborantin(id);
        return "redirect:/admin/laborantins?page=" + page + "&keyword=" + keyword;
    }


    @GetMapping("/admin/laborantinDetails")
public String laborantinDetails(Model model, @RequestParam(name = "id") Long id) {
    Laborantin laborantin = laborantinService.getLaborantin(id);
    if (laborantin == null) throw new RuntimeException("Laborantin introuvable");
    
    model.addAttribute("laborantin", laborantin);
    return "laborantins/details";
}
}