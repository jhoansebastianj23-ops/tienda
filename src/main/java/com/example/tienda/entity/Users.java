package com.example.tienda.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "users")
public class Users {
    /**
     * Id del usuario
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Nombre de usuario
     */
    @Column(name = "username")
    private String username;

    /**
     * Correo del usuario
     */
    @Column(name = "email")
    private String email;

    /**
     * Contraseña del usuario (encriptada)
     */
    @Column(name = "password")
    private String password;

    /**
     * Indica si el usuario está activo
     */
    @Column(name = "active")
    private Boolean active;

    /**
     * Rol del usuario
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rol_id", referencedColumnName = "id")
    private Roles roles;
}
