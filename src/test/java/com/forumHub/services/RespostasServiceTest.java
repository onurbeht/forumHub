package com.forumHub.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.forumHub.domain.entities.Respostas;
import com.forumHub.domain.entities.Topico;
import com.forumHub.domain.entities.Usuario;
import com.forumHub.domain.enums.Status;
import com.forumHub.domain.repositories.RespostasRepository;
import com.forumHub.dtos.respostas.RespostaResponseDto;

@ExtendWith(MockitoExtension.class)
public class RespostasServiceTest {

    @Mock
    RespostasRepository respostasRepository;

    @InjectMocks
    RespostasService respostasService;

    Respostas resposta;

    RespostaResponseDto respostaResponseDto;

    Usuario usuario;

    Topico topico;

    @BeforeEach
    void setUp() {

        usuario = Usuario.builder().id("Id usuario").username("Username").build();

        topico = Topico.builder().id(99L)
                .titulo("Titulo topico")
                .mensagem("Mensagem topico")
                .dataCriacao(LocalDateTime.now().minusDays(1))
                .ativo(true)
                .status(Status.ABERTA)
                .autor(usuario)
                .build();

        resposta = Respostas.builder()
                .dataCriacao(LocalDateTime.now())
                .autor(usuario)
                .topico(topico)
                .build();
    }

    @Test
    @DisplayName("Should return a RespostasResponseDto")
    void RespostasService_mapToDto() {

        resposta.setId("id resposta");
        resposta.setMensagem("Mensagem resposta");

        respostaResponseDto = new RespostaResponseDto(resposta.getId(), resposta.getMensagem(),
                resposta.getDataCriacao(), resposta.getTopico().getId(), resposta.getAutor().getId());

        assertEquals(respostaResponseDto, respostasService.mapToDto(resposta));
    }

    @Test
    @DisplayName("Should create a Resposta, return RespostasResponseDto")
    void CursoService_createResposta_returnRespostasResponseDto() {

        String respostaInput = "Nova mensagem";

        respostaResponseDto = new RespostaResponseDto("id nova resposta", respostaInput, resposta.getDataCriacao(),
                resposta.getTopico().getId(), resposta.getAutor().getId());

        resposta.setId("id nova resposta");
        resposta.setMensagem(respostaInput);

        RespostasService respostaServiceSpy = spy(respostasService);

        when(respostasRepository
                .save(
                        Respostas.builder()
                                .mensagem(respostaInput)
                                .dataCriacao(LocalDateTime.now())
                                .autor(usuario)
                                .topico(topico)
                                .build()))
                .thenReturn(resposta);

        RespostaResponseDto response = respostaServiceSpy.createResposta(topico, usuario, respostaInput);

        assertEquals(response, respostaResponseDto);

        verify(respostasRepository, times(1))
                .save(Respostas.builder()
                        .mensagem(respostaInput)
                        .dataCriacao(LocalDateTime.now())
                        .autor(usuario)
                        .topico(topico)
                        .build());
        verify(respostaServiceSpy, times(1)).mapToDto(resposta);
    }

}
