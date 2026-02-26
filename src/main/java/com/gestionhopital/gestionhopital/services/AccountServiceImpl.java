package com.gestionhopital.gestionhopital.services;

import com.gestionhopital.gestionhopital.entities.AppRole;
import com.gestionhopital.gestionhopital.entities.AppUser;
import com.gestionhopital.gestionhopital.repositories.AppRoleRepository;
import com.gestionhopital.gestionhopital.repositories.AppUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class AccountServiceImpl implements AccountService {
    private AppUserRepository appUserRepository;
    private AppRoleRepository appRoleRepository;
    private PasswordEncoder passwordEncoder;

    @Override
    public AppUser addNewUser(String username, String password, String email, String confirmPassword , String photo) {
        if(!password.equals(confirmPassword)) throw new RuntimeException("Les mots de passe ne correspondent pas");
        AppUser appUser = AppUser.builder()
                .userId(UUID.randomUUID().toString())
                .username(username)
                .password(passwordEncoder.encode(password))
                .email(email)
                .photo(photo)
                .active(true)
                .roles(new ArrayList<>())
                .build();
        return appUserRepository.save(appUser);
    }

    @Override
    public AppRole addNewRole(String roleName) {
        AppRole appRole = appRoleRepository.findByRoleName(roleName);
        if(appRole != null) throw new RuntimeException("Le rôle existe déjà");
        appRole = AppRole.builder().roleName(roleName).build();
        return appRoleRepository.save(appRole);
    }

    @Override
    public void addRoleToUser(String username, String roleName) {
        AppUser appUser = appUserRepository.findByUsername(username);
        AppRole appRole = appRoleRepository.findByRoleName(roleName);
        if(appUser != null && appRole != null) {
            // Bonne pratique : vérifier si l'utilisateur n'a pas déjà le rôle
            if(!appUser.getRoles().contains(appRole)) {
                appUser.getRoles().add(appRole);
            }
        }
    }

    @Override
    public void removeRoleFromUser(String username, String roleName) {
        AppUser appUser = appUserRepository.findByUsername(username);
        AppRole appRole = appRoleRepository.findByRoleName(roleName);
        if(appUser != null && appRole != null) {
            appUser.getRoles().remove(appRole);
        }
    }

    @Override
    public AppUser loadUserByUsername(String username) {
        return appUserRepository.findByUsername(username);
    }

    @Override
    public List<AppUser> listUsers() {
        return appUserRepository.findAll();
    }

    @Override
    public Page<AppUser> searchUsers(String keyword, Pageable pageable) {
        return appUserRepository.findByUsernameContains(keyword, pageable);
    }

    @Override
    public List<AppRole> listRoles() {
        return appRoleRepository.findAll();
    }

    @Override
    public void deleteUser(String username) {
        AppUser user = appUserRepository.findByUsername(username);
        if(user != null) {
            appUserRepository.delete(user);
        }
    }


    @Override
public void toggleUserStatus(String username) {
    AppUser appUser = appUserRepository.findByUsername(username);
    if (appUser != null) {
        // Si true devient false, si false devient true
        appUser.setActive(!appUser.isActive());
        appUserRepository.save(appUser);
    }
}

@Override
public void updateUserInfos(String username, String email, List<String> roles) {
    AppUser user = appUserRepository.findByUsername(username);
    user.setEmail(email);
    
    // On vide les anciens rôles et on met les nouveaux
    user.getRoles().clear();
    roles.forEach(r -> {
        AppRole role = appRoleRepository.findByRoleName(r);
        user.getRoles().add(role);
    });
    appUserRepository.save(user);
}

@Override
public void updatePassword(String username, String newPassword) {
    AppUser user = appUserRepository.findByUsername(username);
    // On encode le nouveau mot de passe avant de sauvegarder
    user.setPassword(passwordEncoder.encode(newPassword));
    appUserRepository.save(user);
}

@Override
@Transactional
public void updatePhoto(String username, String newPhotoName) {
    AppUser user = appUserRepository.findByUsername(username);
    if (user != null) {
        String oldPhotoName = user.getPhoto();
        
        // 1. Supprimer l'ancienne photo du disque (si ce n'est pas default.png)
        if (oldPhotoName != null && !oldPhotoName.equals("default.png")) {
            try {
                String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/photos/";
                java.nio.file.Path oldPath = java.nio.file.Paths.get(uploadDir + oldPhotoName);
                java.nio.file.Files.deleteIfExists(oldPath);
            } catch (Exception e) {
                System.err.println("Erreur lors de la suppression de l'ancienne photo: " + e.getMessage());
            }
        }

        // 2. Mettre à jour avec la nouvelle photo
        user.setPhoto(newPhotoName);
        appUserRepository.save(user);
    }
}


@Override
public void updateUserPassword(String username, String oldPassword, String newPassword) {
    AppUser user = appUserRepository.findByUsername(username);
    
    // Vérifier si l'ancien mot de passe correspond (en utilisant PasswordEncoder)
    if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
        throw new RuntimeException("Ancien mot de passe incorrect");
    }
    
    // Encoder et sauvegarder le nouveau
    user.setPassword(passwordEncoder.encode(newPassword));
    appUserRepository.save(user);
}
}