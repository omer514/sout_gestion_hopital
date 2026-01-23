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
        httpSecurity.csrf(csrf -> csrf.disable());

        // 1. FILTRE JWT
        httpSecurity.addFilterBefore(new JWTAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);

        // 2. Gestion de la session
        httpSecurity.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));

        httpSecurity.authorizeHttpRequests(auth -> auth
            // Ressources publiques
            .requestMatchers("/login/**", "/webjars/**", "/css/**", "/js/**", "/images/**", "/photos/**").permitAll()
            .requestMatchers("/api/login/**").permitAll() 
            
            // API réservée à l'ADMIN
            .requestMatchers("/api/**").hasAuthority("ADMIN") 

            // --- EXCEPTIONS POUR ACCUEIL ET MEDECIN (DOIVENT ÊTRE AVANT /admin/**) ---
            
            // Autoriser spécifiquement les actions de gestion de rendez-vous pour l'Accueil, le Médecin et l'Admin
            .requestMatchers(
                "/admin/rendezvous/**", 
                "/admin/marquerPresent/**", 
                "/admin/confirmerRDV/**", 
                "/admin/annulerRDV/**",
                "/admin/formRendezVous/**",
                "/admin/saveRendezVous/**"
            ).hasAnyAuthority("ADMIN", "ACCUEIL", "MEDECIN")
            
            // Autoriser la gestion des patients pour l'Accueil et l'Admin
            .requestMatchers("/admin/formPatients/**", "/admin/save/**").hasAnyAuthority("ADMIN", "ACCUEIL")
            
            // 3. TOUT le reste de /admin est strictement réservé à l'ADMIN
            .requestMatchers("/admin/**").hasAuthority("ADMIN")
            
            .anyRequest().authenticated()
        );

        // 3. Empêcher la redirection HTML pour les requêtes API
        httpSecurity.exceptionHandling(eh -> eh
            .authenticationEntryPoint((request, response, authException) -> {
                if (request.getServletPath().startsWith("/api")) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                } else {
                    response.sendRedirect("/login");
                }
            })
        );

        // 4. FORM LOGIN AVEC REDIRECTION PERSONNALISÉE SELON LE RÔLE
        httpSecurity.formLogin(form -> form
            .loginPage("/login")
            .successHandler((request, response, authentication) -> {
                boolean isMedecin = authentication.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("MEDECIN"));
                
                if (isMedecin) {
                    response.sendRedirect("/admin/rendezvous"); 
                } else {
                    response.sendRedirect("/dashboard");
                }
            })
            .permitAll()
        );

        httpSecurity.logout(logout -> logout.logoutRequestMatcher(new AntPathRequestMatcher("/logout")).permitAll());

        return httpSecurity.build();
    }
}