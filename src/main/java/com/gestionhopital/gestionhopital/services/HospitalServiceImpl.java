package com.gestionhopital.gestionhopital.services;

import com.gestionhopital.gestionhopital.entities.Patient;
import com.gestionhopital.gestionhopital.repositories.PatientRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class HospitalServiceImpl implements HospitalService {
    private PatientRepository patientRepository;

    @Override
    public Patient savePatient(Patient patient) {
        return patientRepository.save(patient);
    }

    @Override
    public Page<Patient> chercherPatients(String keyword, Pageable pageable) {
        return patientRepository.findByNomContains(keyword, pageable);
    }

    @Override
    public Patient getPatient(Long id) {
        return patientRepository.findById(id).orElse(null);
    }

    @Override
    public void deletePatient(Long id) {
        patientRepository.deleteById(id);
    }
}