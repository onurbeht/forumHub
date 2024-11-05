package com.forumHub.dtos.curso;

import com.forumHub.domain.enums.Categoria;

public record CursoResponseDto(
        Long id,
        String nome,
        Categoria categoria

) {

}
