package com.example.tienda.dto;

import lombok.Data;

@Data
public class CreateUserRequestDTO {
    /**
     * Nombre de usuario
     */
    private String username;

    /**
     * Correo del usuario
     */
    private String email;

    /**
     * Contraseña del usuario
     */
    private String password;

    /**
     * Id del rol del usuario
     */
    private Long rolId;
}
