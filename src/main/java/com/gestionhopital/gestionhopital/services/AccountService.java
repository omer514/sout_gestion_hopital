package com.gestionhopital.gestionhopital.services;

import com.gestionhopital.gestionhopital.entities.AppRole;
import com.gestionhopital.gestionhopital.entities.AppUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AccountService {
    // Création et Gestion
    AppUser addNewUser(String username, String password, String email, String confirmPassword, String photo );
    AppRole addNewRole(String roleName);
    void addRoleToUser(String username, String roleName);
    void removeRoleFromUser(String username, String roleName); // Utile pour les modifs

    void toggleUserStatus(String username);

    void updateUserInfos(String username, String email, List<String> roles);
    void updatePassword(String username, String newPassword);
    void updatePhoto(String username, String newPhotoName);
    
    // Lecture et Recherche
    AppUser loadUserByUsername(String username);
    List<AppUser> listUsers();
    Page<AppUser> searchUsers(String keyword, Pageable pageable);
    List<AppRole> listRoles(); // Indispensable pour remplir les listes déroulantes des formulaires
    
    // Suppression
    void deleteUser(String username);
    // pour modifier le mot de passe de patient 
    void updateUserPassword(String username, String oldPassword, String newPassword);
}