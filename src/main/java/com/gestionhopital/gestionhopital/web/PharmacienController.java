package com.gestionhopital.gestionhopital.web;

import com.gestionhopital.gestionhopital.entities.Pharmacien;
import com.gestionhopital.gestionhopital.services.EmailService;
import com.gestionhopital.gestionhopital.services.PharmacienService;
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
public class PharmacienController {

    private PharmacienService pharmacienService;
    private EmailService emailService;

    @GetMapping("/admin/pharmaciens")
    public String listPharmaciens(Model model,
                                 @RequestParam(name = "page", defaultValue = "0") int page,
                                 @RequestParam(name = "size", defaultValue = "5") int size,
                                 @RequestParam(name = "keyword", defaultValue = "") String keyword) {
        Page<Pharmacien> pagePharmaciens = pharmacienService.searchPharmaciens(keyword, PageRequest.of(page, size));
        model.addAttribute("listPharmaciens", pagePharmaciens.getContent());
        model.addAttribute("pages", new int[pagePharmaciens.getTotalPages()]);
        model.addAttribute("currentPage", page);
        model.addAttribute("keyword", keyword);
        return "pharmaciens/list";
    }

    @GetMapping("/admin/formPharmacien")
    public String formPharmacien(Model model) {
        model.addAttribute("pharmacien", new Pharmacien());
        model.addAttribute("mode", "new");
        return "pharmaciens/form";
    }

    @GetMapping("/admin/editPharmacien")
    public String editPharmacien(Model model, @RequestParam(name = "id") Long id) {
        Pharmacien pharmacien = pharmacienService.getPharmacien(id);
        if (pharmacien == null) throw new RuntimeException("Pharmacien introuvable");
        model.addAttribute("pharmacien", pharmacien);
        model.addAttribute("mode", "edit");
        return "pharmaciens/form";
    }

    @PostMapping("/admin/savePharmacien")
    public String savePharmacien(Pharmacien pharmacien, 
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
            if (mode.equals("edit") && pharmacien.getUser() != null) {
                photoName = pharmacien.getUser().getPhoto();
            } else {
                photoName = "default.png";
            }
        }

        pharmacienService.savePharmacien(pharmacien, email, password, photoName);

        String subject = mode.equals("new") ? "💊 Compte Pharmacien créé" : "💊 Mise à jour compte Pharmacien";
        String content = "Bonjour " + pharmacien.getNom() + ",\n\nIdentifiants Pharmacie :\nLogin : " + email + "\nPass : " + password;
        
        emailService.sendSimpleEmail(email, subject, content);
        return "redirect:/admin/pharmaciens";
    }

    @GetMapping("/admin/deletePharmacien")
    public String deletePharmacien(@RequestParam(name = "id") Long id, String keyword, int page) {
        pharmacienService.deletePharmacien(id);
        return "redirect:/admin/pharmaciens?page=" + page + "&keyword=" + keyword;
    }

    @GetMapping("/admin/pharmacienDetails")
public String pharmacienDetails(Model model, @RequestParam(name = "id") Long id) {
    Pharmacien pharmacien = pharmacienService.getPharmacien(id);
    if (pharmacien == null) throw new RuntimeException("Pharmacien introuvable");
    
    model.addAttribute("pharmacien", pharmacien);
    return "pharmaciens/details";
}
}