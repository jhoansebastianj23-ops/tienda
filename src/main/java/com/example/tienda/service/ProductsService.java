package com.example.tienda.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.tienda.dto.HttpGlobalResponseDTO;
import com.example.tienda.dto.ProductDTO;
import com.example.tienda.dto.ProductRequestDTO;
import com.example.tienda.entity.Categories;
import com.example.tienda.entity.Products;
import com.example.tienda.entity.Users;
import com.example.tienda.enums.RoleEnum;
import com.example.tienda.repository.CategoriesRepository;
import com.example.tienda.repository.OrdersRepository;
import com.example.tienda.repository.ProductsRepository;
import com.example.tienda.repository.UsersRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductsService {

    private final ProductsRepository productsRepository;

    private final CategoriesRepository categoriesRepository;

    private final UsersRepository usersRepository;

    private final OrdersRepository ordersRepository;

    public ResponseEntity<HttpGlobalResponseDTO<List<ProductDTO>>> getProducts(Long categoryId) {
        List<Products> productsFound;

        if (categoryId != null) {
            productsFound = productsRepository.findByCategoryId(categoryId);
        } else {
            productsFound = productsRepository.findAll();
        }

        return answer(HttpStatus.OK, "Products found", toProductDTOList(productsFound));
    }

    public ResponseEntity<HttpGlobalResponseDTO<ProductDTO>> getProduct(Long id) {
        Optional<Products> found = productsRepository.findById(id);

        if (found.isEmpty()) {
            return answer(HttpStatus.NOT_FOUND, "Product not found", null);
        }

        return answer(HttpStatus.OK, "Product found", toProductDTO(found.get()));
    }

    public ResponseEntity<HttpGlobalResponseDTO<List<ProductDTO>>> getMyProducts(Long sellerId) {
        List<Products> productsFound = productsRepository.findBySellerId(sellerId);

        return answer(HttpStatus.OK, "Products found", toProductDTOList(productsFound));
    }

    public ResponseEntity<HttpGlobalResponseDTO<ProductDTO>> createProduct(Long sellerId, ProductRequestDTO request) {
        String error = validate(request);

        if (error != null) {
            return answer(HttpStatus.BAD_REQUEST, error, null);
        }

        Optional<Categories> category = categoriesRepository.findById(request.getCategoryId());

        if (category.isEmpty()) {
            return answer(HttpStatus.NOT_FOUND, "Category not found", null);
        }

        // El vendedor sale del token, no del body
        Optional<Users> seller = usersRepository.findById(sellerId);

        if (seller.isEmpty()) {
            return answer(HttpStatus.NOT_FOUND, "Seller not found", null);
        }

        Products product = new Products();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setCategory(category.get());
        product.setSeller(seller.get());

        productsRepository.save(product);

        return answer(HttpStatus.CREATED, "Product created successfully", toProductDTO(product));
    }

    public ResponseEntity<HttpGlobalResponseDTO<ProductDTO>> updateProduct(Long id, Long userId, Long rolId,
            ProductRequestDTO request) {
        Optional<Products> found = productsRepository.findById(id);

        if (found.isEmpty()) {
            return answer(HttpStatus.NOT_FOUND, "Product not found", null);
        }

        Products product = found.get();

        if (!canModify(product, userId, rolId)) {
            return answer(HttpStatus.FORBIDDEN, "You can only edit your own products", null);
        }

        String error = validate(request);

        if (error != null) {
            return answer(HttpStatus.BAD_REQUEST, error, null);
        }

        Optional<Categories> category = categoriesRepository.findById(request.getCategoryId());

        if (category.isEmpty()) {
            return answer(HttpStatus.NOT_FOUND, "Category not found", null);
        }

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setCategory(category.get());

        productsRepository.save(product);

        return answer(HttpStatus.OK, "Product updated successfully", toProductDTO(product));
    }

    public ResponseEntity<HttpGlobalResponseDTO<ProductDTO>> deleteProduct(Long id, Long userId, Long rolId) {
        Optional<Products> found = productsRepository.findById(id);

        if (found.isEmpty()) {
            return answer(HttpStatus.NOT_FOUND, "Product not found", null);
        }

        Products product = found.get();

        if (!canModify(product, userId, rolId)) {
            return answer(HttpStatus.FORBIDDEN, "You can only delete your own products", null);
        }

        if (ordersRepository.existsByProductId(id)) {
            return answer(HttpStatus.CONFLICT, "Product has orders and cannot be deleted", null);
        }

        productsRepository.delete(product);

        return answer(HttpStatus.OK, "Product deleted successfully", null);
    }

    /**
     * El ADMIN puede modificar cualquier producto, el VENDEDOR solo los suyos
     */
    private boolean canModify(Products product, Long userId, Long rolId) {
        if (RoleEnum.VENDEDOR.getId().equals(rolId)) {
            return product.getSeller().getId().equals(userId);
        }
        return true;
    }

    /**
     * Devuelve el mensaje de error o null si todo está bien
     */
    private String validate(ProductRequestDTO request) {
        if (request.getName() == null || request.getName().isBlank()) {
            return "Name is required";
        }

        if (request.getPrice() == null || request.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            return "Price must be greater than 0";
        }

        if (request.getStock() == null || request.getStock() < 0) {
            return "Stock must be 0 or greater";
        }

        if (request.getCategoryId() == null) {
            return "Category id is required";
        }

        return null;
    }

    private List<ProductDTO> toProductDTOList(List<Products> products) {
        List<ProductDTO> listProducts = new ArrayList<>();

        for (Products p : products) {
            listProducts.add(toProductDTO(p));
        }

        return listProducts;
    }

    private ProductDTO toProductDTO(Products product) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(product.getId());
        productDTO.setName(product.getName());
        productDTO.setDescription(product.getDescription());
        productDTO.setPrice(product.getPrice());
        productDTO.setStock(product.getStock());
        productDTO.setCategoryId(product.getCategory().getId());
        productDTO.setCategoryName(product.getCategory().getName());
        productDTO.setSellerId(product.getSeller().getId());
        productDTO.setSellerName(product.getSeller().getUsername());
        return productDTO;
    }

    private <T> ResponseEntity<HttpGlobalResponseDTO<T>> answer(HttpStatus status, String message, T data) {
        HttpGlobalResponseDTO<T> response = new HttpGlobalResponseDTO<>();
        response.setMessage(message);
        response.setData(data);
        return ResponseEntity.status(status).body(response);
    }
}
