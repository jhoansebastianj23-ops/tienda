package com.example.tienda.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.tienda.dto.HttpGlobalResponseDTO;
import com.example.tienda.dto.OrderDTO;
import com.example.tienda.dto.OrderRequestDTO;
import com.example.tienda.dto.OrderStatusRequestDTO;
import com.example.tienda.enums.RoleEnum;
import com.example.tienda.security.RequiresRole;
import com.example.tienda.service.OrdersService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrdersController {

    private final OrdersService ordersService;

    @PostMapping
    @RequiresRole({ RoleEnum.CLIENTE })
    public ResponseEntity<HttpGlobalResponseDTO<OrderDTO>> createOrder(HttpServletRequest request,
            @RequestBody OrderRequestDTO body) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            return ordersService.createOrder(userId, body);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/mine")
    @RequiresRole({ RoleEnum.CLIENTE })
    public ResponseEntity<HttpGlobalResponseDTO<List<OrderDTO>>> getMyOrders(HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            return ordersService.getMyOrders(userId);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/sales")
    @RequiresRole({ RoleEnum.VENDEDOR })
    public ResponseEntity<HttpGlobalResponseDTO<List<OrderDTO>>> getSales(HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            return ordersService.getSales(userId);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping
    @RequiresRole({ RoleEnum.ADMIN })
    public ResponseEntity<HttpGlobalResponseDTO<List<OrderDTO>>> getAllOrders() {
        try {
            return ordersService.getAllOrders();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PatchMapping("/{id}/status")
    @RequiresRole({ RoleEnum.VENDEDOR, RoleEnum.ADMIN })
    public ResponseEntity<HttpGlobalResponseDTO<OrderDTO>> updateStatus(HttpServletRequest request,
            @PathVariable Long id, @RequestBody OrderStatusRequestDTO body) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            Long rolId = (Long) request.getAttribute("rolId");
            return ordersService.updateStatus(id, userId, rolId, body);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PatchMapping("/{id}/cancel")
    @RequiresRole({ RoleEnum.CLIENTE })
    public ResponseEntity<HttpGlobalResponseDTO<OrderDTO>> cancelOrder(HttpServletRequest request,
            @PathVariable Long id) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            return ordersService.cancelOrder(id, userId);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}
