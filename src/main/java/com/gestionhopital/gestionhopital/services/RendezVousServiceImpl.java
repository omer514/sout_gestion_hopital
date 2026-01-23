package com.gestionhopital.gestionhopital.services;

import com.gestionhopital.gestionhopital.entities.RendezVous;
import com.gestionhopital.gestionhopital.entities.StatusRDV;
import com.gestionhopital.gestionhopital.repositories.RendezVousRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class RendezVousServiceImpl implements RendezVousService {
    
    private final EmailService emailService;
    private final RendezVousRepository rendezvousRepository;

    @Override
    public void saveRendezVous(RendezVous rdv) throws Exception {
        // On vérifie s'il existe un RDV actif (qui n'est PAS annulé) pour ce créneau
        boolean occupe = rendezvousRepository.existsByMedecinAndDateAndHeureAndStatusNot(
                rdv.getMedecin(), 
                rdv.getDate(), 
                rdv.getHeure(), 
                StatusRDV.ANNULE
        );
        
        if (occupe) {
            throw new Exception("Erreur : Ce médecin a déjà un rendez-vous actif à ce créneau.");
        }

        if (rdv.getStatus() == null) {
            rdv.setStatus(StatusRDV.EN_ATTENTE);
        }
        rendezvousRepository.save(rdv);
    }

    @Override
    public List<RendezVous> findAll() {
        return rendezvousRepository.findAll();
    }

    @Override
    public void confirmerRendezVous(Long id) {
        RendezVous rdv = rendezvousRepository.findById(id).orElse(null);
        if (rdv != null) {
            rdv.setStatus(StatusRDV.CONFIRME);
            rendezvousRepository.save(rdv);

            // Préparation de l'email de confirmation
            String subject = "Confirmation de votre Rendez-vous";
            String content = "Bonjour " + rdv.getPatient().getNom() + " " + rdv.getPatient().getPrenom() + ",\n\n" +
                             "Votre médecin, Dr. " + rdv.getMedecin().getNom() + 
                             ", a validé votre rendez-vous pour le " + rdv.getDate() + 
                             " à " + rdv.getHeure() + ".\n\n" +
                             "Cordialement, l'administration de l'Hôpital.";
            
            emailService.sendSimpleEmail(rdv.getPatient().getEmail(), subject, content);
        }
    }

    @Override
    public void annulerRendezVous(Long id) {
        // Version simple sans motif (pour les tests ou annulation rapide)
        RendezVous rdv = rendezvousRepository.findById(id).orElse(null);
        if (rdv != null) {
            rdv.setStatus(StatusRDV.ANNULE);
            rendezvousRepository.save(rdv);
        }
    }

    @Override
    public void annulerRendezVousAvecMotif(Long id, String motif) {
        RendezVous rdv = rendezvousRepository.findById(id).orElse(null);
        if (rdv != null) {
            rdv.setStatus(StatusRDV.ANNULE);
            rdv.setMotifAnnulation(motif);
            rendezvousRepository.save(rdv);

            // Envoi de l'email d'annulation avec explication
            String subject = "Annulation de votre Rendez-vous";
            String content = "Bonjour " + rdv.getPatient().getNom() + ",\n\n" +
                             "Nous vous informons que votre rendez-vous prévu le " + rdv.getDate() + 
                             " a été annulé par le médecin.\n" +
                             "Motif : " + motif + "\n\n" +
                             "Veuillez contacter l'accueil pour reprogrammer une visite.\n" +
                             "Cordialement.";
            
            emailService.sendSimpleEmail(rdv.getPatient().getEmail(), subject, content);
        }
    }

    @Override
    public void marquerPresent(Long id) {
        RendezVous rdv = rendezvousRepository.findById(id).orElse(null);
        if (rdv != null) {
            rdv.setPresent(true);
            rendezvousRepository.save(rdv);
        }
    }
}