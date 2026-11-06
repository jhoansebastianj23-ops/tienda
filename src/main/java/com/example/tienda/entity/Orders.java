package com.example.tienda.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.tienda.enums.OrderStatusEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "orders")
public class Orders {
    /**
     * Id del pedido
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Cliente que hizo el pedido
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", referencedColumnName = "id")
    private Users client;

    /**
     * Producto pedido
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Products product;

    /**
     * Cantidad pedida
     */
    @Column(name = "quantity")
    private Integer quantity;

    /**
     * Total del pedido (precio x cantidad)
     */
    @Column(name = "total")
    private BigDecimal total;

    /**
     * Estado del pedido
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private OrderStatusEnum status;

    /**
     * Fecha de creación del pedido
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
