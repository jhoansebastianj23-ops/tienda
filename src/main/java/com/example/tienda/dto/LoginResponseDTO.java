package com.example.tienda.dto;

import lombok.Data;

@Data
public class LoginResponseDTO {
    /**
     * Token jwt generado
     */
    private String jwt;

    /**
     * Datos del usuario
     */
    private UserDTO user;
}
