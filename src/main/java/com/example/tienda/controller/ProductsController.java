package com.example.tienda.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.tienda.dto.HttpGlobalResponseDTO;
import com.example.tienda.dto.ProductDTO;
import com.example.tienda.dto.ProductRequestDTO;
import com.example.tienda.enums.RoleEnum;
import com.example.tienda.security.RequiresRole;
import com.example.tienda.service.ProductsService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductsController {

    private final ProductsService productsService;

    // Público (lo deja pasar el filtro)
    @GetMapping
    public ResponseEntity<HttpGlobalResponseDTO<List<ProductDTO>>> getProducts(
            @RequestParam(required = false) Long categoryId) {
        try {
            return productsService.getProducts(categoryId);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    // Público (lo deja pasar el filtro)
    @GetMapping("/{id}")
    public ResponseEntity<HttpGlobalResponseDTO<ProductDTO>> getProduct(@PathVariable Long id) {
        try {
            return productsService.getProduct(id);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/mine")
    @RequiresRole({ RoleEnum.VENDEDOR })
    public ResponseEntity<HttpGlobalResponseDTO<List<ProductDTO>>> getMyProducts(HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            return productsService.getMyProducts(userId);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PostMapping
    @RequiresRole({ RoleEnum.VENDEDOR })
    public ResponseEntity<HttpGlobalResponseDTO<ProductDTO>> createProduct(HttpServletRequest request,
            @RequestBody ProductRequestDTO body) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            return productsService.createProduct(userId, body);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping("/{id}")
    @RequiresRole({ RoleEnum.VENDEDOR, RoleEnum.ADMIN })
    public ResponseEntity<HttpGlobalResponseDTO<ProductDTO>> updateProduct(HttpServletRequest request,
            @PathVariable Long id, @RequestBody ProductRequestDTO body) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            Long rolId = (Long) request.getAttribute("rolId");
            return productsService.updateProduct(id, userId, rolId, body);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @DeleteMapping("/{id}")
    @RequiresRole({ RoleEnum.VENDEDOR, RoleEnum.ADMIN })
    public ResponseEntity<HttpGlobalResponseDTO<ProductDTO>> deleteProduct(HttpServletRequest request,
            @PathVariable Long id) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            Long rolId = (Long) request.getAttribute("rolId");
            return productsService.deleteProduct(id, userId, rolId);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}
