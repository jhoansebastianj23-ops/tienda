package com.example.tienda.filter;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.tienda.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtValidationFilter extends OncePerRequestFilter {

    private static final String BASE_PATH = "/api/v1/tienda";

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            writeError(response, "Header Authorization is missing in the request");
            return;
        }

        String token = authHeader.replace("Bearer ", "");
        String username;
        Long userId;
        Long rolId;

        try {
            if (!jwtService.istokenValid(token)) {
                writeError(response, "Token is invalid or expired");
                return;
            }
            username = jwtService.exctractUsername(token);
            userId = jwtService.extractUserId(token);
            rolId = jwtService.extractRolId(token);
        } catch (Exception e) {
            writeError(response, "Validation failed");
            return;
        }

        request.setAttribute("username", username);
        request.setAttribute("userId", userId);
        request.setAttribute("rolId", rolId);

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();

        // Todo /auth/** es público
        if (path.startsWith(BASE_PATH + "/auth/")) {
            return true;
        }

        // Catálogo público: GET /products y GET /products/{id}
        if (method.equals("GET") && path.equals(BASE_PATH + "/products")) {
            return true;
        }

        if (method.equals("GET") && path.matches(BASE_PATH + "/products/\\d+")) {
            return true;
        }

        return false;
    }

    private void writeError(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"message\": \"" + message + "\"}");
    }
}
