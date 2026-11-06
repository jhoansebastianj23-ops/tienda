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

import com.example.tienda.dto.CreateUserRequestDTO;
import com.example.tienda.dto.HttpGlobalResponseDTO;
import com.example.tienda.dto.UpdateRoleRequestDTO;
import com.example.tienda.dto.UpdateStatusRequestDTO;
import com.example.tienda.dto.UserDTO;
import com.example.tienda.enums.RoleEnum;
import com.example.tienda.security.RequiresRole;
import com.example.tienda.service.UsersService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UsersController {

    private final UsersService usersService;

    @GetMapping("/me")
    @RequiresRole({ RoleEnum.ADMIN, RoleEnum.CLIENTE, RoleEnum.VENDEDOR })
    public ResponseEntity<HttpGlobalResponseDTO<UserDTO>> getMe(HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            return usersService.getMe(userId);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping
    @RequiresRole({ RoleEnum.ADMIN })
    public ResponseEntity<HttpGlobalResponseDTO<List<UserDTO>>> getUsers() {
        try {
            return usersService.getUsers();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PostMapping
    @RequiresRole({ RoleEnum.ADMIN })
    public ResponseEntity<HttpGlobalResponseDTO<UserDTO>> createUser(@RequestBody CreateUserRequestDTO request) {
        try {
            return usersService.createUser(request);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PatchMapping("/{id}/role")
    @RequiresRole({ RoleEnum.ADMIN })
    public ResponseEntity<HttpGlobalResponseDTO<UserDTO>> updateRole(@PathVariable Long id,
            @RequestBody UpdateRoleRequestDTO request) {
        try {
            return usersService.updateRole(id, request);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PatchMapping("/{id}/status")
    @RequiresRole({ RoleEnum.ADMIN })
    public ResponseEntity<HttpGlobalResponseDTO<UserDTO>> updateStatus(@PathVariable Long id,
            @RequestBody UpdateStatusRequestDTO request) {
        try {
            return usersService.updateStatus(id, request);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}
