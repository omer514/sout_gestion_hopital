package com.gestionhopital.gestionhopital.web;

import com.gestionhopital.gestionhopital.entities.AppUser;
import com.gestionhopital.gestionhopital.services.AccountService;
import com.gestionhopital.gestionhopital.services.EmailService; // Import de ton service email

import lombok.AllArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@AllArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UsersController {
    
    private AccountService accountService;
    private EmailService emailService; // Injection du service email pour les notifications

    @GetMapping("/admin/users")
    public String listUsers(Model model,
                            @RequestParam(name = "page", defaultValue = "0") int p,
                            @RequestParam(name = "size", defaultValue = "5") int s,
                            @RequestParam(name = "keyword", defaultValue = "") String kw) {
        Page<AppUser> pageUsers = accountService.searchUsers(kw, PageRequest.of(p, s));
        model.addAttribute("listUsers", pageUsers.getContent());
        model.addAttribute("pages", new int[pageUsers.getTotalPages()]);
        model.addAttribute("currentPage", p);
        model.addAttribute("keyword", kw);
        return "users/list";
    }

    @GetMapping("/admin/deleteUser")
    public String delete(String username, String keyword, int page) {
        accountService.deleteUser(username);
        return "redirect:/admin/users?page=" + page + "&keyword=" + keyword;
    }

    @GetMapping("/admin/formUser")
    public String formUser(Model model) {
        model.addAttribute("appUser", new AppUser());
        model.addAttribute("allRoles", accountService.listRoles());
        return "users/form";
    }

    // --- 1. SAUVEGARDE ET ENVOI MAIL DE BIENVENUE ---
    @PostMapping("/admin/saveUser")
    public String saveUser(@RequestParam String username, 
                           @RequestParam String password,
                           @RequestParam String confirmPassword, 
                           @RequestParam String email,
                           @RequestParam(name = "roles", defaultValue = "") List<String> roles,
                           @RequestParam("imageFile") MultipartFile imageFile) throws IOException {
        
        String photoName = "default.png";

        if (!imageFile.isEmpty()) {
            photoName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
            String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/photos/";
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();
            Path path = Paths.get(uploadDir + photoName);
            Files.write(path, imageFile.getBytes());
        }

        // Création en base de données
        accountService.addNewUser(username, password, email, confirmPassword, photoName);
        for (String roleName : roles) {
            accountService.addRoleToUser(username, roleName);
        }

        // Préparation du Mail de Bienvenue
        String subject = "🏥 Vos accès au système GestHopital";
        String content = "Bonjour " + username + ",\n\n" +
                         "Votre compte agent a été créé avec succès.\n" +
                         "Voici vos identifiants pour vous connecter :\n" +
                         "------------------------------------------\n" +
                         "👉 Identifiant : " + username + "\n" +
                         "👉 Mot de passe : " + password + "\n" + 
                         "------------------------------------------\n\n" +
                         "Lien d'accès : http://localhost:8080/login\n\n" +
                         "Cordialement,\nL'Administration.";
        
        emailService.sendSimpleEmail(email, subject, content);
        
        return "redirect:/admin/users";
    }

    // --- 2. ACTIVATION / DÉSACTIVATION ET ALERTE MAIL ---
    @GetMapping("/admin/toggleUserStatus")
    public String toggleUserStatus(@RequestParam String username, 
                                   @RequestParam(defaultValue = "") String keyword, 
                                   @RequestParam(defaultValue = "0") int page) {
        
        accountService.toggleUserStatus(username);
        AppUser user = accountService.loadUserByUsername(username);

        // Envoi d'un mail d'information sur le nouveau statut
        String status = user.isActive() ? "ACTIVÉ (Accès autorisé)" : "DÉSACTIVÉ (Accès suspendu)";
        String subject = "⚠️ Statut de votre compte GestHopital";
        String content = "Bonjour " + username + ",\n\n" +
                         "Le statut de votre compte a été modifié par l'administrateur.\n" +
                         "Votre accès est désormais : " + status + ".\n\n" +
                         "Si vous pensez qu'il s'agit d'une erreur, veuillez contacter le support.";
        
        emailService.sendSimpleEmail(user.getEmail(), subject, content);

        return "redirect:/admin/users?page=" + page + "&keyword=" + keyword;
    }

    @GetMapping("/admin/userDetails")
    public String userDetails(Model model, @RequestParam String username) {
        AppUser user = accountService.loadUserByUsername(username);
        model.addAttribute("user", user);
        return "users/details";
    }

    @GetMapping("/admin/editUser")
    public String editUser(Model model, @RequestParam String username) {
        AppUser user = accountService.loadUserByUsername(username);
        model.addAttribute("appUser", user);
        model.addAttribute("allRoles", accountService.listRoles());
        return "users/editForm";
    }

    // --- 3. MISE À JOUR ET MAIL RÉCAPITULATIF ---
    @PostMapping("/admin/updateUser")
    public String updateUser(@RequestParam String username, 
                             @RequestParam String email,
                             @RequestParam(name = "roles", defaultValue = "") List<String> roles,
                             @RequestParam(required = false) String newPassword,
                             @RequestParam("imageFile") MultipartFile imageFile) throws IOException {
        
        // Mise à jour de base
        accountService.updateUserInfos(username, email, roles);
        
        String subject = "✅ Mise à jour de votre profil - GestHopital";
        StringBuilder content = new StringBuilder();
        content.append("Bonjour ").append(username).append(",\n\n")
               .append("Votre profil a été mis à jour par l'administrateur.\n\n")
               .append("Récapitulatif de vos accès :\n")
               .append("- Email : ").append(email).append("\n")
               .append("- Identifiant : ").append(username).append("\n");

        // Mot de passe modifié ?
        if (newPassword != null && !newPassword.isEmpty()) {
            accountService.updatePassword(username, newPassword);
            content.append("- NOUVEAU Mot de passe : ").append(newPassword).append("\n")
                   .append("\n⚠️ Veuillez utiliser ce nouveau mot de passe lors de votre prochaine connexion.\n");
        } else {
            content.append("- Mot de passe : (Inchangé)\n");
        }

        content.append("\nCordialement,\nL'Administration.");

        // Gestion de la photo
        if (!imageFile.isEmpty()) {
            String photoName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
            String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/photos/";
            java.nio.file.Files.write(java.nio.file.Paths.get(uploadDir + photoName), imageFile.getBytes());
            accountService.updatePhoto(username, photoName);
        }

        // Envoi du mail
        emailService.sendSimpleEmail(email, subject, content.toString());
        
        return "redirect:/admin/users";
    }
}