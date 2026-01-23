package com.gestionhopital.gestionhopital.repositories;

import com.gestionhopital.gestionhopital.entities.AppRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppRoleRepository extends JpaRepository<AppRole, Long> {
    AppRole findByRoleName(String roleName);

    
}
