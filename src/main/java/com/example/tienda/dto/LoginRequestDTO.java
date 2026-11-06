package com.example.tienda.dto;

import lombok.Data;

@Data
public class LoginRequestDTO {
    /**
     * Puede ser el username o el email del usuario
     */
    private String user;

    /**
     * Contraseña del usuario
     */
    private String password;
}
