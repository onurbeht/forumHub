package com.forumHub.dtos.topico;

import java.time.LocalDateTime;

import com.forumHub.domain.enums.Status;
import com.forumHub.dtos.curso.CursoResponseDto;

public record TopicoResponseAllDto(
        Long id,
        String idAutor,
        String titulo,
        String mensagem,
        LocalDateTime dataCriacao,
        boolean ativo,
        Status status,
        CursoResponseDto curso,
        int numeroRespostas) {

}
