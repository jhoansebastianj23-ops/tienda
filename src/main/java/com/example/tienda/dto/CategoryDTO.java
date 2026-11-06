package com.example.tienda.dto;

import lombok.Data;

@Data
public class CategoryDTO {
    /**
     * Id de la categoría
     */
    private Long id;

    /**
     * Nombre de la categoría
     */
    private String name;
}
