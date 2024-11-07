package com.forumHub.services;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.forumHub.domain.entities.Respostas;
import com.forumHub.domain.entities.Topico;
import com.forumHub.domain.entities.Usuario;
import com.forumHub.domain.repositories.RespostasRepository;
import com.forumHub.dtos.respostas.RespostaResponseDto;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RespostasService {

    private final RespostasRepository respostasRepository;

    public RespostaResponseDto mapToDto(Respostas respostas) {
        return new RespostaResponseDto(
                respostas.getId(),
                respostas.getMensagem(),
                respostas.getDataCriacao(),
                respostas.getTopico().getId(),
                respostas.getAutor().getId());
    }

    @Transactional
    public RespostaResponseDto createResposta(Topico topico, Usuario usuario, String resposta) {
        Respostas newResposta = Respostas.builder()
                .mensagem(resposta)
                .dataCriacao(LocalDateTime.now())
                .topico(topico)
                .autor(usuario)
                .build();

        return mapToDto(respostasRepository.save(newResposta));
    }

}
