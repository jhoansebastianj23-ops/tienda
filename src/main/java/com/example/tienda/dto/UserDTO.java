package com.example.tienda.dto;

import lombok.Data;

@Data
public class UserDTO {
    /**
     * Id del usuario
     */
    private Long id;

    /**
     * Nombre de usuario
     */
    private String username;

    /**
     * Correo del usuario
     */
    private String email;

    /**
     * Nombre del rol del usuario
     */
    private String role;

    /**
     * Indica si el usuario está activo
     */
    private Boolean active;
}
