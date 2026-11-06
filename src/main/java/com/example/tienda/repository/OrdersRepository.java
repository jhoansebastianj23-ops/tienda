package com.example.tienda.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tienda.entity.Orders;

public interface OrdersRepository extends JpaRepository<Orders, Long> {
    List<Orders> findByClientId(Long clientId);

    List<Orders> findByProductSellerId(Long sellerId);

    boolean existsByProductId(Long productId);
}
