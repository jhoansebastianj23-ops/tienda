package com.example.tienda.service;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.tienda.dto.HttpGlobalResponseDTO;
import com.example.tienda.dto.LoginRequestDTO;
import com.example.tienda.dto.LoginResponseDTO;
import com.example.tienda.dto.RegisterRequestDTO;
import com.example.tienda.dto.UserDTO;
import com.example.tienda.entity.Roles;
import com.example.tienda.entity.Users;
import com.example.tienda.enums.RoleEnum;
import com.example.tienda.repository.RolesRepository;
import com.example.tienda.repository.UsersRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    /**
     * Repositorio de usuarios
     */
    private final UsersRepository usersRepository;

    /**
     * Repositorio de roles
     */
    private final RolesRepository rolesRepository;

    /**
     * Encriptador de contraseñas
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * Servicio de jwt
     */
    private final JwtService jwtService;

    public ResponseEntity<HttpGlobalResponseDTO<UserDTO>> register(RegisterRequestDTO request) {
        if (isBlank(request.getUsername()) || isBlank(request.getEmail()) || isBlank(request.getPassword())) {
            return answer(HttpStatus.BAD_REQUEST, "Username, email and password are required", null);
        }

        if (usersRepository.findByUsername(request.getUsername()).isPresent()) {
            return answer(HttpStatus.CONFLICT, "Username is already in use", null);
        }

        if (usersRepository.findByEmail(request.getEmail()).isPresent()) {
            return answer(HttpStatus.CONFLICT, "Email is already in use", null);
        }

        // Siempre se registra como CLIENTE
        Optional<Roles> rol = rolesRepository.findById(RoleEnum.CLIENTE.getId());

        if (rol.isEmpty()) {
            return answer(HttpStatus.NOT_FOUND, "Role CLIENTE does not exist", null);
        }

        Users user = new Users();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setActive(true);
        user.setRoles(rol.get());

        usersRepository.save(user);

        return answer(HttpStatus.CREATED, "User registered successfully", toUserDTO(user));
    }

    public ResponseEntity<HttpGlobalResponseDTO<LoginResponseDTO>> login(LoginRequestDTO request) {
        if (isBlank(request.getUser()) || isBlank(request.getPassword())) {
            return answer(HttpStatus.BAD_REQUEST, "User and password are required", null);
        }

        // Busca primero por username y si no lo encuentra, por email
        Optional<Users> found = usersRepository.findByUsername(request.getUser());

        if (found.isEmpty()) {
            found = usersRepository.findByEmail(request.getUser());
        }

        if (found.isEmpty()) {
            return answer(HttpStatus.UNAUTHORIZED, "Invalid user or password", null);
        }

        Users user = found.get();

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return answer(HttpStatus.UNAUTHORIZED, "Invalid user or password", null);
        }

        if (!Boolean.TRUE.equals(user.getActive())) {
            return answer(HttpStatus.UNAUTHORIZED, "User is inactive", null);
        }

        return answer(HttpStatus.OK, "Login successful", buildLoginResponse(user));
    }

    public ResponseEntity<HttpGlobalResponseDTO<LoginResponseDTO>> refreshToken(String token) {
        if (!jwtService.istokenValid(token)) {
            return answer(HttpStatus.UNAUTHORIZED, "Token is invalid or expired", null);
        }

        Long userId = jwtService.extractUserId(token);
        Optional<Users> found = usersRepository.findById(userId);

        if (found.isEmpty() || !Boolean.TRUE.equals(found.get().getActive())) {
            return answer(HttpStatus.UNAUTHORIZED, "User not found or inactive", null);
        }

        return answer(HttpStatus.OK, "Token refreshed successfully", buildLoginResponse(found.get()));
    }

    private LoginResponseDTO buildLoginResponse(Users user) {
        String jwt = jwtService.generateToken(user.getId(), user.getUsername(), user.getRoles().getId());

        LoginResponseDTO loginResponse = new LoginResponseDTO();
        loginResponse.setJwt(jwt);
        loginResponse.setUser(toUserDTO(user));
        return loginResponse;
    }

    private UserDTO toUserDTO(Users user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setRole(user.getRoles().getName());
        userDTO.setActive(user.getActive());
        return userDTO;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private <T> ResponseEntity<HttpGlobalResponseDTO<T>> answer(HttpStatus status, String message, T data) {
        HttpGlobalResponseDTO<T> response = new HttpGlobalResponseDTO<>();
        response.setMessage(message);
        response.setData(data);
        return ResponseEntity.status(status).body(response);
    }
}
