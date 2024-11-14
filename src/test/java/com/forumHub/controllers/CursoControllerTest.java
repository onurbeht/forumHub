package com.forumHub.controllers;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

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
import com.forumHub.domain.enums.Categoria;
import com.forumHub.dtos.curso.CursoRequestDto;
import com.forumHub.dtos.curso.CursoResponseDto;
import com.forumHub.services.CursoService;

@SpringBootTest
@AutoConfigureMockMvc
public class CursoControllerTest {

        @Autowired
        MockMvc mockMvc;

        @Autowired
        ObjectMapper mapper;

        @MockBean
        CursoService cursoService;

        CursoRequestDto requestDto;
        CursoResponseDto responseDto;

        @BeforeEach
        void setUp() {
                requestDto = new CursoRequestDto("Curso teste", Categoria.BACKEND);
                responseDto = new CursoResponseDto(1L, "Curso teste", Categoria.BACKEND);
        }

        @Test
        @DisplayName("Should create a new Curso and return CursoResponseDto")
        @WithMockUser
        void CursoController_createCurso_returnCursoResponseDto() throws JsonProcessingException, Exception {

                // when(tokenService.validateToken(tokenJwt)).thenReturn(usuario.getUsername());
                // when(usuarioRepository.findByUsername(usuario.getUsername())).thenReturn(Optional.of(usuario));

                when(cursoService.findbyName(requestDto.nome())).thenReturn(Optional.empty());
                when(cursoService.createCurso(requestDto)).thenReturn(responseDto);

                ResultActions response = mockMvc.perform(
                                post("/cursos/novo")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                // .header("Authorization", tokenJwt)
                                                .content(mapper.writeValueAsString(requestDto)));

                response
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").value(responseDto.id()))
                                .andExpect(jsonPath("$.nome").value(responseDto.nome()))
                                .andExpect(jsonPath("$.categoria").value(responseDto.categoria().toString()));

                // verify(tokenService, times(1)).validateToken(tokenJwt);
                // verify(usuarioRepository, times(1)).findByUsername(usuario.getUsername());
                verify(cursoService, times(1)).findbyName(requestDto.nome());
                verify(cursoService, times(1)).createCurso(requestDto);

                // verifyNoMoreInteractions(tokenService, usuarioRepository, cursoService);
                verifyNoMoreInteractions(cursoService);

        }

        @Test
        @DisplayName("Should NOT create a new Curso when Curso already exist and return 'Curso já cadastrado'")
        @WithMockUser
        void CursoController_createCurso_errorCase1() throws JsonProcessingException, Exception {

                Curso curso = Curso.builder()
                                .id(responseDto.id())
                                .nome(responseDto.nome())
                                .categoria(responseDto.categoria())
                                .build();

                String expectedResponse = "Curso já cadastrado";

                when(cursoService.findbyName(requestDto.nome())).thenReturn(Optional.of(curso));

                ResultActions response = mockMvc.perform(
                                post("/cursos/novo")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(mapper.writeValueAsString(requestDto)));

                response
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$").value(expectedResponse));

                verify(cursoService, times(1)).findbyName(requestDto.nome());

                verifyNoMoreInteractions(cursoService);

        }

        @Test
        @DisplayName("Should NOT create a new Curso when data in request body is invalid and return error message")
        @WithMockUser
        void CursoController_createCurso_errorCase2() throws JsonProcessingException, Exception {

                CursoRequestDto req1 = new CursoRequestDto("", requestDto.categoria());

                String expectedResponse1 = "must not be blank";
                String expectedResponse2 = "must be a value in [INFRA, FRONTEND, BACKEND, SEGURANCA, MOBILE]";

                ResultActions response1 = mockMvc.perform(
                                post("/cursos/novo")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(mapper.writeValueAsString(req1)));

                ResultActions response2 = mockMvc.perform(
                                post("/cursos/novo")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content("{\"nome\": \"nome curso\", \"categoria\": \"invalido\"}"));

                response1
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.[0].field").value("nome"))
                                .andExpect(jsonPath("$.[0].message").value(expectedResponse1));

                response2
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.field").value("categoria"))
                                .andExpect(jsonPath("$.message").value(expectedResponse2));

                verifyNoMoreInteractions(cursoService);

        }

        @Test
        @DisplayName("Should return a Curso by Id")
        @WithMockUser
        void CursoController_getById_returnCurso() throws Exception {
                Long id = 1L;

                Curso curso = Curso.builder().id(responseDto.id()).nome(responseDto.nome())
                                .categoria(responseDto.categoria()).build();

                when(cursoService.findbyId(id)).thenReturn(Optional.of(curso));
                when(cursoService.toDto(curso)).thenReturn(responseDto);

                ResultActions response = mockMvc
                                .perform(get("/cursos/{id}", id).contentType(MediaType.APPLICATION_JSON));

                response
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(responseDto.id()))
                                .andExpect(jsonPath("$.nome").value(responseDto.nome()))
                                .andExpect(jsonPath("$.categoria").value(responseDto.categoria().toString()));
        }

        @Test
        @DisplayName("Should NOT return a Curso by Id, when Id doesn't exist")
        @WithMockUser
        void CursoController_getById_errorCase1() throws Exception {
                Long id = 5L;

                when(cursoService.findbyId(id)).thenReturn(Optional.empty());

                ResultActions response = mockMvc
                                .perform(get("/cursos/{id}", id).contentType(MediaType.APPLICATION_JSON));

                response
                                .andExpect(status().isNotFound());

        }

        @Test
        @DisplayName("Should NOT return a Curso by Id, when Id is invalid")
        @WithMockUser
        void CursoController_getById_errorCase2() throws Exception {
                String id1 = "abc";

                ResultActions response = mockMvc
                                .perform(get("/cursos/{id}", id1).contentType(MediaType.APPLICATION_JSON));

                response
                                .andExpect(status().isBadRequest());

        }

}
