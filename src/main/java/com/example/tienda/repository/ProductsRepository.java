package com.example.tienda.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tienda.entity.Products;

public interface ProductsRepository extends JpaRepository<Products, Long> {
    List<Products> findByCategoryId(Long categoryId);

    List<Products> findBySellerId(Long sellerId);

    boolean existsByCategoryId(Long categoryId);
}
