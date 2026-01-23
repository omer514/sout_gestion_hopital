package com.gestionhopital.gestionhopital.ApiController;

import lombok.Data;
import java.util.List;

@Data
public class UserRequestDTO {
    private String username;
    private String password;
    private String confirmPassword;
    private String email;
    private List<String> roles;
}