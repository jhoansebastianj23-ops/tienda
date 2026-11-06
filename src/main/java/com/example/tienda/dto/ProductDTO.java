package com.example.tienda.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ProductDTO {
    /**
     * Id del producto
     */
    private Long id;

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
     * Id de la categoría
     */
    private Long categoryId;

    /**
     * Nombre de la categoría
     */
    private String categoryName;

    /**
     * Id del vendedor dueño del producto
     */
    private Long sellerId;

    /**
     * Nombre de usuario del vendedor
     */
    private String sellerName;
}
