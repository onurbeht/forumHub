package com.forumHub.dtos.topico;

public record TopicoUpdateDto(
        String titulo,
        String mensagem,
        Long idCurso) {

}
