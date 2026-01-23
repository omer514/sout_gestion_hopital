package com.gestionhopital.gestionhopital.ApiController;

import com.gestionhopital.gestionhopital.entities.AppRole;
import com.gestionhopital.gestionhopital.entities.AppUser;
import com.gestionhopital.gestionhopital.services.AccountService;
import com.gestionhopital.gestionhopital.services.EmailService; // Import ajouté
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
public class UserRestController {
    
    private AccountService accountService;
    private EmailService emailService; // Injection pour les notifications API

    @GetMapping("/users")
    public Page<AppUser> listUsers(
            @RequestParam(name = "page", defaultValue = "0") int p,
            @RequestParam(name = "size", defaultValue = "5") int s,
            @RequestParam(name = "keyword", defaultValue = "") String kw) {
        return accountService.searchUsers(kw, PageRequest.of(p, s));
    }

    @DeleteMapping("/users/{username}")
    public void delete(@PathVariable String username) {
        accountService.deleteUser(username);
    }

    @GetMapping("/roles")
    public List<AppRole> listRoles() {
        return accountService.listRoles();
    }

    @GetMapping("/users/{username}")
    public AppUser getUser(@PathVariable String username) {
        return accountService.loadUserByUsername(username);
    }

    // --- 1. CRÉATION VIA API + EMAIL ---
    @PostMapping(path = "/users", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AppUser saveUser(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String email,
            @RequestParam String confirmPassword,
            @RequestParam List<String> roles,
            @RequestParam("imageFile") MultipartFile imageFile) throws IOException {

        String photoName = "default.png";

        if (imageFile != null && !imageFile.isEmpty()) {
            photoName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
            String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/photos/";
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();
            Files.write(Paths.get(uploadDir + photoName), imageFile.getBytes());
        }

        AppUser user = accountService.addNewUser(username, password, email, confirmPassword, photoName);

        if (roles != null) {
            for (String roleName : roles) {
                accountService.addRoleToUser(username, roleName);
            }
        }

        // Notification Email de Bienvenue
        String subject = "🏥 Accès API GestHopital";
        String content = "Bonjour " + username + ",\n\nVotre compte a été créé via l'interface administrative.\n" +
                         "Vos identifiants : \n- Login: " + username + "\n- Password: " + password;
        emailService.sendSimpleEmail(email, subject, content);

        return user;
    }

    // --- 2. MISE À JOUR VIA API + EMAIL ---
    @PutMapping(path = "/users/{username}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AppUser updateUser(
            @PathVariable String username,
            @RequestParam String email,
            @RequestParam List<String> roles,
            @RequestParam(required = false) String newPassword,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) throws IOException {

        // Mise à jour infos de base
        accountService.updateUserInfos(username, email, roles);

        StringBuilder emailContent = new StringBuilder("Bonjour " + username + ",\n\nVotre profil API a été mis à jour.\n");

        // Gestion du mot de passe
        if (newPassword != null && !newPassword.isEmpty()) {
            accountService.updatePassword(username, newPassword);
            emailContent.append("- Nouveau mot de passe : ").append(newPassword).append("\n");
        }

        // Gestion de la photo
        if (imageFile != null && !imageFile.isEmpty()) {
            String photoName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
            String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/photos/";
            Files.write(Paths.get(uploadDir + photoName), imageFile.getBytes());
            accountService.updatePhoto(username, photoName);
        }

        // Envoi email de confirmation
        emailService.sendSimpleEmail(email, "✅ Mise à jour Profil API", emailContent.toString());

        return accountService.loadUserByUsername(username);
    }

    // --- 3. TOGGLE STATUS VIA API + EMAIL ---
    @PatchMapping("/users/{username}/toggle")
    public AppUser toggleStatus(@PathVariable String username) {
        accountService.toggleUserStatus(username);
        AppUser user = accountService.loadUserByUsername(username);

        String status = user.isActive() ? "ACTIVÉ" : "DÉSACTIVÉ";
        String subject = "⚠️ Changement de statut de compte";
        String content = "Bonjour " + username + ",\n\nLe statut de votre compte est désormais : " + status;
        
        emailService.sendSimpleEmail(user.getEmail(), subject, content);

        return user;
    }
}