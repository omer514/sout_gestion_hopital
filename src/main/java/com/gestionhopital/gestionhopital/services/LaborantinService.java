package com.gestionhopital.gestionhopital.services;

import com.gestionhopital.gestionhopital.entities.Laborantin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LaborantinService {
    Laborantin saveLaborantin(Laborantin laborantin, String email, String password, String photo);
    Laborantin getLaborantin(Long id);
    Page<Laborantin> searchLaborantins(String kw, Pageable pageable);
    void deleteLaborantin(Long id);
    Laborantin updateLaborantin(Laborantin laborantin);
}