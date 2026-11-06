package com.example.tienda.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.tienda.dto.CategoryDTO;
import com.example.tienda.dto.CategoryRequestDTO;
import com.example.tienda.dto.HttpGlobalResponseDTO;
import com.example.tienda.enums.RoleEnum;
import com.example.tienda.security.RequiresRole;
import com.example.tienda.service.CategoriesService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoriesController {

    private final CategoriesService categoriesService;

    @GetMapping
    @RequiresRole({ RoleEnum.ADMIN, RoleEnum.CLIENTE, RoleEnum.VENDEDOR })
    public ResponseEntity<HttpGlobalResponseDTO<List<CategoryDTO>>> getCategories() {
        try {
            return categoriesService.getCategories();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PostMapping
    @RequiresRole({ RoleEnum.ADMIN })
    public ResponseEntity<HttpGlobalResponseDTO<CategoryDTO>> createCategory(
            @RequestBody CategoryRequestDTO request) {
        try {
            return categoriesService.createCategory(request);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @DeleteMapping("/{id}")
    @RequiresRole({ RoleEnum.ADMIN })
    public ResponseEntity<HttpGlobalResponseDTO<CategoryDTO>> deleteCategory(@PathVariable Long id) {
        try {
            return categoriesService.deleteCategory(id);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}
