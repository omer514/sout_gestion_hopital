package com.gestionhopital.gestionhopital.services;

import com.gestionhopital.gestionhopital.entities.AppUser;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {
    private AccountService accountService;

    @Override
public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    AppUser appUser = accountService.loadUserByUsername(username);
    if (appUser == null) throw new UsernameNotFoundException("Utilisateur non trouvé");

    // On transforme la liste des rôles en tableau de String (authorities)
    String[] authorities = appUser.getRoles().stream()
            .map(r -> r.getRoleName())
            .toArray(String[]::new);

    return User.withUsername(appUser.getUsername())
            .password(appUser.getPassword())
            .authorities(authorities) // <--- ICI on utilise .authorities() au lieu de .roles()
            .build();
}
}
