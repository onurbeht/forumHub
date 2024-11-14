package com.forumHub.dtos.curso;

import com.forumHub.domain.enums.Categoria;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

public record CursoRequestDto(
                @NotBlank String nome,
                @Valid Categoria categoria) {

}
