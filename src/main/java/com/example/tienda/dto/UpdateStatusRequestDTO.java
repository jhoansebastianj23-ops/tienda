package com.example.tienda.dto;

import lombok.Data;

@Data
public class UpdateStatusRequestDTO {
    /**
     * true para activar el usuario, false para desactivarlo
     */
    private Boolean active;
}
