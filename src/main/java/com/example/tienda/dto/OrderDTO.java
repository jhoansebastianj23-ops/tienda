package com.example.tienda.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.tienda.enums.OrderStatusEnum;

import lombok.Data;

@Data
public class OrderDTO {
    /**
     * Id del pedido
     */
    private Long id;

    /**
     * Id del cliente
     */
    private Long clientId;

    /**
     * Id del producto
     */
    private Long productId;

    /**
     * Nombre del producto
     */
    private String productName;

    /**
     * Cantidad pedida
     */
    private Integer quantity;

    /**
     * Total del pedido
     */
    private BigDecimal total;

    /**
     * Estado del pedido
     */
    private OrderStatusEnum status;

    /**
     * Fecha de creación del pedido
     */
    private LocalDateTime createdAt;
}
