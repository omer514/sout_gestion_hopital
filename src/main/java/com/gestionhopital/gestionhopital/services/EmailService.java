package com.gestionhopital.gestionhopital.services;

/**
 * Interface pour la gestion des services d'emails.
 */
public interface EmailService {
    
    /**
     * Signature de la méthode d'envoi d'un email simple.
     * @param to : l'adresse email de destination (celle de l'agent).
     * @param subject : le sujet du message.
     * @param content : le texte du message (contiendra le login et password).
     */
    void sendSimpleEmail(String to, String subject, String content);
}
