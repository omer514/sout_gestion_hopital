package com.gestionhopital.gestionhopital.entities;

public enum StatusRDV {
    EN_ATTENTE,   // Le rendez-vous est créé mais pas encore validé
    CONFIRME,     // Le médecin a validé le rendez-vous
    ANNULE        // Le rendez-vous est annulé
}