package com.forumHub.dtos.topico;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TopicoRequestDto(
        @NotBlank String titulo,
        @NotBlank String mensagem,
        @NotNull Long idCurso) {

}
