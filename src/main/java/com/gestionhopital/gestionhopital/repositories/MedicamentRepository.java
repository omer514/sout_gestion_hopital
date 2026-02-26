package com.gestionhopital.gestionhopital.repositories;
import com.gestionhopital.gestionhopital.entities.Medicament;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicamentRepository extends JpaRepository<Medicament, Long> {
}