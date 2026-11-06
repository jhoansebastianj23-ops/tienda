package com.example.tienda.entity;

import java.math.BigDecimal;

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
@Table(name = "products")
public class Products {
    /**
     * Id del producto
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Nombre del producto
     */
    @Column(name = "name")
    private String name;

    /**
     * Descripción del producto
     */
    @Column(name = "description")
    private String description;

    /**
     * Precio del producto
     */
    @Column(name = "price")
    private BigDecimal price;

    /**
     * Unidades disponibles
     */
    @Column(name = "stock")
    private Integer stock;

    /**
     * Categoría del producto
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private Categories category;

    /**
     * Vendedor dueño del producto
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", referencedColumnName = "id")
    private Users seller;
}
