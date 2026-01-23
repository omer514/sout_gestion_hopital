package com.gestionhopital.gestionhopital.ApiController;

import com.gestionhopital.gestionhopital.entities.Patient;
import com.gestionhopital.gestionhopital.services.HospitalService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/patients") // Préfixe pour éviter les conflits
public class PatientRestController {
    private HospitalService hospitalService;

    // Récupérer la liste paginée (JSON)
    @GetMapping
    public Page<Patient> getPatients(
            @RequestParam(name = "page", defaultValue = "0") int p,
            @RequestParam(name = "size", defaultValue = "5") int s,
            @RequestParam(name = "keyword", defaultValue = "") String kw) {
        return hospitalService.chercherPatients(kw, PageRequest.of(p, s));
    }

    // Récupérer un seul patient par son ID
    @GetMapping("/{id}")
    public Patient getPatient(@PathVariable Long id) {
        return hospitalService.getPatient(id);
    }

    // Créer un nouveau patient via API
    @PostMapping("/create")
    public Patient savePatient(@RequestBody Patient patient) {
        return hospitalService.savePatient(patient);
    }

    // Supprimer un patient via API
    @DeleteMapping("/delete/{id}")
    public void deletePatient(@PathVariable Long id) {
        hospitalService.deletePatient(id);
    }
}