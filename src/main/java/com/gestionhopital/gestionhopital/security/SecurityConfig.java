package com.gestionhopital.gestionhopital.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        
        // 1. CONFIGURATION DE BASE
        httpSecurity.csrf(csrf -> csrf.disable()); // Désactivé pour le développement
        httpSecurity.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));

        // 2. FILTRE JWT (Placé avant le filtre d'authentification standard)
        httpSecurity.addFilterBefore(new JWTAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);

        // 3. GESTION DES AUTORISATIONS (ORDONNÉES)
        httpSecurity.authorizeHttpRequests(auth -> auth
            
            // --- ACCÈS PUBLICS ---
            .requestMatchers("/login/**", "/webjars/**", "/css/**", "/js/**", "/images/**", "/photos/**").permitAll()
            .requestMatchers("/api/login/**").permitAll() 

            // --- ACCÈS LABORATOIRE ---
            .requestMatchers("/labo/**").hasAnyAuthority("ADMIN", "LABO")

            // --- ACCÈS MÉDICAL (PARTAGÉ : ADMIN, MÉDECIN, ACCUEIL) ---
            // On regroupe ici tout ce qui concerne le suivi patient et les rendez-vous
            .requestMatchers(
                "/admin/rendezvous/**", 
                "/admin/marquerPresent/**", 
                "/admin/confirmerRDV/**", 
                "/admin/annulerRDV/**",
                "/admin/formRendezVous/**",
                "/admin/saveRendezVous/**",
                "/admin/demarrerConsultation/**",
                "/admin/saveConsultation/**",
                "/admin/voirConsultation/**",
                "/admin/patientDetails/**", // Débloqué pour le médecin ici
                "/medecin/**"               // Toutes les actions spécifiques médecin
            ).hasAnyAuthority("ADMIN", "ACCUEIL", "MEDECIN")

            // --- ACCÈS RÉCEPTION (ADMIN + ACCUEIL uniquement) ---
            // Création et modification de la fiche patient de base
            .requestMatchers("/admin/formPatients/**", "/admin/save/**", "/admin/delete/**").hasAnyAuthority("ADMIN", "ACCUEIL")

            // --- ACCÈS DASHBOARD (TOUS UTILISATEURS CONNECTÉS) ---
            .requestMatchers("/dashboard/**").authenticated()

            // --- ACCÈS API REST (ADMIN uniquement) ---
            .requestMatchers("/api/**").hasAuthority("ADMIN") 

            // --- ACCÈS ADMINISTRATION GÉNÉRALE (STRICT ADMIN) ---
            // Toute autre URL commençant par /admin est réservée au super-admin
            .requestMatchers("/admin/**").hasAuthority("ADMIN")
            
            // --- TOUT LE RESTE ---
            .anyRequest().authenticated()
        );

        // 4. GESTION DES ERREURS D'ACCÈS (Redirection vs API Error)
        httpSecurity.exceptionHandling(eh -> eh
            .authenticationEntryPoint((request, response, authException) -> {
                if (request.getServletPath().startsWith("/api")) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                } else {
                    response.sendRedirect("/login");
                }
            })
        );

        // 5. CONFIGURATION DU FORMULAIRE DE CONNEXION
        httpSecurity.formLogin(form -> form
            .loginPage("/login")
            .defaultSuccessUrl("/dashboard", true)
            .permitAll()
        );

        // 6. CONFIGURATION DE LA DÉCONNEXION
        httpSecurity.logout(logout -> logout
            .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
            .logoutSuccessUrl("/login?logout")
            .permitAll()
        );

        return httpSecurity.build();
    }
}