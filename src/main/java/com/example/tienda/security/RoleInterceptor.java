package com.example.tienda.security;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import com.example.tienda.enums.RoleEnum;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RoleInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        // Verifica que el handler sea un método del controller y no cualquier otra cosa
        if (!(handler instanceof HandlerMethod method)) {
            return true; // Si no es un endpoint no nos importa
        }

        // Busca la anotación @RequiresRole en el método
        RequiresRole annotation = method.getMethodAnnotation(RequiresRole.class);

        if (annotation == null) {
            return true; // Endpoint sin restricción de rol
        }

        Object rol = request.getAttribute("rolId");

        if (!(rol instanceof Long rolId)) {
            writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "User is not authenticated");
            return false; // mochamos la petición, no alcanza a llegar al controller
        }

        // Verifica si el rol esta dentro de los roles permitidos
        boolean hasRole = false;
        for (RoleEnum role : annotation.value()) {
            if (role.getId().equals(rolId)) {
                hasRole = true;
            }
        }

        if (!hasRole) {
            writeError(response, HttpServletResponse.SC_FORBIDDEN,
                    "The user does not have permission to perform this action");
            return false;
        }

        // Si pasa todas las validaciones, permite continuar al controller
        return true;
    }

    private void writeError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write("{\"message\": \"" + message + "\"}");
    }
}
