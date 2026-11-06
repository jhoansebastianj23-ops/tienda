package com.example.tienda.dto;

import com.example.tienda.enums.OrderStatusEnum;

import lombok.Data;

@Data
public class OrderStatusRequestDTO {
    /**
     * Nuevo estado del pedido
     */
    private OrderStatusEnum status;
}
