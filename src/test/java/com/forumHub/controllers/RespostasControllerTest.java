package com.forumHub.controllers;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.forumHub.domain.entities.Curso;
import com.forumHub.domain.entities.Topico;
import com.forumHub.domain.entities.Usuario;
import com.forumHub.domain.enums.Categoria;
import com.forumHub.domain.enums.Status;
import com.forumHub.dtos.respostas.RespostaResponseDto;
import com.forumHub.dtos.respostas.RespostasRequestDto;
import com.forumHub.services.RespostasService;
import com.forumHub.services.TopicoService;
import com.forumHub.services.UsuarioService;

@SpringBootTest
@AutoConfigureMockMvc
public class RespostasControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    RespostasService respostasService;

    @MockBean
    UsuarioService usuarioService;

    @MockBean
    TopicoService topicoService;

    RespostasRequestDto respostasRequestDto;

    RespostaResponseDto respostaResponseDto;

    Topico topico;

    Usuario usuario;

    Curso curso;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder().id(UUID.randomUUID().toString()).username("username").password("password").build();

        curso = Curso.builder().id(1L).nome("nome do curso").categoria(Categoria.BACKEND).build();

        topico = Topico.builder()
                .id(1L)
                .titulo("Titulo do topico")
                .mensagem("Mensagem")
                .dataCriacao(LocalDateTime.now().minusHours(1))
                .ativo(true)
                .status(Status.ABERTA)
                .autor(usuario)
                .curso(curso)
                .build();

        respostasRequestDto = new RespostasRequestDto("Nova resposta");
    }

    @Test
    @DisplayName("Should create a new Resposta to a Topico, return RespostasResponseDto")
    @WithMockUser
    void RespostasController_createResposta_returnRespostaResponseDto() throws JsonProcessingException, Exception {

        Long idTopico = 1L;
        respostaResponseDto = new RespostaResponseDto("idResposta", respostasRequestDto.resposta(),
                LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS),
                idTopico, usuario.getId());

        when(topicoService.findByIdAndAtivo(idTopico)).thenReturn(Optional.of(topico));
        when(usuarioService.findByUsername(usuario.getUsername())).thenReturn(Optional.of(usuario));
        when(usuarioService.getPrincipal()).thenReturn(usuario.getUsername());
        when(respostasService.createResposta(topico, usuario, respostasRequestDto.resposta()))
                .thenReturn(respostaResponseDto);

        ResultActions response = mockMvc.perform(
                post("/respostas/novo/{idTopico}", idTopico)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(respostasRequestDto)));

        response
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(respostaResponseDto.id()))
                .andExpect(jsonPath("$.mensagem").value(respostaResponseDto.mensagem()))
                .andExpect(jsonPath("$.dataCriacao",
                        Matchers.is(respostaResponseDto.dataCriacao().truncatedTo(ChronoUnit.MILLIS).toString())))
                .andExpect(jsonPath("$.idTopico").value(idTopico))
                .andExpect(jsonPath("$.idAutor").value(usuario.getId()));

        verify(topicoService, times(1)).findByIdAndAtivo(idTopico);
        verifyNoMoreInteractions(topicoService);

        verify(respostasService, times(1)).createResposta(topico, usuario, respostasRequestDto.resposta());
        verifyNoMoreInteractions(respostasService);

        verify(usuarioService, times(1)).findByUsername(usuario.getUsername());
        verify(usuarioService, times(1)).getPrincipal();
        verifyNoMoreInteractions(usuarioService);

    }

    @Test
    @DisplayName("Should not create a new Resposta to a Topico when idTopico is invalid, return error message")
    @WithMockUser
    void RespostasController_createResposta_errorCase1() throws JsonProcessingException, Exception {

        Long idTopico1 = 10L;
        String idTopico2 = "abc";

        String expectedResponse1 = "Id do topico informado não existe. Verifique e tente novamente";
        String expectedResponse2 = "Parametro informado está em um formato invalido, verifique e tente novamente";

        when(topicoService.findByIdAndAtivo(idTopico1)).thenReturn(Optional.empty());

        ResultActions response1 = mockMvc.perform(
                post("/respostas/novo/{idTopico}", idTopico1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(respostasRequestDto)));

        ResultActions response2 = mockMvc.perform(
                post("/respostas/novo/{idTopico}", idTopico2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(respostasRequestDto)));

        response1
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value(expectedResponse1));

        response2
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("idTopico"))
                .andExpect(jsonPath("$.message").value(expectedResponse2));

        verify(topicoService, times(1)).findByIdAndAtivo(idTopico1);
        verifyNoMoreInteractions(topicoService);
        verifyNoInteractions(usuarioService);
        verifyNoInteractions(respostasService);

    }

    @Test
    @DisplayName("Should not create a new Resposta to a Topico when request data is invalid, return error message")
    @WithMockUser
    void RespostasController_createResposta_errorCase2() throws JsonProcessingException, Exception {

        Long idTopico = 1L;
        respostasRequestDto = new RespostasRequestDto("");

        String expectedResponse = "must not be blank";

        ResultActions response = mockMvc.perform(
                post("/respostas/novo/{idTopico}", idTopico)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(respostasRequestDto)));

        response
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.[0].field").value("resposta"))
                .andExpect(jsonPath("$.[0].message").value(expectedResponse));

        verifyNoInteractions(topicoService);
        verifyNoInteractions(usuarioService);
        verifyNoInteractions(respostasService);

    }

}
