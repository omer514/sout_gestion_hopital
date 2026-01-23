package com.gestionhopital.gestionhopital.services;

import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage; // Classe Spring pour créer un mail textuel
import org.springframework.mail.javamail.JavaMailSender; // L'interface de Spring qui gère l'envoi SMTP
import org.springframework.stereotype.Service; // Annotation pour que Spring gère cette classe comme un Service

@Service // Indique à Spring de créer un "Bean" (objet) pour cette classe au démarrage
@AllArgsConstructor // Génère un constructeur avec l'argument 'mailSender' (Injection de dépendance)
public class EmailServiceImpl implements EmailService {

    // On déclare l'outil d'envoi d'email (sera configuré via application.yml)
    private final JavaMailSender mailSender;

    /**
     * Implémentation de la méthode d'envoi.
     */
    @Override
    public void sendSimpleEmail(String to, String subject, String content) {
        try {
            // 1. Création d'une instance de message simple
            SimpleMailMessage message = new SimpleMailMessage();
            
            // 2. Définition de l'expéditeur (ton email Gmail configuré)
            message.setFrom("aidegoomer5@gmail.com"); 
            
            // 3. Définition du destinataire (l'email de l'agent récupéré en base)
            message.setTo(to);
            
            // 4. Définition de l'objet de l'email
            message.setSubject(subject);
            
            // 5. Définition du contenu textuel (Username et Password "en clair")
            message.setText(content);
            
            // 6. Commande d'envoi via le protocole SMTP
            mailSender.send(message);
            
            // Affiche un message dans ta console pour confirmer que tout va bien
            System.out.println("LOG SUCCESS : Email envoyé avec succès à " + to);
            
        } catch (Exception e) {
            // Si le mail ne part pas (pas de wifi, mauvais mot de passe d'application, etc.)
            // On affiche l'erreur dans la console pour déboguer
            System.err.println("LOG ERROR : Échec de l'envoi de l'email : " + e.getMessage());
        }
    }
}