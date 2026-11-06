package com.example.tienda.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.tienda.dto.HttpGlobalResponseDTO;
import com.example.tienda.dto.LoginRequestDTO;
import com.example.tienda.dto.LoginResponseDTO;
import com.example.tienda.dto.RegisterRequestDTO;
import com.example.tienda.dto.UserDTO;
import com.example.tienda.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    /**
     * Servicio de auth
     */
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<HttpGlobalResponseDTO<UserDTO>> register(@RequestBody RegisterRequestDTO request) {
        try {
            return authService.register(request);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<HttpGlobalResponseDTO<LoginResponseDTO>> login(@RequestBody LoginRequestDTO request) {
        try {
            return authService.login(request);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<HttpGlobalResponseDTO<LoginResponseDTO>> refreshToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            HttpGlobalResponseDTO<LoginResponseDTO> response = new HttpGlobalResponseDTO<>();
            response.setMessage("Token is invalid");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        String token = authHeader.replace("Bearer ", "");

        try {
            return authService.refreshToken(token);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}
