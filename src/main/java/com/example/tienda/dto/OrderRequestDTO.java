package com.example.tienda.dto;

import lombok.Data;

@Data
public class OrderRequestDTO {
    /**
     * Id del producto que se quiere comprar
     */
    private Long productId;

    /**
     * Cantidad que se quiere comprar
     */
    private Integer quantity;
}
