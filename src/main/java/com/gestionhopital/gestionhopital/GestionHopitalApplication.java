package com.gestionhopital.gestionhopital;

import com.gestionhopital.gestionhopital.services.AccountService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GestionHopitalApplication {

	public static void main(String[] args) {
		SpringApplication.run(GestionHopitalApplication.class, args);
	}

	@Bean
CommandLineRunner commandLineRunner(AccountService accountService) {
    return args -> {
        // 1. Création des rôles un par un
        String[] roles = {"ADMIN", "MEDECIN", "ACCUEIL", "PATIENT"};
        for (String role : roles) {
            try { accountService.addNewRole(role); } catch (Exception e) { }
        }

        // 2. Création des utilisateurs
        try {
            accountService.addNewUser("admin", "1234", "admin@gmail.com", "1234", "default.png");
            accountService.addNewUser("medecin", "1234medecin", "med@gmail.com", "1234", "default.png");
            accountService.addNewUser("accueil", "1234", "acc@gmail.com", "1234", "default.png");
        } catch (Exception e) { }

        // 3. ASSOCIATION FORCEE (C'est ici que se remplit ta table vide !)
        try {
            accountService.addRoleToUser("admin", "ADMIN");
            accountService.addRoleToUser("medecin", "MEDECIN");
            accountService.addRoleToUser("accueil", "ACCUEIL");
            
            System.out.println("Données d'initialisation créées avec succès !");
        } catch (Exception e) {
            // Si les données existent déjà, on ignore l'erreur au démarrage
            System.out.println("Les données de test existent déjà, passage à l'étape suivante.");
        }
    };
}

}
