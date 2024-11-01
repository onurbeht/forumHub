package com.forumHub.dtos.usuario;

import jakarta.validation.constraints.NotBlank;

public record CreateUsuarioDto(
        @NotBlank String username,
        @NotBlank String password,
        @NotBlank String confirmPassword) {

}
