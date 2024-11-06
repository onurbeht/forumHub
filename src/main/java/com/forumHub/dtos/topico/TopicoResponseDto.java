package com.forumHub.dtos.topico;

import com.forumHub.domain.enums.Status;
import com.forumHub.dtos.RespostaResponseDto;
import com.forumHub.dtos.curso.CursoResponseDto;

import java.time.LocalDateTime;
import java.util.List;

public record TopicoResponseDto(
        Long id,
        String idAutor,
        String titulo,
        String mensagem,
        LocalDateTime dataCriacao,
        Status status,
        CursoResponseDto curso,
        List<RespostaResponseDto> respostas

) {

}
