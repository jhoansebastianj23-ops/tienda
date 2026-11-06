package com.example.tienda.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.tienda.dto.CategoryDTO;
import com.example.tienda.dto.CategoryRequestDTO;
import com.example.tienda.dto.HttpGlobalResponseDTO;
import com.example.tienda.entity.Categories;
import com.example.tienda.repository.CategoriesRepository;
import com.example.tienda.repository.ProductsRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoriesService {

    private final CategoriesRepository categoriesRepository;

    private final ProductsRepository productsRepository;

    public ResponseEntity<HttpGlobalResponseDTO<List<CategoryDTO>>> getCategories() {
        List<CategoryDTO> listCategories = new ArrayList<>();

        for (Categories c : categoriesRepository.findAll()) {
            listCategories.add(toCategoryDTO(c));
        }

        return answer(HttpStatus.OK, "Categories found", listCategories);
    }

    public ResponseEntity<HttpGlobalResponseDTO<CategoryDTO>> createCategory(CategoryRequestDTO request) {
        if (request.getName() == null || request.getName().isBlank()) {
            return answer(HttpStatus.BAD_REQUEST, "Name is required", null);
        }

        if (categoriesRepository.findByName(request.getName()).isPresent()) {
            return answer(HttpStatus.CONFLICT, "Category name is already in use", null);
        }

        Categories category = new Categories();
        category.setName(request.getName());
        categoriesRepository.save(category);

        return answer(HttpStatus.CREATED, "Category created successfully", toCategoryDTO(category));
    }

    public ResponseEntity<HttpGlobalResponseDTO<CategoryDTO>> deleteCategory(Long id) {
        Optional<Categories> found = categoriesRepository.findById(id);

        if (found.isEmpty()) {
            return answer(HttpStatus.NOT_FOUND, "Category not found", null);
        }

        if (productsRepository.existsByCategoryId(id)) {
            return answer(HttpStatus.CONFLICT, "Category has products and cannot be deleted", null);
        }

        categoriesRepository.delete(found.get());

        return answer(HttpStatus.OK, "Category deleted successfully", null);
    }

    private CategoryDTO toCategoryDTO(Categories category) {
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(category.getId());
        categoryDTO.setName(category.getName());
        return categoryDTO;
    }

    private <T> ResponseEntity<HttpGlobalResponseDTO<T>> answer(HttpStatus status, String message, T data) {
        HttpGlobalResponseDTO<T> response = new HttpGlobalResponseDTO<>();
        response.setMessage(message);
        response.setData(data);
        return ResponseEntity.status(status).body(response);
    }
}
