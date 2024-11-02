package com.forumHub.dtos;

import java.time.LocalDateTime;

public record RespostaResponseDto(

                String id,
                String mensagem,
                LocalDateTime dataCriacao,
                Long idTopico,
                String idAutor

) {

}
