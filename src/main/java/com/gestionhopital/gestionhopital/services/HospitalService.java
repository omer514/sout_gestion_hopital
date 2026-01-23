package com.gestionhopital.gestionhopital.services;

import com.gestionhopital.gestionhopital.entities.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HospitalService {
    Patient savePatient(Patient patient);
    Page<Patient> chercherPatients(String keyword, Pageable pageable);
    Patient getPatient(Long id);
    void deletePatient(Long id);
}