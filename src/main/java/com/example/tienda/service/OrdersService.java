package com.example.tienda.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.tienda.dto.HttpGlobalResponseDTO;
import com.example.tienda.dto.OrderDTO;
import com.example.tienda.dto.OrderRequestDTO;
import com.example.tienda.dto.OrderStatusRequestDTO;
import com.example.tienda.entity.Orders;
import com.example.tienda.entity.Products;
import com.example.tienda.entity.Users;
import com.example.tienda.enums.OrderStatusEnum;
import com.example.tienda.enums.RoleEnum;
import com.example.tienda.repository.OrdersRepository;
import com.example.tienda.repository.ProductsRepository;
import com.example.tienda.repository.UsersRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrdersService {

    private final OrdersRepository ordersRepository;

    private final ProductsRepository productsRepository;

    private final UsersRepository usersRepository;

    public ResponseEntity<HttpGlobalResponseDTO<OrderDTO>> createOrder(Long clientId, OrderRequestDTO request) {
        if (request.getProductId() == null || request.getQuantity() == null || request.getQuantity() <= 0) {
            return answer(HttpStatus.BAD_REQUEST, "productId and a quantity greater than 0 are required", null);
        }

        Optional<Products> productFound = productsRepository.findById(request.getProductId());

        if (productFound.isEmpty()) {
            return answer(HttpStatus.NOT_FOUND, "Product not found", null);
        }

        // El cliente sale del token, no del body
        Optional<Users> clientFound = usersRepository.findById(clientId);

        if (clientFound.isEmpty()) {
            return answer(HttpStatus.NOT_FOUND, "Client not found", null);
        }

        Products product = productFound.get();

        if (product.getStock() < request.getQuantity()) {
            return answer(HttpStatus.BAD_REQUEST, "Not enough stock for this product", null);
        }

        // El total lo calcula el servidor: precio x cantidad
        BigDecimal total = product.getPrice().multiply(BigDecimal.valueOf(request.getQuantity()));

        Orders order = new Orders();
        order.setClient(clientFound.get());
        order.setProduct(product);
        order.setQuantity(request.getQuantity());
        order.setTotal(total);
        order.setStatus(OrderStatusEnum.PENDIENTE);
        order.setCreatedAt(LocalDateTime.now());
        ordersRepository.save(order);

        // Descuenta el stock
        product.setStock(product.getStock() - request.getQuantity());
        productsRepository.save(product);

        return answer(HttpStatus.CREATED, "Order created successfully", toOrderDTO(order));
    }

    public ResponseEntity<HttpGlobalResponseDTO<List<OrderDTO>>> getMyOrders(Long clientId) {
        return answer(HttpStatus.OK, "Orders found", toOrderDTOList(ordersRepository.findByClientId(clientId)));
    }

    public ResponseEntity<HttpGlobalResponseDTO<List<OrderDTO>>> getSales(Long sellerId) {
        return answer(HttpStatus.OK, "Orders found",
                toOrderDTOList(ordersRepository.findByProductSellerId(sellerId)));
    }

    public ResponseEntity<HttpGlobalResponseDTO<List<OrderDTO>>> getAllOrders() {
        return answer(HttpStatus.OK, "Orders found", toOrderDTOList(ordersRepository.findAll()));
    }

    public ResponseEntity<HttpGlobalResponseDTO<OrderDTO>> updateStatus(Long orderId, Long userId, Long rolId,
            OrderStatusRequestDTO request) {
        if (request.getStatus() == null) {
            return answer(HttpStatus.BAD_REQUEST, "Status is required", null);
        }

        Optional<Orders> found = ordersRepository.findById(orderId);

        if (found.isEmpty()) {
            return answer(HttpStatus.NOT_FOUND, "Order not found", null);
        }

        Orders order = found.get();

        // El VENDEDOR solo cambia el estado de pedidos de sus productos
        if (RoleEnum.VENDEDOR.getId().equals(rolId) && !order.getProduct().getSeller().getId().equals(userId)) {
            return answer(HttpStatus.FORBIDDEN, "You can only change the status of orders for your own products",
                    null);
        }

        if (order.getStatus() == OrderStatusEnum.CANCELADO) {
            return answer(HttpStatus.BAD_REQUEST, "A cancelled order cannot be changed", null);
        }

        if (request.getStatus() == OrderStatusEnum.CANCELADO) {
            returnStock(order);
        }

        order.setStatus(request.getStatus());
        ordersRepository.save(order);

        return answer(HttpStatus.OK, "Order status updated successfully", toOrderDTO(order));
    }

    public ResponseEntity<HttpGlobalResponseDTO<OrderDTO>> cancelOrder(Long orderId, Long clientId) {
        Optional<Orders> found = ordersRepository.findById(orderId);

        if (found.isEmpty()) {
            return answer(HttpStatus.NOT_FOUND, "Order not found", null);
        }

        Orders order = found.get();

        if (!order.getClient().getId().equals(clientId)) {
            return answer(HttpStatus.FORBIDDEN, "You can only cancel your own orders", null);
        }

        if (order.getStatus() != OrderStatusEnum.PENDIENTE) {
            return answer(HttpStatus.BAD_REQUEST, "Only PENDIENTE orders can be cancelled", null);
        }

        returnStock(order);

        order.setStatus(OrderStatusEnum.CANCELADO);
        ordersRepository.save(order);

        return answer(HttpStatus.OK, "Order cancelled successfully", toOrderDTO(order));
    }

    /**
     * Devuelve al producto las unidades del pedido
     */
    private void returnStock(Orders order) {
        Products product = order.getProduct();
        product.setStock(product.getStock() + order.getQuantity());
        productsRepository.save(product);
    }

    private List<OrderDTO> toOrderDTOList(List<Orders> orders) {
        List<OrderDTO> listOrders = new ArrayList<>();

        for (Orders o : orders) {
            listOrders.add(toOrderDTO(o));
        }

        return listOrders;
    }

    private OrderDTO toOrderDTO(Orders order) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(order.getId());
        orderDTO.setClientId(order.getClient().getId());
        orderDTO.setProductId(order.getProduct().getId());
        orderDTO.setProductName(order.getProduct().getName());
        orderDTO.setQuantity(order.getQuantity());
        orderDTO.setTotal(order.getTotal());
        orderDTO.setStatus(order.getStatus());
        orderDTO.setCreatedAt(order.getCreatedAt());
        return orderDTO;
    }

    private <T> ResponseEntity<HttpGlobalResponseDTO<T>> answer(HttpStatus status, String message, T data) {
        HttpGlobalResponseDTO<T> response = new HttpGlobalResponseDTO<>();
        response.setMessage(message);
        response.setData(data);
        return ResponseEntity.status(status).body(response);
    }
}
