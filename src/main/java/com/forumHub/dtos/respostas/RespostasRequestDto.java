package com.forumHub.dtos.respostas;

import jakarta.validation.constraints.NotBlank;

public record RespostasRequestDto(
        @NotBlank String resposta) {
}
