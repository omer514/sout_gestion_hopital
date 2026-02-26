package com.gestionhopital.gestionhopital.services;

import com.gestionhopital.gestionhopital.entities.RendezVous;
import java.util.List;

public interface RendezVousService {
    void saveRendezVous(RendezVous rdv) throws Exception;
    List<RendezVous> findAll();
    void annulerRendezVous(Long id);
    void confirmerRendezVous(Long id);
    void marquerPresent(Long id);
    void annulerRendezVousAvecMotif(Long id, String motif);
    void validerRendezVous(Long id); 
    void refuserRendezVous(Long id, String motif); 
}