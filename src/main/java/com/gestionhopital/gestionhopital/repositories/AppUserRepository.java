package com.gestionhopital.gestionhopital.repositories;

import com.gestionhopital.gestionhopital.entities.AppUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUser, String> {
    AppUser findByUsername(String username);
    
    Page<AppUser> findByUsernameContains(String keyword, Pageable pageable);
}