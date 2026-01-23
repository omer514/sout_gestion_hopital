package com.gestionhopital.gestionhopital.security; // Vérifie ton package

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // On récupère le chemin absolu du projet
        String projectPath = System.getProperty("user.dir");
        
        registry.addResourceHandler("/photos/**")
                .addResourceLocations("file:" + projectPath + "/src/main/resources/static/photos/");
    }
}