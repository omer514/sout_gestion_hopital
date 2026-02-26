package com.gestionhopital.gestionhopital.services;

import com.gestionhopital.gestionhopital.entities.AppUser;
import com.gestionhopital.gestionhopital.entities.Laborantin;
import com.gestionhopital.gestionhopital.repositories.LaborantinRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class LaborantinServiceImpl implements LaborantinService {

    private LaborantinRepository laborantinRepository;
    private AccountService accountService;

    @Override
    @Transactional
    public Laborantin saveLaborantin(Laborantin laborantin, String email, String password, String photo) {
        AppUser user;

        if (laborantin.getId() != null) {
            Laborantin existing = laborantinRepository.findById(laborantin.getId())
                    .orElseThrow(() -> new RuntimeException("Laborantin non trouvé"));
            user = existing.getUser();
            user.setEmail(email);
            user.setUsername(email);
            user.setPhoto(photo);
            if (password != null && !password.isEmpty()) {
                user.setPassword(password); 
            }
            laborantin.setUser(user);
        } else {
            user = accountService.addNewUser(email, password, email, password, photo);
            accountService.addRoleToUser(email, "LABO");
            laborantin.setUser(user);
        }
        return laborantinRepository.save(laborantin);
    }

    @Override
    public Laborantin getLaborantin(Long id) {
        return laborantinRepository.findById(id).orElse(null);
    }

    @Override
    public Page<Laborantin> searchLaborantins(String kw, Pageable pageable) {
        return laborantinRepository.findByNomContainsOrPrenomContains(kw, kw, pageable);
    }

    @Override
    public void deleteLaborantin(Long id) {
        laborantinRepository.deleteById(id);
    }

    @Override
    public Laborantin updateLaborantin(Laborantin laborantin) {
        return laborantinRepository.save(laborantin);
    }
}