package com.forumHub.dtos.usuario;

import java.util.List;

import com.forumHub.dtos.respostas.RespostaResponseDto;

public record UsuarioResponseDto(
                String id,
                String username,
                List<RespostaResponseDto> respostas) {

}
