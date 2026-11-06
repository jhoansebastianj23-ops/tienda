package com.example.tienda.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ProductRequestDTO {
    /**
     * Nombre del producto
     */
    private String name;

    /**
     * Descripción del producto
     */
    private String description;

    /**
     * Precio del producto
     */
    private BigDecimal price;

    /**
     * Unidades disponibles
     */
    private Integer stock;

    /**
     * Id de la categoría del producto
     */
    private Long categoryId;
}
