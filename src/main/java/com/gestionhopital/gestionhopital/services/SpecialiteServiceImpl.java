package com.gestionhopital.gestionhopital.services;

import com.gestionhopital.gestionhopital.entities.Specialite;
import com.gestionhopital.gestionhopital.repositories.SpecialiteRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class SpecialiteServiceImpl implements SpecialiteService {

    private SpecialiteRepository specialiteRepository;

    @Override
    public Specialite saveSpecialite(Specialite specialite) {
        return specialiteRepository.save(specialite);
    }

    @Override
    public Specialite getSpecialite(Long id) {
        return specialiteRepository.findById(id).orElse(null);
    }

    @Override
    public Page<Specialite> searchSpecialites(String kw, Pageable pageable) {
        return specialiteRepository.findByNomContains(kw, pageable);
    }

    @Override
    public List<Specialite> getAllSpecialites() {
        return specialiteRepository.findAll();
    }

    @Override
    public void deleteSpecialite(Long id) {
        specialiteRepository.deleteById(id);
    }

    @Override
    public Specialite updateSpecialite(Specialite specialite) {
        return specialiteRepository.save(specialite); // save() fait l'update si l'ID existe
    }
}