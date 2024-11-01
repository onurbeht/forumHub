package com.forumHub.dtos.usuario;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDto(
                @NotBlank String username,
                @NotBlank String password) {

}
