package com.gestionhopital.gestionhopital.ApiController;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.gestionhopital.gestionhopital.security.SecConstants;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
public class AuthRestController {
    private AuthenticationManager authenticationManager;

    @PostMapping("/api/login")
    public Map<String, String> login(@RequestBody Map<String, String> user) {
        Authentication auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(user.get("username"), user.get("password"))
        );

        String jwt = JWT.create()
                .withSubject(auth.getName())
                .withExpiresAt(new Date(System.currentTimeMillis() + SecConstants.EXPIRATION))
                .withArrayClaim("roles", auth.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority).toArray(String[]::new))
                .sign(Algorithm.HMAC256(SecConstants.SECRET));

        Map<String, String> idToken = new HashMap<>();
        idToken.put("access-token", jwt);
        return idToken;
    }
}