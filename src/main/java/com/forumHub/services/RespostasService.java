package com.forumHub.services;

import org.springframework.stereotype.Service;

import com.forumHub.domain.entities.Respostas;
import com.forumHub.dtos.RespostaResponseDto;

@Service
public class RespostasService {

    public RespostaResponseDto mapToDto(Respostas respostas) {
        return new RespostaResponseDto(
                respostas.getId(),
                respostas.getMensagem(),
                respostas.getDataCriacao(),
                respostas.getTopico().getId(),
                respostas.getAutor().getId());
    }

}
