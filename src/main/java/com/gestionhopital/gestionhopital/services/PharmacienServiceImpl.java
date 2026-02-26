package com.gestionhopital.gestionhopital.services;

import com.gestionhopital.gestionhopital.entities.AppUser;
import com.gestionhopital.gestionhopital.entities.Pharmacien;
import com.gestionhopital.gestionhopital.repositories.PharmacienRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class PharmacienServiceImpl implements PharmacienService {

    private PharmacienRepository pharmacienRepository;
    private AccountService accountService;

    @Override
    @Transactional
    public Pharmacien savePharmacien(Pharmacien pharmacien, String email, String password, String photo) {
        AppUser user;

        if (pharmacien.getId() != null) {
            Pharmacien existing = pharmacienRepository.findById(pharmacien.getId())
                    .orElseThrow(() -> new RuntimeException("Pharmacien non trouvé"));
            user = existing.getUser();
            user.setEmail(email);
            user.setUsername(email);
            user.setPhoto(photo);
            if (password != null && !password.isEmpty()) {
                user.setPassword(password); 
            }
            pharmacien.setUser(user);
        } else {
            user = accountService.addNewUser(email, password, email, password, photo);
            accountService.addRoleToUser(email, "PHARMACIE");
            pharmacien.setUser(user);
        }
        return pharmacienRepository.save(pharmacien);
    }

    @Override
    public Pharmacien getPharmacien(Long id) {
        return pharmacienRepository.findById(id).orElse(null);
    }

    @Override
    public Page<Pharmacien> searchPharmaciens(String kw, Pageable pageable) {
        return pharmacienRepository.findByNomContainsOrPrenomContains(kw, kw, pageable);
    }

    @Override
    public void deletePharmacien(Long id) {
        pharmacienRepository.deleteById(id);
    }

    @Override
    public Pharmacien updatePharmacien(Pharmacien pharmacien) {
        return pharmacienRepository.save(pharmacien);
    }
}