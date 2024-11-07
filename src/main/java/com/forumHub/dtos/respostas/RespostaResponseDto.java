package com.forumHub.dtos.respostas;

import java.time.LocalDateTime;

public record RespostaResponseDto(

        String id,
        String mensagem,
        LocalDateTime dataCriacao,
        Long idTopico,
        String idAutor

) {

}
