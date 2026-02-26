package com.gestionhopital.gestionhopital.services;

import com.gestionhopital.gestionhopital.entities.Pharmacien;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PharmacienService {
    Pharmacien savePharmacien(Pharmacien pharmacien, String email, String password, String photo);
    Pharmacien getPharmacien(Long id);
    Page<Pharmacien> searchPharmaciens(String kw, Pageable pageable);
    void deletePharmacien(Long id);
    Pharmacien updatePharmacien(Pharmacien pharmacien);
}