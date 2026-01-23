package com.gestionhopital.gestionhopital.services;

import com.gestionhopital.gestionhopital.entities.Specialite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface SpecialiteService {
    Specialite saveSpecialite(Specialite specialite);
    Specialite getSpecialite(Long id);
    Page<Specialite> searchSpecialites(String kw, Pageable pageable);
    List<Specialite> getAllSpecialites();
    void deleteSpecialite(Long id);
    Specialite updateSpecialite(Specialite specialite);
}