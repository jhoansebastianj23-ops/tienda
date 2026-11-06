package com.example.tienda.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.tienda.dto.CreateUserRequestDTO;
import com.example.tienda.dto.HttpGlobalResponseDTO;
import com.example.tienda.dto.UpdateRoleRequestDTO;
import com.example.tienda.dto.UpdateStatusRequestDTO;
import com.example.tienda.dto.UserDTO;
import com.example.tienda.entity.Roles;
import com.example.tienda.entity.Users;
import com.example.tienda.repository.RolesRepository;
import com.example.tienda.repository.UsersRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsersService {

    private final UsersRepository usersRepository;

    private final RolesRepository rolesRepository;

    private final PasswordEncoder passwordEncoder;

    public ResponseEntity<HttpGlobalResponseDTO<UserDTO>> getMe(Long userId) {
        Optional<Users> found = usersRepository.findById(userId);

        if (found.isEmpty()) {
            return answer(HttpStatus.NOT_FOUND, "User not found", null);
        }

        return answer(HttpStatus.OK, "User found", toUserDTO(found.get()));
    }

    public ResponseEntity<HttpGlobalResponseDTO<List<UserDTO>>> getUsers() {
        List<UserDTO> listUsers = new ArrayList<>();

        for (Users u : usersRepository.findAll()) {
            listUsers.add(toUserDTO(u));
        }

        return answer(HttpStatus.OK, "Users found", listUsers);
    }

    public ResponseEntity<HttpGlobalResponseDTO<UserDTO>> createUser(CreateUserRequestDTO request) {
        if (isBlank(request.getUsername()) || isBlank(request.getEmail()) || isBlank(request.getPassword())
                || request.getRolId() == null) {
            return answer(HttpStatus.BAD_REQUEST, "Username, email, password and rolId are required", null);
        }

        if (usersRepository.findByUsername(request.getUsername()).isPresent()) {
            return answer(HttpStatus.CONFLICT, "Username is already in use", null);
        }

        if (usersRepository.findByEmail(request.getEmail()).isPresent()) {
            return answer(HttpStatus.CONFLICT, "Email is already in use", null);
        }

        Optional<Roles> rol = rolesRepository.findById(request.getRolId());

        if (rol.isEmpty()) {
            return answer(HttpStatus.NOT_FOUND, "Role not found", null);
        }

        Users user = new Users();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setActive(true);
        user.setRoles(rol.get());

        usersRepository.save(user);

        return answer(HttpStatus.CREATED, "User created successfully", toUserDTO(user));
    }

    public ResponseEntity<HttpGlobalResponseDTO<UserDTO>> updateRole(Long id, UpdateRoleRequestDTO request) {
        if (request.getRolId() == null) {
            return answer(HttpStatus.BAD_REQUEST, "rolId is required", null);
        }

        Optional<Users> found = usersRepository.findById(id);

        if (found.isEmpty()) {
            return answer(HttpStatus.NOT_FOUND, "User not found", null);
        }

        Optional<Roles> rol = rolesRepository.findById(request.getRolId());

        if (rol.isEmpty()) {
            return answer(HttpStatus.NOT_FOUND, "Role not found", null);
        }

        Users user = found.get();
        user.setRoles(rol.get());
        usersRepository.save(user);

        return answer(HttpStatus.OK, "User role updated successfully", toUserDTO(user));
    }

    public ResponseEntity<HttpGlobalResponseDTO<UserDTO>> updateStatus(Long id, UpdateStatusRequestDTO request) {
        if (request.getActive() == null) {
            return answer(HttpStatus.BAD_REQUEST, "active is required", null);
        }

        Optional<Users> found = usersRepository.findById(id);

        if (found.isEmpty()) {
            return answer(HttpStatus.NOT_FOUND, "User not found", null);
        }

        Users user = found.get();
        user.setActive(request.getActive());
        usersRepository.save(user);

        return answer(HttpStatus.OK, "User status updated successfully", toUserDTO(user));
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
