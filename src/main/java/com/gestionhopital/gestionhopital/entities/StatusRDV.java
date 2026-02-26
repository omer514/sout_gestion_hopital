package com.gestionhopital.gestionhopital.entities;

public enum StatusRDV {
    EN_ATTENTE,   // Créé, non validé
    CONFIRME,   // Validé par le médecin/admin
    EN_COURS,     
    ANNULE,       // Annulé par l'une des parties
    TERMINE,      // Consultation terminée, dossier clôturé
    A_VALIDER,  // <-- Nouveau statut pour la demande patient

}